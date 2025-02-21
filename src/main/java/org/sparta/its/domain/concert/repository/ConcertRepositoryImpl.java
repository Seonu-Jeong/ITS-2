package org.sparta.its.domain.concert.repository;

import static org.sparta.its.domain.concert.entity.QConcert.*;
import static org.sparta.its.domain.reservation.entity.QReservation.*;
import static org.sparta.its.global.constant.GlobalConstant.*;
import static org.sparta.its.global.exception.errorcode.ConcertErrorCode.*;

import java.time.LocalDate;
import java.util.List;

import org.sparta.its.domain.concert.dto.ConcertRequest;
import org.sparta.its.domain.concert.dto.ConcertResponse;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.reservation.entity.ReservationStatus;
import org.sparta.its.global.exception.ConcertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import jakarta.persistence.EntityManager;

/**
 * create on 2025. 01. 15.
 * create by IntelliJ IDEA.
 *
 * 콘서트 관련 Repository 구현체.
 *
 * @author UTae Jang
 */
@Repository
public class ConcertRepositoryImpl implements ConcertQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final EntityManager entityManager;

	public ConcertRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
		this.entityManager = entityManager;
	}

	// TODO : 성능 개선 JPA 메서드 find 해보기 (영속성 컨텍스트)

	/**
	 * 콘서트 정보 수정
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param updateDto 수정 요청 Dto
	 */
	@Override
	public void updateConcert(Long concertId, ConcertRequest.UpdateDto updateDto) {

		JPAUpdateClause query = jpaQueryFactory
			.update(concert)
			.where(concert.id.eq(concertId));

		if (updateDto.getTitle() != null) {
			query.set(concert.title, updateDto.getTitle());
		}

		if (updateDto.getStartAt() != null) {
			query.set(concert.startAt, updateDto.getStartAt());
		}

		if (updateDto.getEndAt() != null) {
			query.set(concert.endAt, updateDto.getEndAt());
		}

		if (updateDto.getRunningStartTime() != null) {
			query.set(concert.runningStartTime, updateDto.getRunningStartTime());
		}

		if (updateDto.getRunningEndTime() != null) {
			query.set(concert.runningEndTime, updateDto.getRunningEndTime());
		}

		query.execute();

		entityManager.flush();
		entityManager.clear();
	}

	// todo : jpql vs querydsl 최적화 테스트
	// @Override
	// public Page<Concert> findConcertsBySingerAndTitleAndTodayOrderByStartAt(String singer, String title, String order,
	// 	LocalDate today, Pageable pageable) {
	//
	// 	List<Concert> fetch = jpaQueryFactory.selectFrom(concert)
	// 		.where(singerLikeTest(singer)
	// 			.and(concertTitleLike(title))
	// 			.and(concert.endAt.goe(today)))
	// 		.orderBy(decideOrderBy(order))
	// 		.fetch();
	//
	// 	JPAQuery<Long> count = jpaQueryFactory
	// 		.select(concert.count())
	// 		.from(concert)
	// 		.where(
	// 			singerLikeTest(singer)
	// 				.and(concertTitleLike(title))
	// 				.and(concert.endAt.goe(today)));
	//
	// 	return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
	// }
	//
	// private BooleanExpression singerLikeTest(String singer) {
	// 	if (singer == null) {
	// 		return null;
	// 	}
	// 	return concert.title.like("%" + singer + "%");
	// }

	/**
	 * 콘서트 등록 현황 조회
	 *
	 * @param title 콘서트 제목
	 * @param startAt 콘서트 시작 시간
	 * @param endAt 콘서트 종료 시간
	 * @param order 정렬 방식
	 * @param pageable 페이지 설정
	 * @return {@link Page<Concert>}
	 */
	@Override
	public Page<ConcertResponse.StatisticsDto> findStatisticsWithOrderByConcertInfo(
		String title,
		LocalDate startAt,
		LocalDate endAt,
		String order,
		Pageable pageable) {

		JPQLQuery<Integer> reservationCount = JPAExpressions.select(reservation.count().intValue())
			.from(reservation)
			.where(reservation.status.eq(ReservationStatus.COMPLETED)
				.and(reservation.concert.id.eq(concert.id)));

		List<ConcertResponse.StatisticsDto> fetch = jpaQueryFactory
			.select(Projections.constructor(
				ConcertResponse.StatisticsDto.class,
				concert.id,
				concert.title,
				concert.hall.capacity,
				reservationCount,
				concert.price.multiply(reservationCount),
				concert.startAt))
			.from(concert)
			.where(concertTitleLike(title)
				.and(isAfterStartAt(startAt))
				.and(isBeforeEndAt(endAt)))
			.groupBy(concert.id)
			.orderBy(decideOrderBy(order))
			.offset(pageable.getPageNumber())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> count = jpaQueryFactory
			.select(concert.count())
			.from(concert)
			.where(
				concertTitleLike(title)
					.and(isAfterStartAt(startAt))
					.and(isBeforeEndAt(endAt)));

		return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
	}

	private BooleanExpression concertTitleLike(String title) {
		if (title == null) {
			return null;
		}
		return concert.title.like("%" + title + "%");
	}

	private BooleanExpression isAfterStartAt(LocalDate startAt) {
		if (startAt == null) {
			return null;
		}
		return concert.startAt.goe(startAt);
	}

	private BooleanExpression isBeforeEndAt(LocalDate endAt) {
		if (endAt == null) {
			return null;
		}
		return concert.endAt.loe(endAt);
	}

	private OrderSpecifier<LocalDate> decideOrderBy(String order) {
		return switch (order.toUpperCase()) {
			case ORDER_DESC -> concert.startAt.desc();
			case ORDER_ASC -> concert.startAt.asc();
			default -> throw new ConcertException(INCORRECT_VALUE);
		};
	}
}
