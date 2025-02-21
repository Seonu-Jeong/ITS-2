package org.sparta.its.domain.user.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.sparta.its.domain.user.Service.UserService;
import org.sparta.its.domain.user.dto.AuthRequest;
import org.sparta.its.domain.user.dto.AuthResponse;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.global.security.JwtUtil;
import org.sparta.its.global.security.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthController.class)
@WithMockUser(authorities = "USER")
public class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@Autowired
	private ObjectMapper objectMapper;

	// 회원가입 API 테스트
	@Test
	void signUpTest() throws Exception {
		// Given
		AuthRequest.SignUpDto signUpDto = new AuthRequest.SignUpDto(
			"test@example.com",
			"Password123!",
			"John",
			"010-1234-5678",
			"USER");

		AuthResponse.SignUpDto responseDto = AuthResponse.SignUpDto.builder()
			.id(1L)
			.email("test@example.com")
			.name("John")
			.role(Role.USER)
			.build();

		when(userService.signUp(any())).thenReturn(responseDto);

		// When & Then
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signUpDto))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.email").value("test@example.com"))
			.andExpect(jsonPath("$.name").value("John"))
			.andExpect(jsonPath("$.role").value("USER"));
	}

	// 로그인 API 테스트
	@Test
	void loginTest() throws Exception {
		// Given
		AuthRequest.LoginDto loginDto = new AuthRequest.LoginDto("test@example.com", "Password123!");

		AuthResponse.LoginDto responseDto = AuthResponse.LoginDto.builder()
			.id(1L)
			.email("test@example.com")
			.name("John")
			.role(Role.USER)
			.build();

		when(userService.login(any())).thenReturn(responseDto);
		when(jwtUtil.createToken(1L, "test@example.com", "John", Role.USER)).thenReturn("mockJwtToken");

		// When & Then
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDto))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.email").value("test@example.com"))
			.andExpect(jsonPath("$.name").value("John"))
			.andExpect(jsonPath("$.role").value(Role.USER.toString()));
	}

	//로그아웃 API 테스트
	@Test
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	void logoutTest() throws Exception {
		// Given
		UserDetail userDetail = new UserDetail(1L, "John", "test@example.com");
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(userDetail, null, getGrantedAuthorities()));

		AuthResponse.LogoutDto responseDto = AuthResponse.LogoutDto.builder()
			.id(1L)
			.name("John")
			.build();

		// When & Then
		mockMvc.perform(post("/auth/logout")
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("John"));
	}

	private Collection<GrantedAuthority> getGrantedAuthorities() {
		String authority = "ROLE_USER";
		String authorityAdmin = "ROLE_ADMIN";

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		SimpleGrantedAuthority simpleGrantedAuthorityAdmin = new SimpleGrantedAuthority(authorityAdmin);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);
		authorities.add(simpleGrantedAuthorityAdmin);
		return authorities;
	}

}
