package org.sparta.its.global.exception;

import org.sparta.its.global.exception.errorcode.ConcertImageErrorCode;
import org.sparta.its.global.exception.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ConcertImageException extends ParentException {

	private final ConcertImageErrorCode errorCode;

	public ConcertImageException(ConcertImageErrorCode errorCode) {
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


