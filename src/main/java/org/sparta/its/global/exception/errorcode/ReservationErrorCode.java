package org.sparta.its.global.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode {

	// 400 BAD REQUEST
	ALREADY_STARTED(HttpStatus.BAD_REQUEST, "이미 시작된 콘서트는 예약 취소할 수 없습니다."),

	CANCEL_COMPLETED(HttpStatus.BAD_REQUEST, "예약 취소할 수 없습니다."),

	NOT_CORRECT_DATE(HttpStatus.BAD_REQUEST, "불가능한 콘서트 날짜입니다."),

	// 403 FORBIDDEN
	ALREADY_BOOKED(HttpStatus.FORBIDDEN, "이 자리는 이미 예약되었습니다."),

	// 404 NOT FOUND
	NOT_FOUND_RESERVATION(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),

	// 409 CONFLICT
	TIME_OUT(HttpStatus.CONFLICT, "자리 선택 요청 대기 시간을 초과했습니다. 재시도해주세요");

	private final HttpStatus httpStatus;
	private final String detail;
}