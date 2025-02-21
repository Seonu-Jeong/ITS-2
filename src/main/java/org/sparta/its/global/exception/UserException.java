package org.sparta.its.global.exception;

import org.sparta.its.global.exception.errorcode.UserErrorCode;
import org.sparta.its.global.exception.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UserException extends ParentException {

	private final UserErrorCode errorCode;

	public UserException(UserErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return errorCode.getHttpStatus();
	}

	@Override
	public ErrorResponseDto toErrorResponseDto() {
		return ErrorResponseDto.builder()
			.code(errorCode.toString())
			.massage(errorCode.getDetail())
			.build();
	}
}