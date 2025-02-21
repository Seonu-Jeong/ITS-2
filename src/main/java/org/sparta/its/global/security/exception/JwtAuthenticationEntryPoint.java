package org.sparta.its.global.security.exception;

import static org.sparta.its.global.exception.errorcode.UserErrorCode.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.sparta.its.global.exception.UserException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		UserException exception = new UserException(UNAUTHORIZED_ACCESS);

		response.setStatus(exception.getHttpStatus().value());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(
			exception.toErrorResponseDto()
		));
	}
}