package org.sparta.its.global.exception;

import org.sparta.its.global.exception.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;

public abstract class ParentException extends RuntimeException {

	public abstract HttpStatus getHttpStatus();

	public abstract ErrorResponseDto toErrorResponseDto();
}