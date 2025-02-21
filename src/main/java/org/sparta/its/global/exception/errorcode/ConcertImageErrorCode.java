package org.sparta.its.global.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConcertImageErrorCode {

	// 400 BAD_REQUEST
	NOT_MATCHING(HttpStatus.BAD_REQUEST, "콘서트 이미지가 콘서트와 매칭되지 않습니다."),

	// 404 NOT_FOUND
	NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트 이미지 정보를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String detail;
}
