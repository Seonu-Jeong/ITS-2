package org.sparta.its.domain.user.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.user.Service.UserService;
import org.sparta.its.domain.user.dto.UserRequest;
import org.sparta.its.domain.user.dto.UserResponse;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.global.security.UserDetail;
import org.sparta.its.global.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthorizationFilter.class),
})
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private User testUser;

	@BeforeEach
	void setUp() {
		// testUser = new User(null, "testName", "testEmail");
		testUser = User.builder()
			.email("test@email.com")
			.password("Password1234!")
			.name("Test User")
			.phoneNumber("01012345678")
			.role(Role.USER)
			.build();

		ReflectionTestUtils.setField(testUser, "id", 1L);
		//
		UserDetail userDetails = new UserDetail(testUser.getId(), testUser.getName(), testUser.getEmail());
		//
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, getGrantedAuthorities()));
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

	@Test
	void updateUser() throws Exception {
		// Prepare the request body
		UserRequest.UpdateDto updateDto = new UserRequest.UpdateDto(
			"updated@example.com", "originPassword12@", "newPassword123@", "Updated Name", "010-1234-5678");
		String jsonRequest = objectMapper.writeValueAsString(updateDto);

		// Prepare the response
		UserResponse.UpdateDto responseDto = UserResponse.UpdateDto.builder()
			.id(1L)
			.email(updateDto.getEmail())
			.name(updateDto.getName())
			.phoneNumber(updateDto.getPhoneNumber())
			.build();

		when(userService.updateUser(any(), eq(1L))).thenReturn(responseDto);

		// Perform the PATCH request and check the response
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(responseDto.getEmail()))
			.andExpect(jsonPath("$.name").value(responseDto.getName()))
			.andExpect(jsonPath("$.phoneNumber").value(responseDto.getPhoneNumber()));
	}

	@Test
	void deleteUser() throws Exception {
		// Prepare the request body
		UserRequest.DeleteDto deleteDto = new UserRequest.DeleteDto("password123");
		String jsonRequest = objectMapper.writeValueAsString(deleteDto);

		// Prepare the response
		UserResponse.DeleteDto responseDto = UserResponse.DeleteDto.builder()
			.message("User deleted successfully")
			.build();

		when(userService.deleteUser(any(), eq(1L))).thenReturn(responseDto);

		// Perform the DELETE request and check the response
		mockMvc.perform(delete("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("User deleted successfully"));
	}
}
