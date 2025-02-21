package org.sparta.its.domain.hallImage.controller;

import static org.sparta.its.global.constant.GlobalConstant.*;

import org.sparta.its.domain.hallImage.dto.HallImageRequest;
import org.sparta.its.domain.hallImage.dto.HallImageResponse;
import org.sparta.its.domain.hallImage.service.HallImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 공연장이미지 관련 Controller.
 *
 * @author TaeHyeon Kim
 */
@RestController
@RequestMapping("/halls/{hallId}")
@RequiredArgsConstructor
public class HallImageController {

	private final HallImageService hallImageService;

	/**
	 * 공연장 이미지 단건 수정 API
	 *
	 * @param hallId 공연장 고유 식별자
	 * @param updateImageDto 공연장 이미지 요청 DTO
	 * @return {@link HallImageResponse.UpdateDto}
	 */
	@PreAuthorize(ROLE_ADMIN)
	@PatchMapping("/hallImages/{hallImagesId}")
	public ResponseEntity<HallImageResponse.UpdateDto> updateHallImage(
		@PathVariable Long hallId,
		@PathVariable Long hallImagesId,
		@Valid @ModelAttribute HallImageRequest.UpdateImageDto updateImageDto) {

		HallImageResponse.UpdateDto updateDto
			= hallImageService.updateHallImage(hallId, hallImagesId, updateImageDto);

		return ResponseEntity.status(HttpStatus.OK).body(updateDto);
	}
}
