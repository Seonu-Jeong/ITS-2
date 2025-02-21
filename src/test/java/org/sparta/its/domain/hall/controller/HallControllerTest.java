package org.sparta.its.domain.hall.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.sparta.its.domain.hall.dto.HallRequest;
import org.sparta.its.domain.hall.dto.HallResponse;
import org.sparta.its.domain.hall.service.HallService;
import org.sparta.its.global.s3.ImageFormat;
import org.sparta.its.global.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = HallController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class))
@WithMockUser(authorities = "ADMIN")
class HallControllerTest {

	@MockitoBean
	HallService hallService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("공연장 등록 요청 정상 동작")
	void createHall() throws Exception {
		// given
		MockMultipartFile image1 = new MockMultipartFile(
			"images",
			"test1.png",
			ImageFormat.HALL.toString(),
			"test1.png".getBytes(StandardCharsets.UTF_8));

		MockMultipartFile image2 = new MockMultipartFile(
			"images",
			"test1.png",
			ImageFormat.HALL.toString(),
			"test1.png".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile[] images = {image1, image2};

		List<String> imageUrls = List.of("imageUrl1", "imageUrl2");
		HallResponse.CreatDto responseDto = HallResponse.CreatDto.builder()
			.hallId(1L)
			.hallName("올림픽 경기장")
			.capacity(400)
			.createdAt(LocalDateTime.parse("2025-01-03T20:00:00"))
			.imageUrls(imageUrls).build();
		// when, then
		BDDMockito.given(hallService.creatHall(any(HallRequest.CreateDto.class))).willReturn(responseDto);
		mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/halls")
				.file(images[0])
				.file(images[1])
				.param("name", "올림픽 경기장")
				.param("location", "잠실")
				.param("capacity", "400")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.hallId").value(1))
			.andExpect(jsonPath("$.hallName").value("올림픽 경기장"));
	}

	@Test
	@DisplayName("공연장 다건 조회 정상 조회")
	void getHalls() throws Exception {
		// given
		String name = "";
		String location = "";
		Pageable pageable = PageRequest.of(0, 2);

		HallResponse.ReadDto responseDto1 = HallResponse.ReadDto.builder()
			.hallId(1L)
			.hallName("공연장 이름1")
			.location("지역1")
			.capacity(400)
			.createdAt(LocalDateTime.parse("2025-01-03T20:00:00"))
			.modifiedAt(LocalDateTime.parse("2025-01-04T21:00:00"))
			.imageUrls(List.of("imageUrl1", "imageUrl2"))
			.isOpen(true).build();

		HallResponse.ReadDto responseDto2 = HallResponse.ReadDto.builder()
			.hallId(2L)
			.hallName("공연장 이름1")
			.location("지역1")
			.capacity(400)
			.createdAt(LocalDateTime.parse("2025-01-03T20:00:00"))
			.modifiedAt(LocalDateTime.parse("2025-01-04T21:00:00"))
			.imageUrls(List.of("imageUrl1", "imageUrl2"))
			.isOpen(true).build();

		HallResponse.ReadDto responseDto3 = HallResponse.ReadDto.builder()
			.hallId(3L)
			.hallName("공연장 이름1")
			.location("지역1")
			.capacity(400)
			.createdAt(LocalDateTime.parse("2025-01-03T20:00:00"))
			.modifiedAt(LocalDateTime.parse("2025-01-04T21:00:00"))
			.imageUrls(List.of("imageUrl1", "imageUrl2"))
			.isOpen(true).build();
		List<HallResponse.ReadDto> responseDtoList = List.of(responseDto1, responseDto2, responseDto3);

		// when, then
		BDDMockito.given(hallService.getHalls(name, location, pageable)).willReturn(responseDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/halls")
				.param("name", name)
				.param("location", location)
				.param("page", "0")
				.param("size", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].hallId").value(1))
			.andExpect(jsonPath("$[1].hallId").value(2))
			.andExpect(jsonPath("$[2].hallId").value(3));
	}

	@Test
	@DisplayName("공연장 상세 조회 정상 조회")
	void getDetailHall() throws Exception {
		// given
		Long hallId = 1L;
		HallResponse.ReadDto responseDto1 = HallResponse.ReadDto.builder()
			.hallId(1L)
			.hallName("공연장 이름1")
			.location("지역1")
			.capacity(400)
			.createdAt(LocalDateTime.parse("2025-01-03T20:00:00"))
			.modifiedAt(LocalDateTime.parse("2025-01-04T21:00:00"))
			.imageUrls(List.of("imageUrl1", "imageUrl2"))
			.isOpen(true).build();
		// when, then
		BDDMockito.given(hallService.getDetailHall(hallId)).willReturn(responseDto1);
		mockMvc.perform(get("/halls/{hallId}", hallId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.hallId").value(hallId))
			.andExpect(MockMvcResultMatchers.jsonPath("$.hallName").value("공연장 이름1"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.location").value("지역1"));
	}

	@Test
	@DisplayName("공연장 수정 요청 정상 작동")
	void updateHall() throws Exception {
		Long hallId = 1L;
		HallRequest.UpdateDto requestDto = new HallRequest.UpdateDto(
			"공연장 이름2",
			"지역2");

		HallResponse.UpdateDto responseDto = HallResponse.UpdateDto.builder()
			.hallId(1L)
			.hallName("공연장 이름1")
			.location("지역1")
			.capacity(400)
			.createdAt(LocalDateTime.parse("2025-01-03T20:00:00"))
			.modifiedAt(LocalDateTime.parse("2025-01-04T21:00:00"))
			.imageUrls(List.of("imageUrl1", "imageUrl2"))
			.isOpen(true).build();

		BDDMockito.given(hallService.updateHall(eq(hallId), any(HallRequest.UpdateDto.class))).willReturn(responseDto);
		mockMvc.perform(patch("/halls/{hallId}", hallId)
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.hallId").value(hallId))
			.andExpect(MockMvcResultMatchers.jsonPath("$.hallName").value("공연장 이름1"));
	}

	@Test
	@DisplayName("공연장 삭제 정상 작동")
	void deleteHall() throws Exception {
		Long hallId = 1L;

		HallResponse.DeleteDto responseDto = HallResponse.DeleteDto.builder()
			.message("공연장 삭제 완료").build();

		BDDMockito.given(hallService.deleteHall(hallId)).willReturn(responseDto);

		mockMvc.perform(delete("/halls/{hallId}", hallId)
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$.message").value("공연장 삭제 완료"));
	}
}
