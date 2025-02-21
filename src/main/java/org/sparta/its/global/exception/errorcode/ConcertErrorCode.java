package org.sparta.its.global.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConcertErrorCode {

	// 400 BAD_REQUEST
	IS_NOT_AFTER_TIME(HttpStatus.BAD_REQUEST, "콘서트 시작 시간은 종료 시간보다 이후일 수 없습니다."),

	IS_NOT_AFTER_DATE(HttpStatus.BAD_REQUEST, "콘서트 시작 날짜는 종료 날짜보다 늦을 수 없습니다."),

	IS_NOT_AFTER_UPDATE_TIME(HttpStatus.BAD_REQUEST, "콘서트 시간을 수정할 수 없습니다"),

	IS_NOT_AFTER_UPDATE_DATE(HttpStatus.BAD_REQUEST, "콘서트 날짜 수정할 수 없습니다"),

	ALREADY_ENDED(HttpStatus.BAD_REQUEST, "이미 종료된 콘서트입니다."),

	ALREADY_PASSED(HttpStatus.BAD_REQUEST, "콘서트 시작 날짜와, 종료 날짜는 현재 날짜보다 이전일 수 없습니다."),

	INCORRECT_VALUE(HttpStatus.BAD_REQUEST, "ASC 또는 DESC 만 입력 가능합니다."),

	// 404 NOT_FOUND
	NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트 정보를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String detail;
}
