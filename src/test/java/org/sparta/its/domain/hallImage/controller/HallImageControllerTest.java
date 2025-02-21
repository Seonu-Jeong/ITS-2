package org.sparta.its.domain.hallImage.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hallImage.dto.HallImageRequest;
import org.sparta.its.domain.hallImage.dto.HallImageResponse;
import org.sparta.its.domain.hallImage.entity.HallImage;
import org.sparta.its.domain.hallImage.service.HallImageService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(
	controllers = HallImageController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthorizationFilter.class))
@WithMockUser(authorities = "ADMIN")
class HallImageControllerTest {

	@MockitoBean
	private HallImageService hallImageService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
	}

	@Test
	@DisplayName("공연장 이미지 단건 수정 요청 적상 작동")
	void updateHallImage() throws Exception {
		Hall hall = new Hall("잠실 경기장", "잠실", 400, true);
		Long hallId = 1L;
		ReflectionTestUtils.setField(hall, "id", hallId);

		String imageUrl = "imageUrl";
		Long hallImageId = 1L;
		HallImage hallImage = new HallImage(hall, imageUrl);
		ReflectionTestUtils.setField(hallImage, "id", hallImageId);

		MockMultipartFile multipartFile1 = new MockMultipartFile(
			"images",
			"test1.png",
			ImageFormat.HALL.toString(),
			"test1.png".getBytes(StandardCharsets.UTF_8));

		MockMultipartFile[] images = {multipartFile1};

		HallImageResponse.UpdateDto responseDto = HallImageResponse.UpdateDto.builder()
			.hallId(hallId)
			.hallImageId(hallImageId)
			.imageUrl(imageUrl).build();

		BDDMockito
			.given(hallImageService.updateHallImage(
				eq(hallId),
				eq(hallImageId),
				any(HallImageRequest.UpdateImageDto.class)))
			.willReturn(responseDto);

		mockMvc.perform(MockMvcRequestBuilders
				.multipart(HttpMethod.PATCH, "/halls/{hallId}/hallImages/{hallImageId}", hallId, hallImageId)
				.file(images[0])
				.param("imageFormat", ImageFormat.HALL.toString())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.hallId").value(responseDto.getHallId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.hallImageId").value(responseDto.getHallImageId()));
	}
}
