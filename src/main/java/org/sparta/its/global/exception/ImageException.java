package org.sparta.its.global.exception;

import org.sparta.its.global.exception.errorcode.ImageErrorCode;
import org.sparta.its.global.exception.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ImageException extends ParentException {

	private final ImageErrorCode errorCode;

	public ImageException(ImageErrorCode errorCode) {
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
