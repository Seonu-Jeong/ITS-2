package org.sparta.its.domain.reservation.repository;

import java.time.LocalDate;

import org.sparta.its.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 예약 관련 QueryDsl Repository.
 *
 * @author Jun Heo
 */
public interface ReservationQueryDslRepository {
	Page<Reservation> findReservationsByBetweenDateAndConcertInfo(
		LocalDate startAt,
		LocalDate endAt,
		String concertTitle,
		String singer,
		Pageable pageable);
}
