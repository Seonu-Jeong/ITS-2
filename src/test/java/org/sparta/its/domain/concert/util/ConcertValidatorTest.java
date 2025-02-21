package org.sparta.its.domain.concert.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sparta.its.global.exception.ConcertException;

class ConcertValidatorTest {

	@Test
	@DisplayName("콘서트 시작 날짜가 종료 날짜 이후일 경우 예외처리")
	void StartDateIsAfterEndDateTest() {
		// given
		LocalDate startAt = LocalDate.of(2025, 02, 17);
		LocalDate endAt = LocalDate.of(2025, 01, 17);

		// then
		assertThrows(ConcertException.class, () -> ConcertValidator.startAtIsAfterEndAt(startAt, endAt));
	}

	@Test
	@DisplayName("콘서트 시작 시간이 종료 시간 이후일 경우 예외처리")
	void checkStartTimeIsAfterEndTimeTest() {
		// given
		LocalTime startTime = LocalTime.of(16, 00);
		LocalTime endTime = LocalTime.of(14, 00);

		// then
		assertThrows(ConcertException.class, () -> ConcertValidator.startTimeIsAfterEndTime(startTime, endTime));
	}

	@Test
	@DisplayName("콘서트 시작 시간과 종료 시간은 현재 시점을 이후일 수 없습니다.")
	void isBeforeTodayTest() {
		// given
		LocalDate startAt = LocalDate.of(2025, 01, 01);
		LocalDate endAt = LocalDate.of(2025, 01, 02);

		// then
		assertThrows(ConcertException.class, () -> ConcertValidator.isBeforeToday(startAt, endAt));
	}
}
