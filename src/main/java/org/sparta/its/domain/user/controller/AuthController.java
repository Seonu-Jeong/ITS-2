package org.sparta.its.domain.user.controller;

import org.sparta.its.domain.user.Service.UserService;
import org.sparta.its.domain.user.dto.AuthRequest;
import org.sparta.its.domain.user.dto.AuthResponse;
import org.sparta.its.global.security.JwtUtil;
import org.sparta.its.global.security.UserDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 인증 관련 Controller.
 *
 * @author Seonu-Jeong
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtil jwtUtil;
	private final UserService userService;

	/**
	 * 회원가입 API
	 *
	 * @param signUpDto 회원가입 DTO
	 * @return {@link AuthResponse.SignUpDto}
	 */
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse.SignUpDto> signup(
		@Valid @RequestBody AuthRequest.SignUpDto signUpDto) {

		AuthResponse.SignUpDto responseDto = userService.signUp(signUpDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	/**
	 * 로그인 API
	 *
	 * @param loginDto 로그인 DTO
	 * @param res http 응답 객체
	 * @return {@link AuthResponse.LoginDto}
	 */
	@PostMapping("/login")
	public ResponseEntity<AuthResponse.LoginDto> login(
		@Valid @RequestBody AuthRequest.LoginDto loginDto,
		HttpServletResponse res) {

		AuthResponse.LoginDto responseDto = userService.login(loginDto);

		// JWT 토큰 생성
		String token = jwtUtil.createToken(
			responseDto.getId(),
			responseDto.getEmail(),
			responseDto.getName(),
			responseDto.getRole());

		// 쿠키에 토큰 등록
		jwtUtil.addJwtToHeader(token, res);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	/**
	 * 로그아웃 API
	 *
	 * @param userDetail 유저 인증 객체
	 * @param res http 응답 객체
	 * @return {@link HttpServletResponse}
	 */
	@PostMapping("/logout")
	public ResponseEntity<AuthResponse.LogoutDto> logout(
		@AuthenticationPrincipal UserDetail userDetail,
		HttpServletResponse res) {

		// TODO JWT 로그아웃 방식으로 변경

		// 응답 DTO 생성
		AuthResponse.LogoutDto responseDto = AuthResponse.LogoutDto.toDto(userDetail.getId(), userDetail.getName());

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

}