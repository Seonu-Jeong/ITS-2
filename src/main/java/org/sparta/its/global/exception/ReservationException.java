package org.sparta.its.global.exception;

import org.sparta.its.global.exception.errorcode.ReservationErrorCode;
import org.sparta.its.global.exception.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ReservationException extends ParentException {

	private final ReservationErrorCode errorCode;

	public ReservationException(ReservationErrorCode errorCode) {
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
