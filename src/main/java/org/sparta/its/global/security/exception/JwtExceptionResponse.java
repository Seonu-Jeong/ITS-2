package org.sparta.its.global.security.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class JwtExceptionResponse {

	private final String message;
	private final HttpStatus status;

	public JwtExceptionResponse(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

}