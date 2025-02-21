package org.sparta.its.domain.user.dto;

import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 인증 관련 응답 DTO.
 *
 * @author Seonu-Jeong
 */
public class AuthResponse {

	@Getter
	@Builder
	public static class SignUpDto {

		private final Long id;

		private final String name;

		private final String email;

		private final Role role;

		public static SignUpDto toDto(User user) {

			return SignUpDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.role(user.getRole())
				.build();
		}
	}

	@Getter
	@Builder
	public static class LoginDto {

		private final Long id;

		private final String name;

		private final String email;

		private final Role role;

		public static LoginDto toDto(User user) {

			return LoginDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.role(user.getRole())
				.build();
		}
	}

	@Getter
	@Builder
	public static class LogoutDto {

		private final Long id;

		private final String name;

		public static LogoutDto toDto(Long id, String name) {

			return LogoutDto.builder()
				.id(id)
				.name(name)
				.build();
		}
	}
}