package org.sparta.its.domain.concert.service;

import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sparta.its.domain.concert.dto.ConcertRequest;
import org.sparta.its.domain.concert.dto.ConcertResponse;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concert.repository.ConcertRepository;
import org.sparta.its.domain.concertimage.entity.ConcertImage;
import org.sparta.its.domain.concertimage.repository.ConcertImageRepository;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.reservation.entity.ReservationStatus;
import org.sparta.its.global.exception.ConcertException;
import org.sparta.its.global.s3.ImageFormat;
import org.sparta.its.global.s3.S3Service;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

	@InjectMocks
	private ConcertService concertService;

	@Mock
	private ConcertRepository concertRepository;

	@Mock
	private HallRepository hallRepository;

	@Mock
	private S3Service s3Service;

	@Mock
	private ConcertImageRepository concertImageRepository;

	@Test
	@DisplayName("콘서트 등록 서비스 테스트")
	@Transactional
	void createConcertTest() throws Exception {
		// given
		List<String> imageUrls = List.of("imageUrl1", "imageUrl2");
		MockMultipartFile multipartFile1 = Mockito.mock(MockMultipartFile.class);
		MockMultipartFile multipartFile2 = Mockito.mock(MockMultipartFile.class);
		MockMultipartFile[] multipartFiles = {multipartFile1, multipartFile2};

		Long hallId = 1L;
		Hall hall = new Hall("잠실 경기장", "잠실", 400, true);
		ReflectionTestUtils.setField(hall, "id", hallId);

		ConcertRequest.CreateDto requestDto = new ConcertRequest.CreateDto(
			hallId,
			"콘서트명",
			"가수명",
			LocalDate.parse("2025-03-01"),
			LocalDate.parse("2025-03-31"),
			LocalTime.parse("20:00"),
			LocalTime.parse("22:00"),
			110000,
			multipartFiles);
		Concert concert = requestDto.toEntity(hall);
		ConcertImage concertImage = new ConcertImage(concert, imageUrls.get(0));
		// when
		BDDMockito.given(concertRepository.save(any())).willReturn(concert);
		BDDMockito.given(hallRepository.findByIdOrThrow(anyLong())).willReturn(hall);
		BDDMockito
			.given(s3Service.uploadImages(requestDto.getImages(), ImageFormat.CONCERT, concert.getId()))
			.willReturn(imageUrls);
		BDDMockito.given(concertImageRepository.save(any())).willReturn(concertImage);

		ConcertResponse.CreateDto responseDto = concertService.createConcert(requestDto);
		ReflectionTestUtils.setField(responseDto, "images", imageUrls);
		// then
		Assertions.assertThat(responseDto).isNotNull();
		Assertions.assertThat(responseDto.getHallId()).isEqualTo(hallId);
		Assertions.assertThat(responseDto.getTitle()).isEqualTo("콘서트명");
		Assertions.assertThat(responseDto.getSinger()).isEqualTo("가수명");
		Assertions.assertThat(responseDto.getImages().size()).isEqualTo(2);
	}

	@Nested
	class getConcert_shouldReturnConcertInfo {

		@DisplayName("콘서트 생성 메서드")
		private Concert createConcert(Long id, String title, String singer, LocalDate startAt) {
			Hall hall = new Hall("올림픽 경기장", "잠실", 400, true);
			Concert concert = Concert.builder()
				.hall(hall)
				.title(title)
				.singer(singer)
				.startAt(startAt)
				.endAt(LocalDate.parse("2025-03-31"))
				.runningStartTime(LocalTime.parse("22:00"))
				.runningEndTime(LocalTime.parse("23:00"))
				.price(110000).build();
			ReflectionTestUtils.setField(concert, "id", id);
			return concert;
		}

		@Test
		@DisplayName("콘서트 다건 조회 정상 조회 콘서트 시작일자 기준으로 내림차순 정렬")
		void getConcerts_Test() {
			// given
			String singer = "";
			String title = "";
			String order = "DESC";
			Sort sort = Sort.by(Sort.Order.desc("startAt"));
			Pageable pageable = PageRequest.of(0, 2, sort);

			List<Concert> concertList = List.of(
				createConcert(1L, "빅뱅", "빅뱅 콘서트", LocalDate.parse("2025-03-01")),
				createConcert(2L, "제니", "제니 콘서트", LocalDate.parse("2025-03-11")),
				createConcert(3L, "아이유", "아이유 콘서트", LocalDate.parse("2025-03-21")));
			// when
			BDDMockito
				.given(concertRepository.findConcertsBySingerAndTitleAndTodayOrderByStartAt
					(singer, title, LocalDate.now(), pageable))
				.willReturn(new PageImpl<>(concertList, pageable, concertList.size()));

			List<ConcertResponse.ReadDto> responseDtoList = concertService.getConcerts(singer, title, order, pageable);
			// then
			Assertions.assertThat(responseDtoList).isNotNull();
			Assertions.assertThat(responseDtoList.get(0).getStartAt()).isEqualTo(LocalDate.parse("2025-03-01"));
			Assertions.assertThat(responseDtoList.get(1).getStartAt()).isEqualTo(LocalDate.parse("2025-03-11"));
			Assertions.assertThat(responseDtoList.get(2).getStartAt()).isEqualTo(LocalDate.parse("2025-03-21"));
			Assertions.assertThat(responseDtoList.size()).isEqualTo(3);
		}

		@Test
		@DisplayName("콘서트 상세 조회 정상 조회")
		void getDetailConcert_Test() {
			// given
			Concert findConcert
				= createConcert(1L, "아이유 콘서트", "아이유", LocalDate.parse("2025-03-01"));
			// when
			BDDMockito.given(concertRepository.findByIdOrThrow(anyLong())).willReturn(findConcert);
			ConcertResponse.ReadDto detailConcert = concertService.getDetailConcert(1L);
			// then
			Assertions.assertThat(detailConcert).isNotNull();
			Assertions.assertThat(detailConcert.getId()).isEqualTo(1L);
		}

		@Test
		@DisplayName("현재 날짜 기준 이미 종료된 콘서트 조회 시 예외 처리")
		void getDetailConcert_endAtTest() {
			// given
			Concert concert = Concert.builder()
				.startAt(LocalDate.parse("2025-01-01"))
				.endAt(LocalDate.parse("2025-01-31"))
				.build();
			ReflectionTestUtils.setField(concert, "id", 1L);
			// when
			BDDMockito.given(concertRepository.findByIdOrThrow(anyLong())).willReturn(concert);
			// then
			Assertions.assertThatThrownBy(() -> concertService.getDetailConcert(concert.getId()))
				.isInstanceOf(ConcertException.class);
		}

		@Test
		@DisplayName("콘서트 정보 수정 시 콘서트 정보 반환")
		void updateConcert() {
			// given
			ConcertRequest.UpdateDto mockRequest = Mockito.mock(ConcertRequest.UpdateDto.class);
			Concert findConcert
				= createConcert(1L, "아이유 콘서트", "아이유", LocalDate.parse("2025-03-01"));
			// when
			BDDMockito.given(concertRepository.findByIdOrThrow(anyLong())).willReturn(findConcert);
			ConcertResponse.UpdateDto updateDto = concertService.updatedConcert(1L, mockRequest);
			// then
			Assertions.assertThat(updateDto).isNotNull();
			Assertions.assertThat(updateDto.getId()).isEqualTo(1L);
			Assertions.assertThat(updateDto.getTitle()).isEqualTo("아이유 콘서트");
			Assertions.assertThat(updateDto.getSinger()).isEqualTo("아이유");
		}

		@Test
		@DisplayName("콘서트 날짜별 좌석 조회 시 정상 조회")
		void getConcertSeats() {
			// given
			Concert findConcert
				= createConcert(1L, "아이유 콘서트", "아이유", LocalDate.parse("2025-03-01"));

			ConcertResponse.ConcertSeatDto responseDto1 = new ConcertResponse.ConcertSeatDto(
				1L,
				1,
				null);

			ConcertResponse.ConcertSeatDto responseDto2 = new ConcertResponse.ConcertSeatDto(
				2L,
				2,
				ReservationStatus.PENDING);

			ConcertResponse.ConcertSeatDto responseDto3 = new ConcertResponse.ConcertSeatDto(
				3L,
				3,
				ReservationStatus.PENDING);

			List<ConcertResponse.ConcertSeatDto> responseDtoList = List.of(responseDto1, responseDto2, responseDto3);
			// when
			BDDMockito.given(concertRepository.findByIdOrThrow(anyLong())).willReturn(findConcert);
			BDDMockito
				.given(concertRepository.findSeatsWithReservationByHallIdAndConcertDate(any(), eq(LocalDate.now())))
				.willReturn(responseDtoList);

			List<ConcertResponse.ConcertSeatDto> concertSeats
				= concertService.getConcertSeats(findConcert.getId(), LocalDate.now());
			// then
			Assertions.assertThat(concertSeats).isNotNull();
			Assertions.assertThat(concertSeats.size()).isEqualTo(3);
			Assertions.assertThat(concertSeats.get(0).getStatus()).isEqualTo("AVAILABLE");
			Assertions.assertThat(concertSeats.get(1).getStatus()).isEqualTo(ReservationStatus.PENDING.toString());
			Assertions.assertThat(concertSeats.get(2).getStatus()).isEqualTo(ReservationStatus.PENDING.toString());
		}
	}

	@Test
	@DisplayName("콘서트 시작 날짜가 종료 날짜 이후일 경우 예외 처리")
	void getStatistics_shouldReturnConcertException() {
		String title = "아이유";
		LocalDate startAt = LocalDate.parse("2025-03-31");
		LocalDate endAt = LocalDate.parse("2025-03-01");
		String order = "ASC";
		Pageable pageable = PageRequest.of(0, 2);

		Assertions.assertThatThrownBy(() -> concertService.getStatistics(title, startAt, endAt, order, pageable))
			.isInstanceOf(ConcertException.class);
	}
}

