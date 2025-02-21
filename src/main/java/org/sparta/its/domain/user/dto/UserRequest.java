package org.sparta.its.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 12.
 * create by IntelliJ IDEA.
 *
 * 유저 관련 요청 DTO.
 *
 * @author Seonu-Jeong
 */
public class UserRequest {

	@Getter
	@RequiredArgsConstructor
	public static class UpdateDto {

		@Email(message = "올바른 email 형식이 아닙니다")
		private final String email;

		private final String originPassword;

		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8글자 이상이며, 영문, 숫자, 특수문자를 1개씩 포함해야합니다.")
		private final String newPassword;

		private final String name;

		@Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}", message = "유효하지 않은 휴대폰 형식입니다.")
		private final String phoneNumber;

	}

	@Getter
	@RequiredArgsConstructor
	public static class DeleteDto {

		@NotBlank(message = "비밀번호를 입력해주세요")
		private final String password;
	}
}