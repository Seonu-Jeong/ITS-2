package org.sparta.its.domain.user.controller;

import org.sparta.its.domain.user.Service.UserService;
import org.sparta.its.domain.user.dto.UserRequest;
import org.sparta.its.domain.user.dto.UserResponse;
import org.sparta.its.global.security.UserDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 12.
 * create by IntelliJ IDEA.
 *
 * 유저 관련 Controller.
 *
 * @author Seonu-Jeong
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * 회원수정 API
	 *
	 * @param updateDto 유저 수정 DTO
	 * @param userDetail 유저 인증 객체
	 * @return {@link UserResponse.UpdateDto}
	 */
	@PatchMapping
	public ResponseEntity<UserResponse.UpdateDto> updateUser(
		@Valid @RequestBody UserRequest.UpdateDto updateDto,
		@AuthenticationPrincipal UserDetail userDetail) {

		UserResponse.UpdateDto responseDto = userService.updateUser(updateDto, userDetail.getId());

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	/**
	 * 회원탈퇴 API
	 *
	 * @param deleteDto 유저 삭제 DTO
	 * @param userDetail 유저 인증 객체
	 * @return {@link UserResponse.DeleteDto}
	 */
	@DeleteMapping
	public ResponseEntity<UserResponse.DeleteDto> deleteUser(
		@Valid @RequestBody UserRequest.DeleteDto deleteDto,
		@AuthenticationPrincipal UserDetail userDetail) {

		UserResponse.DeleteDto responseDto = userService.deleteUser(deleteDto, userDetail.getId());

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}
}