package org.sparta.its.global.exception.response;

import org.sparta.its.global.exception.ParentException;
import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {

	private final String code;
	private final String massage;

	public static ResponseEntity<ErrorResponseDto> toResponseEntity(ParentException exception) {
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(exception.toErrorResponseDto());
	}
}