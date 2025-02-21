package org.sparta.its.domain.user.Service;

import static org.sparta.its.global.exception.errorcode.UserErrorCode.*;

import org.sparta.its.domain.user.dto.AuthRequest;
import org.sparta.its.domain.user.dto.AuthResponse;
import org.sparta.its.domain.user.dto.UserRequest;
import org.sparta.its.domain.user.dto.UserResponse;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.sparta.its.global.exception.UserException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 12.
 * create by IntelliJ IDEA.
 *
 * 유저 관련 Service.
 *
 * @author Seonu-Jeong
 */
@Service
@RequiredArgsConstructor
public class UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	/**
	 * 회원가입
	 *
	 * @param signUpDto 유저 요청 DTO
	 * @return {@link AuthResponse.SignUpDto}
	 */
	@Transactional
	public AuthResponse.SignUpDto signUp(AuthRequest.SignUpDto signUpDto) {

		// 이메일 중복 여부 확인
		if (userRepository.existsUserByEmail(signUpDto.getEmail())) {
			throw new UserException(ALREADY_EXIST);
		}

		User toSaveUser = signUpDto.toEntity(passwordEncoder);

		User savedUser = userRepository.save(toSaveUser);

		return AuthResponse.SignUpDto.toDto(savedUser);
	}

	/**
	 * 로그인
	 *
	 * @param loginDto 유저 로그인 요청 DTO
	 * @return {@link AuthResponse.LoginDto}
	 */
	@Transactional(readOnly = true)
	public AuthResponse.LoginDto login(AuthRequest.LoginDto loginDto) {

		User user = userRepository.findUserByEmailAndStatusIsActivatedOrThrow(loginDto.getEmail());

		// 비밀번호 검증
		if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
			throw new UserException(INVALID_LOGIN);

		return AuthResponse.LoginDto.toDto(user);
	}

	/**
	 * 회원수정
	 *
	 * @param updateDto 유저 수정 요청 DTO
	 * @param id 유저 식별자
	 * @return {@link UserResponse.UpdateDto}
	 */
	@Transactional
	public UserResponse.UpdateDto updateUser(UserRequest.UpdateDto updateDto, Long id) {

		// 유저 찾기
		User savedUser = userRepository.findByIdOrThrow(id);

		// 비밀번호 수정 여부 확인
		boolean isChangePassword = updateDto.getOriginPassword() != null && updateDto.getNewPassword() != null;

		// DB에 저장된 비밀번호와 유저가 입력한 origin 비밀번호 비교 검즘
		if (isChangePassword && !passwordEncoder.matches(updateDto.getOriginPassword(), savedUser.getPassword()))
			throw new UserException(PASSWORD_NOT_MATCH);

		// 비밀번호 인코딩
		String encodedNewPassword = null;

		if (isChangePassword)
			encodedNewPassword = passwordEncoder.encode(updateDto.getNewPassword());

		// 유저 수정
		userRepository.updateUser(
			id,
			updateDto.getEmail(),
			updateDto.getName(),
			updateDto.getPhoneNumber(),
			encodedNewPassword);

		User updatedUser = userRepository.findByIdOrThrow(id);

		return UserResponse.UpdateDto.toDto(updatedUser);
	}

	/**
	 * 회원탈퇴
	 *
	 * @param deleteDto 유저 삭제 요청 DTO
	 * @param id 유저 식별자
	 * @return {@link UserResponse.DeleteDto}
	 */
	@Transactional
	public UserResponse.DeleteDto deleteUser(UserRequest.DeleteDto deleteDto, Long id) {

		User savedUser = userRepository.findUserByIdAndStatusIsActivatedOrThrow(id);

		// 요청 비밀번호와 DB 비밀번호 비교
		if (!passwordEncoder.matches(deleteDto.getPassword(), savedUser.getPassword())) {
			throw new UserException(PASSWORD_NOT_MATCH);
		}

		// 유저 비활성화
		savedUser.deActivate();

		return UserResponse.DeleteDto.toDto("회원탈퇴 완료");
	}

}