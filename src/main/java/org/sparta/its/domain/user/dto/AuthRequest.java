package org.sparta.its.domain.user.dto;

import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 인증 관련 요청 DTO.
 *
 * @author Seonu-Jeong
 */
public class AuthRequest {

	@Getter
	@RequiredArgsConstructor
	public static class SignUpDto {

		@NotBlank(message = "email 입력은 필수입니다")
		@Email(message = "올바른 email 형식이 아닙니다")
		private final String email;

		@NotBlank(message = "password 입력은 필수입니다")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8글자 이상이며, 영문, 숫자, 특수문자를 1개씩 포함해야합니다.")
		private final String password;

		@NotBlank(message = "이름은 빈 값이 허용되지 않습니다")
		private final String name;

		@Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}", message = "유효하지 않은 휴대폰 형식입니다.")
		private final String phoneNumber;

		private final String role;

		public User toEntity(PasswordEncoder passwordEncoder) {

			return User.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.name(name)
				.phoneNumber(phoneNumber)
				.role(Role.of(role))
				.build();
		}
	}

	@Getter
	@RequiredArgsConstructor
	public static class LoginDto {

		@NotBlank(message = "이메일을 입력해주세요")
		private final String email;

		@NotBlank(message = "비밀번호를 입력해주세요")
		private final String password;

	}
}