package org.sparta.its.domain.concert.repository;

import static org.sparta.its.global.exception.errorcode.ConcertErrorCode.*;

import java.time.LocalDate;
import java.util.List;

import org.sparta.its.domain.concert.dto.ConcertResponse;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.global.exception.ConcertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * create on 2025. 01. 08.
 * create by IntelliJ IDEA.
 *
 * 콘서트 관련 Repository.
 *
 * @author UTae Jang
 */
public interface ConcertRepository extends JpaRepository<Concert, Long>, ConcertQueryDslRepository {

	// 쿼리 메소드

	// @Query 작성 메소드
	@Query("""
		SELECT c
			FROM concert c
			WHERE c.singer LIKE %:singer%
			AND c.title LIKE %:title%
			AND c.endAt >:today
		""")
	Page<Concert> findConcertsBySingerAndTitleAndTodayOrderByStartAt(
		@Param("singer") String singer,
		@Param("title") String title,
		@Param("today") LocalDate today,
		Pageable pageable);
	
	@Query("""
		SELECT new org.sparta.its.domain.concert.dto.ConcertResponse$ConcertSeatDto(s.id, s.seatNumber, r.status)
			FROM seat s
			LEFT JOIN reservation r
			ON s.id = r.seat.id AND r.concertDate = :date
			WHERE s.hall.id = :hallId
		""")
	List<ConcertResponse.ConcertSeatDto> findSeatsWithReservationByHallIdAndConcertDate(
		@Param("hallId") Long hallId,
		@Param("date") LocalDate date);

	// Default 메소드
	default Concert findByIdOrThrow(Long concertId) {
		return findById(concertId).orElseThrow(() ->
			new ConcertException(NOT_FOUND));
	}
}
