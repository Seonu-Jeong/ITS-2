package org.sparta.its.domain.concert.repository;

import java.time.LocalDate;

import org.sparta.its.domain.concert.dto.ConcertRequest;
import org.sparta.its.domain.concert.dto.ConcertResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 콘서트 QueryDslRepository.
 *
 * @author UTae Jang
 */
public interface ConcertQueryDslRepository {

	void updateConcert(Long concertId, ConcertRequest.UpdateDto updateDto);

	// todo :  querydsl vs jpql 비교 메서드
	// Page<Concert> findConcertsBySingerAndTitleAndTodayOrderByStartAt(
	// 	String singer,
	// 	String title,
	// 	String order,
	// 	LocalDate today,
	// 	Pageable pageable);

	Page<ConcertResponse.StatisticsDto> findStatisticsWithOrderByConcertInfo(
		String title,
		LocalDate startAt,
		LocalDate endAt,
		String order,
		Pageable pageable);
}
