package org.sparta.its.domain.concert.util;

import java.time.LocalDate;
import java.time.LocalTime;

import org.sparta.its.domain.concert.dto.ConcertRequest;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.global.exception.ConcertException;
import org.sparta.its.global.exception.errorcode.ConcertErrorCode;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 콘서트 시간 및 날짜 관련 예외
 * @author UTae Jang
 */
public class ConcertValidator {

	// 시작 시간과 종료시간이 null 이 아니고 시작시간이 종료시간 이후일 경우 예외 처리
	public static void startTimeIsAfterEndTimeWithUpdate(LocalTime startTime, LocalTime endTime) {
		if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_UPDATE_TIME);
		}
	}

	// 시작 날짜과 종료날짜이 null 이 아니고 시작날짜이 종료날짜 이후일 경우 예외 처리
	public static void startAtIsAfterEndAtWithUpdate(LocalDate startAt, LocalDate endAt) {
		if (startAt != null && endAt != null && startAt.isAfter(endAt)) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_UPDATE_DATE);
		}
	}

	// 시작 날짜과 종료날짜이 null 이 아니고 시작날짜와 종료날짜 현재 시점 기준 이전일 경우 얘외 처리
	public static void isBeforeTodayWithNullCheck(LocalDate startAt, LocalDate endAt) {
		if (startAt != null && startAt.isBefore(LocalDate.now()) ||
			(endAt != null && endAt.isBefore(LocalDate.now()))) {
			throw new ConcertException(ConcertErrorCode.ALREADY_PASSED);
		}
	}

	//
	public static void compareDatesUpdateDtoToConcert(ConcertRequest.UpdateDto updateDto, Concert concert) {
		if (updateDto.getStartAt() == null && updateDto.getEndAt() != null && concert.getStartAt()
			.isAfter(updateDto.getEndAt())) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_UPDATE_DATE);
		}

		if (updateDto.getEndAt() == null && updateDto.getStartAt() != null && updateDto.getStartAt()
			.isAfter(concert.getEndAt())) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_UPDATE_DATE);
		}
	}

	public static void compareTimesUpdateDtoToConcert(ConcertRequest.UpdateDto updateDto, Concert concert) {
		if (updateDto.getRunningStartTime() == null && updateDto.getRunningEndTime() != null
			&& concert.getRunningStartTime().isAfter(updateDto.getRunningEndTime())) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_UPDATE_TIME);
		}

		if (updateDto.getRunningEndTime() == null && updateDto.getRunningStartTime() != null
			&& updateDto.getRunningStartTime().isAfter(concert.getRunningEndTime())) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_UPDATE_TIME);
		}
	}

	public static void startTimeIsAfterEndTime(LocalTime startTime, LocalTime endTime) {
		if (startTime.isAfter(endTime)) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_TIME);
		}
	}

	public static void startAtIsAfterEndAt(LocalDate startAt, LocalDate endAt) {
		if (startAt.isAfter(endAt)) {
			throw new ConcertException(ConcertErrorCode.IS_NOT_AFTER_DATE);
		}

	}

	public static void isBeforeToday(LocalDate startAt, LocalDate endAt) {
		if (startAt.isBefore(LocalDate.now()) || endAt.isBefore(LocalDate.now())) {
			throw new ConcertException(ConcertErrorCode.ALREADY_PASSED);
		}
	}
}

