package org.sparta.its.domain.concertimage.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concertimage.dto.ConcertImageRequest;
import org.sparta.its.domain.concertimage.dto.ConcertImageResponse;
import org.sparta.its.domain.concertimage.entity.ConcertImage;
import org.sparta.its.domain.concertimage.service.ConcertImageService;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.global.s3.ImageFormat;
import org.sparta.its.global.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(
	controllers = ConcertImageController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthorizationFilter.class))
@WithMockUser(authorities = "ADMIN")
class ConcertImageControllerTest {

	@MockitoBean
	private ConcertImageService concertImageService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("콘서트 이미지 단건 수정 요청 적상 작동")
	void updateConcertImage() throws Exception {
		Hall hall = new Hall("잠실 경기장", "잠실", 400, true);
		Long concertId = 1L;
		Concert concert = Concert.builder()
			.hall(hall)
			.title("아이유 콘서트")
			.singer("아이유")
			.startAt(LocalDate.parse("2025-01-31"))
			.endAt(LocalDate.parse("2025-02-28"))
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000).build();
		ReflectionTestUtils.setField(concert, "id", concertId);

		String imageUrl = "imageUrl";
		Long concertImageId = 1L;
		ConcertImage concertImage = new ConcertImage(concert, imageUrl);
		ReflectionTestUtils.setField(concertImage, "id", concertImageId);

		MockMultipartFile multipartFile1 = new MockMultipartFile(
			"images",
			"test1.png",
			ImageFormat.CONCERT.toString(),
			"test1.png".getBytes(StandardCharsets.UTF_8));

		MockMultipartFile multipartFile2 = new MockMultipartFile(
			"images",
			"test2.png",
			ImageFormat.CONCERT.toString(),
			"test2.png".getBytes(StandardCharsets.UTF_8));

		MockMultipartFile[] multipartFiles = {multipartFile1, multipartFile2};

		ConcertImageResponse.UpdateDto responseDto = ConcertImageResponse.UpdateDto.builder()
			.concertId(concertId)
			.concertImageId(concertImageId)
			.imageUrl(imageUrl).build();

		BDDMockito
			.given(concertImageService.updatedConcertImage(
				eq(concertId),
				eq(concertImageId),
				any(ConcertImageRequest.UpdateDto.class)))
			.willReturn(responseDto);

		mockMvc.perform
				(multipart(HttpMethod.PATCH,
					"/concerts/{concertId}/concertImages/{concertImageId}", concertId, concertImageId)
					.file(multipartFiles[0])
					.file(multipartFiles[1])
					.param("imageFormat", ImageFormat.CONCERT.toString())
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.concertId").value(concertId))
			.andExpect(MockMvcResultMatchers.jsonPath("$.concertImageId").value(concertImageId));
	}
}
