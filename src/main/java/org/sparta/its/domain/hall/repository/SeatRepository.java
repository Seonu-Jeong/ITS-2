package org.sparta.its.domain.hall.repository;

import static org.sparta.its.global.exception.errorcode.SeatErrorCode.*;

import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.global.exception.SeatException;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 좌석 관련 Repository.
 *
 * @author TaeHyeon Kim
 */
public interface SeatRepository extends JpaRepository<Seat, Long> {

	// 쿼리 메소드

	// @Query 작성 메소드

	// Default 메소드
	default Seat findByIdOrThrow(Long seatId) {
		return findById(seatId)
			.orElseThrow(() -> new SeatException(NOT_FOUND_SEAT));
	}
}
