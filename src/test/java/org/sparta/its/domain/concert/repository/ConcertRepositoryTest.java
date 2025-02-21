package org.sparta.its.domain.concert.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.concert.dto.ConcertRequest;
import org.sparta.its.domain.concert.dto.ConcertResponse;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.reservation.entity.Reservation;
import org.sparta.its.domain.reservation.repository.ReservationRepository;
import org.sparta.its.global.exception.ConcertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;

@SqlGroup({
	@Sql(
		value = "/sql/domain/concert/repository/setup.sql",
		config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.DEFAULT),
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
	),
	@Sql(
		value = "/sql/domain/concert/repository/delete.sql",
		config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.DEFAULT),
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
	)
})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	// @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class ConcertRepositoryTest {

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Test
	@DisplayName("콘서트 고유 식별자로 조회 시 존재하는 콘서트")
	void findByIdTest() {
		// given  @Sql Annotation 으로 INSERT Query 생성
		Concert findConcert = concertRepository.findByIdOrThrow(1L);

		// then
		assertThat(findConcert).isNotNull();
		assertThat(findConcert.getId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("존재하지 않는 콘서트 조회 시 예외 처리")
	void notFoundConcert() {
		// given
		Long concertId = 400L;

		// then
		assertThatThrownBy(() -> concertRepository.findByIdOrThrow(concertId))
			.isInstanceOf(ConcertException.class)
			.hasMessage(null);
	}

	@Test
	@DisplayName("가수명과 콘서트 제목으로 다건 조회, 정렬 방식과 기준으로 페이징 처리")
	void findConcertsBySingerAndTitleAndTodayOrderByStartAt() {
		// given @Sql Annotation 으로 INSERT Query 생성
		LocalDate today = LocalDate.now();
		Sort startAt = Sort.by(Sort.Order.desc("startAt"));

		Pageable pageable = PageRequest.of(0, 2, startAt);

		String singer = "%%";
		String title = "%콘서트%";

		Page<Concert> concerts = concertRepository.findConcertsBySingerAndTitleAndTodayOrderByStartAt(
			singer, title, today, pageable);

		assertThat(concerts.getTotalPages()).isEqualTo(2);
		assertThat(concerts.getTotalElements()).isEqualTo(3);
		assertThat(concerts.getContent().get(0).getStartAt()).isEqualTo(LocalDate.parse("2025-03-21"));
		assertThat(concerts.getContent().get(1).getStartAt()).isEqualTo(LocalDate.parse("2025-03-11"));
	}

	@Test
	@DisplayName("콘서트 날짜에 따른 예매 좌석 정상 조회")
	void findSeatsWithReservationByHallIdAndConcertDate_Test() {
		// given @Sql Annotation 으로 INSERT Query 생성

		// when
		Reservation findReservation = reservationRepository.findByIdOrThrow(1L);
		findReservation.completeReservation();

		List<ConcertResponse.ConcertSeatDto> seatDtoList
			= concertRepository.findSeatsWithReservationByHallIdAndConcertDate(1L, LocalDate.now());
		// then
		assertThat(seatDtoList.size()).isEqualTo(3);
		assertThat(seatDtoList.get(0).getSeatNumber()).isEqualTo(1);
		assertThat(seatDtoList.get(1).getSeatNumber()).isEqualTo(2);
		assertThat(seatDtoList.get(0).getStatus()).isEqualTo("COMPLETED");
		assertThat(seatDtoList.get(1).getStatus()).isEqualTo("AVAILABLE");
	}

	@Test
	@DisplayName("콘서트 제목과 날짜로 콘서트 별 예약된 좌석 및 총 매출 정상 조회")
	void findConcertInfoAndReservationInfo_Test() {
		// given @Sql Annotation 으로 INSERT Query 생성
		String title = "콘서트";
		LocalDate startAt = LocalDate.parse("2025-02-28");
		LocalDate endAt = LocalDate.parse("2025-03-31");
		String order = "ASC";
		Pageable pageable = PageRequest.of(0, 2);

		Reservation findReservation = reservationRepository.findByIdOrThrow(1L);
		findReservation.completeReservation();

		Page<ConcertResponse.StatisticsDto> result
			= concertRepository.findStatisticsWithOrderByConcertInfo(title, startAt, endAt, order, pageable);

		Assertions.assertThat(result.getTotalPages()).isEqualTo(2);
		Assertions.assertThat(result.getNumberOfElements()).isEqualTo(2);
		Assertions.assertThat(result.getContent().get(0).getReservationSeat()).isEqualTo(1);
		Assertions.assertThat(result.getContent().get(0).getStartAt()).isEqualTo(LocalDate.parse("2025-03-01"));
		Assertions.assertThat(result.getContent().get(1).getStartAt()).isEqualTo(LocalDate.parse("2025-03-11"));
	}

	@Test
	@DisplayName("콘서트 수정 요청 정상 작동")
	void updateConcert() {
		// given
		ConcertRequest.UpdateDto requestDto = new ConcertRequest.UpdateDto(
			"콘서트 이름 수정",
			null, // 기존 값 startAt : 2025-03-01
			null,        // 기존 값 endAt : 2025-03-31
			LocalTime.parse("21:00"),
			LocalTime.parse("23:00"));
		// then
		concertRepository.updateConcert(1L, requestDto);
		Concert updateConcert = concertRepository.findByIdOrThrow(1L);

		// then
		Assertions.assertThat(updateConcert.getTitle()).isEqualTo("콘서트 이름 수정");
		Assertions.assertThat(updateConcert.getStartAt().toString()).isEqualTo("2025-03-01");
		Assertions.assertThat(updateConcert.getRunningStartTime()).isEqualTo(LocalTime.parse("21:00"));
	}
}
