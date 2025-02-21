package org.sparta.its.domain.hallImage.dto;

import org.sparta.its.global.s3.ImageFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 공연장이미지 관련 요청 DTO.
 *
 * @author TaeHyeon Kim
 */
public class HallImageRequest {

	@Getter
	@RequiredArgsConstructor
	public static class UpdateImageDto {

		@NotNull(message = "imageFormat 는 필수입니다.")
		private final ImageFormat imageFormat;

		@NotNull(message = "images 는 필수입니다.")
		private final MultipartFile[] images;

	}
}
