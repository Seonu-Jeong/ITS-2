package org.sparta.its.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sparta.its.domain.user.Service.UserService;
import org.sparta.its.domain.user.dto.AuthRequest;
import org.sparta.its.domain.user.dto.AuthResponse;
import org.sparta.its.domain.user.dto.UserRequest;
import org.sparta.its.domain.user.dto.UserResponse;
import org.sparta.its.domain.user.entity.Status;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.email("test@email.com")
			.password("encodedPassword")
			.name("Test User")
			.phoneNumber("01012345678")
			.build();
	}

	@Test
	@DisplayName("회원가입 테스트")
	void testSignUp() {
		// given
		AuthRequest.SignUpDto signUpDto = new AuthRequest.SignUpDto("test@email.com", "password", "Test User",
			"01012345678", "USER");
		when(userRepository.existsUserByEmail(signUpDto.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(signUpDto.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		// when
		AuthResponse.SignUpDto result = userService.signUp(signUpDto);

		// then
		assertNotNull(result);
		assertEquals(testUser.getEmail(), result.getEmail());
	}

	@Test
	@DisplayName("로그인 테스트")
	void testLogin() {
		// given
		AuthRequest.LoginDto loginDto = new AuthRequest.LoginDto("test@email.com", "password");
		when(userRepository.findUserByEmailAndStatusIsActivatedOrThrow(loginDto.getEmail())).thenReturn(testUser);
		when(passwordEncoder.matches(loginDto.getPassword(), testUser.getPassword())).thenReturn(true);

		// when
		AuthResponse.LoginDto result = userService.login(loginDto);

		// then
		assertNotNull(result);
		assertEquals(testUser.getEmail(), result.getEmail());
	}

	@Test
	@DisplayName("회원 정보 수정 테스트")
	void testUpdateUser() {
		// given
		UserRequest.UpdateDto updateDto = new UserRequest.UpdateDto("new@email.com", "Test User", "01087654321",
			"oldPassword", "newPassword");
		when(userRepository.findByIdOrThrow(anyLong())).thenReturn(testUser);
		when(passwordEncoder.matches(updateDto.getOriginPassword(), testUser.getPassword())).thenReturn(true);
		when(passwordEncoder.encode(updateDto.getNewPassword())).thenReturn("newEncodedPassword");

		// when
		UserResponse.UpdateDto result = userService.updateUser(updateDto, 1L);

		// then
		assertNotNull(result);
		verify(userRepository, times(1)).updateUser(anyLong(), any(), any(), any(), any());
	}

	@Test
	@DisplayName("회원 탈퇴 테스트")
	void testDeleteUser() {
		// given
		when(userRepository.findUserByIdAndStatusIsActivatedOrThrow(testUser.getId()))
			.thenReturn(testUser);
		when(passwordEncoder.matches(anyString(), anyString()))
			.thenReturn(true);

		// when
		UserResponse.DeleteDto result = userService.deleteUser(
			new UserRequest.DeleteDto("correct_password"), testUser.getId());

		// then
		assertNotNull(result);
		assertEquals("회원탈퇴 완료", result.getMessage());
		assertNotEquals(testUser.getStatus(), Status.ACTIVATED); // 상태 변경 확인
	}
}
