package org.sparta.its.domain.concertimage.controller;

import static org.sparta.its.global.constant.GlobalConstant.*;

import org.sparta.its.domain.concertimage.dto.ConcertImageRequest;
import org.sparta.its.domain.concertimage.dto.ConcertImageResponse;
import org.sparta.its.domain.concertimage.service.ConcertImageService;
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
 * 콘서트이미지 관련 Controller.
 *
 * @author UTae Jang
 */
@RestController
@RequestMapping("/concerts/{concertId}")
@RequiredArgsConstructor
public class ConcertImageController {

	private final ConcertImageService concertImageService;

	/**
	 * 콘서트 이미지 단건 수정
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param concertImageId 콘서트 이미지 고유 식별자
	 * @param updateDto 수정 요청 Dto
	 * @return {@link ConcertImageResponse.UpdateDto}
	 */
	@PreAuthorize(ROLE_ADMIN)
	@PatchMapping("/concertImages/{concertImageId}")
	public ResponseEntity<ConcertImageResponse.UpdateDto> updateConcertImage(
		@PathVariable Long concertId,
		@PathVariable Long concertImageId,
		@Valid @ModelAttribute ConcertImageRequest.UpdateDto updateDto) {

		ConcertImageResponse.UpdateDto response = concertImageService.updatedConcertImage(concertId, concertImageId,
			updateDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
