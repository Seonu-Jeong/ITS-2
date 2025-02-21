package org.sparta.its.domain.hallImage.dto;

import org.sparta.its.domain.hallImage.entity.HallImage;

import lombok.Builder;
import lombok.Getter;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 공연장이미지 관련 응답 DTO.
 *
 * @author TaeHyeon Kim
 */
public class HallImageResponse {

	@Getter
	@Builder
	public static class UpdateDto {
		private final Long hallId;

		private final Long hallImageId;

		private final String imageUrl;

		public static HallImageResponse.UpdateDto toDto(HallImage hallImage) {
			return UpdateDto.builder()
				.hallId(hallImage.getHall().getId())
				.hallImageId(hallImage.getId())
				.imageUrl(hallImage.getImageUrl())
				.build();
		}
	}
}
