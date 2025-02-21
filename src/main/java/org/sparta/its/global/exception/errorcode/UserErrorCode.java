package org.sparta.its.global.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode {

	// 400 BAD REQUEST
	ILLEGAL_ROLE(HttpStatus.BAD_REQUEST, "해당하는 이름의 역할이 존재하지 않습니다"),

	ALREADY_EXIST(HttpStatus.BAD_REQUEST, "가입이 불가능한 이메일입니다"),

	INVALID_LOGIN(HttpStatus.BAD_REQUEST, "아이디, 비밀번호가 불일치합니다"),

	PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 불일치합니다"),

	// 401 UNAUTHORIZED
	UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

	// 403 FORBIDDEN
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 접근입니다."),

	// 404 NOT FOUND
	NO_EXIST_ID(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다");

	private final HttpStatus httpStatus;
	private final String detail;
}