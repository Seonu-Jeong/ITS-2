package org.sparta.its.domain.concert.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.sparta.its.domain.concert.dto.ConcertRequest;
import org.sparta.its.domain.concert.dto.ConcertResponse;
import org.sparta.its.domain.concert.service.ConcertService;
import org.sparta.its.domain.reservation.entity.ReservationStatus;
import org.sparta.its.global.security.JwtUtil;
import org.sparta.its.global.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
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
	value = ConcertController.class,
	includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthorizationFilter.class)
	}
)
@Import({
	// WebSecurityConfig.class
	// JwtAuthorizationFilter.class
})
@WithMockUser(authorities = "ADMIN")
public class ConcertControllerTest {

	@MockitoBean
	JwtUtil jwtUtil;

	@MockitoBean
	ConcertService concertService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
	}

	@Test
	@DisplayName("콘서트 등록시 성공적으로 JSON 응답을 반환")
	void CreateConcertTest() throws Exception {
		MockMultipartFile image1
			= new MockMultipartFile("images", "test1.jpg", "image/jpeg", "test1".getBytes());
		MockMultipartFile image2
			= new MockMultipartFile("images", "test2.jpg", "image/jpeg", "test2".getBytes());

		MockMultipartFile[] images = {image1, image2};

		ConcertRequest.CreateDto requestDto = new ConcertRequest.CreateDto(
			1L,
			"콘서트 제목",
			"가수 이름",
			LocalDate.parse("2025-01-21"),
			LocalDate.parse("2025-01-25"),
			LocalTime.parse("20:00"),
			LocalTime.parse("22:00"),
			110000,
			images);

		List<String> imageUrl = List.of("imageUrl1", "imageUr2");

		ConcertResponse.CreateDto responseDto = ConcertResponse.CreateDto.builder()
			.id(1L)
			.hallId(1L)
			.title("콘서트 제목")
			.singer("가수 이름")
			.startAt(LocalDate.parse("2025-01-21"))
			.endAt(LocalDate.parse("2025-01-25"))
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000)
			.images(imageUrl).build();

		// when
		BDDMockito.given(concertService.createConcert(any(ConcertRequest.CreateDto.class))).willReturn(responseDto);

		// then
		mockMvc
			.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/concerts")
				.file(images[0])
				.file(images[1])
				.param("hallId", requestDto.getHallId().toString())
				.param("title", requestDto.getTitle())
				.param("singer", requestDto.getSinger())
				.param("startAt", requestDto.getStartAt().toString())
				.param("endAt", requestDto.getEndAt().toString())
				.param("runningStartTime", requestDto.getRunningStartTime().toString())
				.param("runningEndTime", requestDto.getRunningEndTime().toString())
				.param("price", requestDto.getPrice().toString())
				.with(csrf())
				.contentType(MediaType.MULTIPART_FORM_DATA)

			)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.singer").value(responseDto.getSinger()));
	}

	@Test
	@DisplayName("콘서트명 과 가수이름으로 조회")
	void getConcerts() throws Exception {
		// given
		String singer = "";
		String concertTitle = "콘서트";
		String order = "ASC";
		List<String> imageUrls = List.of("imageUrl1", "imageUrl2");
		LocalDate concertEndAt = LocalDate.parse("2025-06-15");

		Long concertId1 = 1L;
		Long concertId2 = 2L;
		Long concertId3 = 3L;

		ConcertResponse.ReadDto readDto1 = ConcertResponse.ReadDto.builder()
			.id(concertId1)
			.hallName("올림픽 경기장")
			.title("아이유 콘서트")
			.singer("아이유")
			.startAt(LocalDate.parse("2025-05-11"))
			.endAt(concertEndAt)
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000)
			.images(imageUrls).build();

		ConcertResponse.ReadDto readDto2 = ConcertResponse.ReadDto.builder()
			.id(concertId2)
			.hallName("잠실 경기장")
			.title("제니 콘서트")
			.singer("제니")
			.startAt(LocalDate.parse("2025-05-12"))
			.endAt(concertEndAt)
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000)
			.images(imageUrls).build();

		ConcertResponse.ReadDto readDto3 = ConcertResponse.ReadDto.builder()
			.id(concertId3)
			.hallName("서울상암 경기장")
			.title("빅뱅 콘서트")
			.singer("빅뱅")
			.startAt(LocalDate.parse("2025-05-13"))
			.endAt(concertEndAt)
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000)
			.images(imageUrls).build();

		List<ConcertResponse.ReadDto> readDtoList = List.of(readDto1, readDto2, readDto3);
		Pageable pageable = PageRequest.of(0, 2);

		// when
		BDDMockito.given(concertService.getConcerts(singer, concertTitle, order, pageable)).willReturn(readDtoList);

		// then
		mockMvc.perform(get("/concerts")
				.param("singer", singer)
				.param("concertTitle", concertTitle)
				.param("order", order)
				.param("page", "0")
				.param("size", "2"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].singer").value("아이유"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[1].singer").value("제니"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].singer").value("빅뱅"));
	}

	@Test
	@DisplayName("콘서트 정보 날짜 정보 없이 수정 요청 정상 작동")
	void updateConcert_Test() throws Exception {
		// given
		String jwtToken = "test_token";
		List<String> imageUrls = List.of("imageUrl1", "imageUrl2");
		Long concertId = 1L;

		ConcertResponse.UpdateDto updateResponseDto = ConcertResponse.UpdateDto.builder()
			.id(concertId)
			.hallName("잠실 경기장")
			.title("아이유 콘서트 수정")
			.singer("아이유")
			.startAt(null)
			.endAt(null)
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000)
			.images(imageUrls).build();

		ConcertRequest.UpdateDto updateRequestDto = new ConcertRequest.UpdateDto(
			"아이유 콘서트 수정",
			null,
			null,
			LocalTime.parse("20:00"),
			LocalTime.parse("22:00"));

		// when
		BDDMockito.given(concertService.updatedConcert(eq(concertId), any())).willReturn(updateResponseDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/concerts/{concertId}", concertId)
				.header("Authorization", jwtToken)
				.content(objectMapper.writeValueAsString(updateRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.title").value("아이유 콘서트 수정"));
	}

	@Test
	@DisplayName("콘서트 단건 조회 정상 작동")
	void getDetailsConcerts() throws Exception {
		// given
		List<String> imageUrls = List.of("imageUrl1", "imageUrl2");
		Long concertId = 1L;

		ConcertResponse.ReadDto responseDto = ConcertResponse.ReadDto.builder()
			.id(concertId)
			.hallName("올림픽 경기장")
			.title("아이유 콘서트")
			.singer("아이유")
			.startAt(LocalDate.parse("2025-05-11"))
			.endAt(LocalDate.parse("2025-06-11"))
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000)
			.images(imageUrls)
			.build();

		// when
		BDDMockito.given(concertService.getDetailConcert(concertId)).willReturn(responseDto);
		// then
		mockMvc.perform(get("/concerts/{concertId}", concertId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(concertId))
			.andExpect(MockMvcResultMatchers.jsonPath("$.title").value("아이유 콘서트"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.singer").value("아이유"));
	}

	@Test
	@DisplayName("예매된 좌석 조회 시 정상 조회")
	void getStatistics_Test() throws Exception {
		// given
		String title = "콘서트";
		LocalDate startAt = LocalDate.parse("2025-02-01");
		LocalDate endAt = LocalDate.parse("2025-02-28");
		String order = "ASC";
		Pageable pageable = PageRequest.of(0, 2);

		ConcertResponse.StatisticsDto responseDto1 = new ConcertResponse.StatisticsDto(
			1L,
			"아이유 콘서트",
			400,
			4,
			440000,
			LocalDate.parse("2025-02-05")
		);

		ConcertResponse.StatisticsDto responseDto2 = new ConcertResponse.StatisticsDto(
			2L,
			"빅뱅 콘서트",
			400,
			2,
			220000,
			LocalDate.parse("2025-02-15")
		);

		ConcertResponse.StatisticsDto responseDto3 = new ConcertResponse.StatisticsDto(
			3L,
			"제니 콘서트",
			400,
			1,
			110000,
			LocalDate.parse("2025-02-25")
		);

		List<ConcertResponse.StatisticsDto> responseDtoList = new ArrayList<>();

		responseDtoList.add(responseDto1);
		responseDtoList.add(responseDto2);
		responseDtoList.add(responseDto3);

		// when
		BDDMockito.given(concertService.getStatistics(title, startAt, endAt, order, pageable))
			.willReturn(responseDtoList);

		// then
		mockMvc.perform(get("/concerts/statistics")
				.param("title", title)
				.param("startAt", startAt.toString())
				.param("endAt", endAt.toString())
				.param("order", order)
				.param("page", "0")
				.param("size", "2"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].concertTitle").value("아이유 콘서트"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].reservationSeat").value(4))
			.andExpect(MockMvcResultMatchers.jsonPath("$[1].concertTitle").value("빅뱅 콘서트"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[1].reservationSeat").value(2))
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].concertTitle").value("제니 콘서트"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].reservationSeat").value(1));
	}

	@Nested
	class getConcertSeats {

		@DisplayName("DTO 생성 메서드")
		private ConcertResponse.ConcertSeatDto createSeatDto(Long seatId, Integer seatNumber,
			ReservationStatus status) {
			ConcertResponse.ConcertSeatDto response = new ConcertResponse.ConcertSeatDto(
				seatId,
				seatNumber,
				status);
			return response;
		}

		@Test
		@DisplayName("콘서트 좌석 조회 시 정상 조회")
		void getConcertSeats_shouldReturnConcertSeatDto() throws Exception {
			// given
			Long concertId = 1L;
			LocalDate date = LocalDate.parse("2025-01-31");

			ConcertResponse.ConcertSeatDto seatDto1 = createSeatDto(1L, 1, null);
			ConcertResponse.ConcertSeatDto seatDto2 = createSeatDto(2L, 2, ReservationStatus.PENDING);
			ConcertResponse.ConcertSeatDto seatDto3 = createSeatDto(3L, 3, ReservationStatus.PENDING);

			List<ConcertResponse.ConcertSeatDto> responseDtoList = List.of(seatDto1, seatDto2, seatDto3);

			// when
			BDDMockito.given(concertService.getConcertSeats(concertId, date)).willReturn(responseDtoList);

			// then
			mockMvc.perform(get("/concerts/{concertId}/seats", concertId)
					.param("date", date.toString()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("AVAILABLE"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].status").value(ReservationStatus.PENDING.toString()));
		}
	}
}
