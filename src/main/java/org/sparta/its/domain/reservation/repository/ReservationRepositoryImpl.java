package org.sparta.its.domain.reservation.repository;

import static org.sparta.its.domain.reservation.entity.QReservation.*;

import java.time.LocalDate;
import java.util.List;

import org.sparta.its.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * create on 2025. 01. 14.
 * create by IntelliJ IDEA.
 *
 * 예약 관련 QueryDsl 인터페이스 구현체.
 *
 * @author Jun Heo
 */
@Repository
public class ReservationRepositoryImpl implements ReservationQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public ReservationRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	/**
	 * 날짜 구간, 콘서트 이름, 가수 이름을 이용한 예약 조회
	 *
	 * @param startDate 날짜 구간 시작
	 * @param endDate 날짜 구간 끝
	 * @param concertTitle 콘서트 이름
	 * @param singer 가수 이름
	 * @param pageable 페이징
	 * @return {@link Page<Reservation>}
	 */
	@Override
	public Page<Reservation> findReservationsByBetweenDateAndConcertInfo(
		LocalDate startDate,
		LocalDate endDate,
		String concertTitle,
		String singer,
		Pageable pageable) {
		List<Reservation> fetch = jpaQueryFactory.selectFrom(reservation)
			.where(
				startDateFrom(startDate),
				endDateTo(endDate),
				concertTitleLike(concertTitle),
				singerLike(singer)
			)
			.orderBy(reservation.concert.startAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> count = jpaQueryFactory.select(reservation.count())
			.from(reservation)
			.where(
				startDateFrom(startDate),
				endDateTo(endDate),
				concertTitleLike(concertTitle),
				singerLike(singer)
			);

		return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
	}

	private BooleanExpression startDateFrom(LocalDate startAt) {
		if (startAt == null) {
			return null;
		}
		return reservation.concertDate.goe(startAt);
	}

	private BooleanExpression endDateTo(LocalDate endAt) {
		if (endAt == null) {
			return null;
		}
		return reservation.concertDate.loe(endAt);
	}

	private BooleanExpression concertTitleLike(String concertTitle) {
		if (concertTitle == null) {
			return null;
		}
		return reservation.concert.title.like("%" + concertTitle + "%");
	}

	private BooleanExpression singerLike(String singer) {
		if (singer == null) {
			return null;
		}
		return reservation.concert.singer.like("%" + singer + "%");
	}
}
