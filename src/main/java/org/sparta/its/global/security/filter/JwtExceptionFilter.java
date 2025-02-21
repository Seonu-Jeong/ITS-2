package org.sparta.its.global.security.filter;

import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.sparta.its.global.security.exception.JwtExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws
		ServletException, IOException {
		try {
			chain.doFilter(req, res); // go to 'JwtAuthenticationFilter'
		} catch (JwtException | IOException ex) {
			setErrorResponse(HttpStatus.UNAUTHORIZED, res, ex);
		}
	}

	public void setErrorResponse(HttpStatus status, HttpServletResponse res, Throwable ex) throws IOException {
		res.setStatus(status.value());
		res.setContentType(APPLICATION_JSON_VALUE);
		res.setCharacterEncoding("UTF-8");

		JwtExceptionResponse jwtExceptionResponse = new JwtExceptionResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);

		res.getWriter().write(objectMapper.writeValueAsString(jwtExceptionResponse));
	}
}