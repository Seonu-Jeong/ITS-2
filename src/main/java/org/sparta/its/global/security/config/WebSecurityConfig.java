package org.sparta.its.global.security.config;

import org.sparta.its.global.security.exception.JwtAccessDeniedHandler;
import org.sparta.its.global.security.exception.JwtAuthenticationEntryPoint;
import org.sparta.its.global.security.filter.JwtAuthorizationFilter;
import org.sparta.its.global.security.filter.JwtExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	private final JwtAuthorizationFilter jwtAuthorizationFilter;
	private final JwtExceptionFilter jwtExceptionFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF 설정
		http.csrf(AbstractHttpConfigurer::disable);

		// 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
		http.sessionManagement((sessionManagement) ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests((authorizeHttpRequests) ->
			authorizeHttpRequests
				// cancleList 도메인
				.requestMatchers("/cancelLists/**").authenticated()

				// concert 도메인, concertImage 도메인
				.requestMatchers(HttpMethod.GET, "/concerts", "/concerts/*").permitAll()
				.requestMatchers("/concerts/**").authenticated()

				// hall 도메인, hallImage 도메인
				.requestMatchers("/halls/**").hasAuthority("ADMIN")

				// reservation 도메인
				.requestMatchers("/reservations/**").authenticated()

				// auth (유저 인증)
				.requestMatchers("/auth/signup", "/auth/login").permitAll()
				.requestMatchers("/auth/logout").authenticated()

				// user 도메인
				.requestMatchers("/users/**").authenticated()

				//나머지
				.anyRequest().permitAll()
		);

		// 필터 관리
		http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtExceptionFilter, JwtAuthorizationFilter.class);

		http.exceptionHandling(exceptionHandling ->
			exceptionHandling
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)
		);

		return http.build();
	}

}