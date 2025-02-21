package org.sparta.its.domain.reservation.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.global.exception.ReservationException;
import org.sparta.its.global.exception.errorcode.ReservationErrorCode;

public class ReservationEntityTest {
	private User user;
	private Concert concert;
	private Seat seat;
	private Reservation reservation;

	@BeforeEach
	void setUp() {
		// Mock 데이터 준비
		user = mock(User.class);
		concert = mock(Concert.class);
		seat = mock(Seat.class);

		// 예약 인스턴스 생성
		reservation = Reservation.builder()
			.user(user)
			.concert(concert)
			.seat(seat)
			.status(ReservationStatus.PENDING) // 초기 상태는 대기
			.concertDate(LocalDate.of(2025, 1, 31))
			.build();
	}

	@Test
	void testCompleteReservation() {
		// given: 예약 상태가 PENDING일 때
		assertEquals(ReservationStatus.PENDING, reservation.getStatus());

		// when: 예약 완료 처리
		reservation.completeReservation();

		// then: 상태가 COMPLETED로 변경됨
		assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
	}

	@Test
	void testCompleteReservation_AlreadyCompleted() {
		// given: 예약 상태가 이미 COMPLETED일 때
		reservation.completeReservation(); // 상태 변경
		assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());

		// when & then: 이미 완료된 예약에 대해 다시 완료를 시도하면 예외가 발생해야 함
		ReservationException exception = assertThrows(ReservationException.class, reservation::completeReservation);

		assertEquals(ReservationErrorCode.ALREADY_BOOKED, exception.getErrorCode());
	}

	@Test
	void testCancelReservation() {
		// given: 예약 상태가 PENDING일 때
		assertEquals(ReservationStatus.PENDING, reservation.getStatus());

		// when: 예약 취소 처리
		reservation.cancelReservation();

		// then: 상태가 CANCEL_WAIT으로 변경됨
		assertEquals(ReservationStatus.CANCEL_WAIT, reservation.getStatus());
	}
}
