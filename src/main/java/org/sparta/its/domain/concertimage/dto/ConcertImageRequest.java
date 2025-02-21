package org.sparta.its.domain.concertimage.dto;

import org.sparta.its.global.s3.ImageFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 콘서트이미지 관련 RequestDto.
 *
 * @author UTae Jang
 */
public class ConcertImageRequest {

	@Getter
	@RequiredArgsConstructor
	public static class UpdateDto {

		@NotNull(message = "imageFormat 은 필수값 입니다.")
		private final ImageFormat imageFormat;

		@NotNull(message = "images 는 필수값 입니다.")
		private final MultipartFile[] images;
	}
}
