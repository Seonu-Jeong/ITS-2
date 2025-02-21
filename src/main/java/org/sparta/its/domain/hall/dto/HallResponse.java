package org.sparta.its.domain.hall.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hallImage.entity.HallImage;

import lombok.Builder;
import lombok.Getter;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장 관련 응답 DTO.
 *
 * @author TaeHyeon Kim
 */
public class HallResponse {

	@Getter
	@Builder
	public static class CreatDto {
		private final Long hallId;

		private final String hallName;

		private final String location;

		private final Integer capacity;

		private final LocalDateTime createdAt;

		private final List<String> imageUrls;

		public static CreatDto toDto(Hall savedHall, List<String> publicUrl) {
			return CreatDto.builder()
				.hallId(savedHall.getId())
				.hallName(savedHall.getName())
				.location(savedHall.getLocation())
				.capacity(savedHall.getCapacity())
				.createdAt(savedHall.getCreatedAt())
				.imageUrls(publicUrl)
				.build();
		}
	}

	@Getter
	@Builder
	public static class ReadDto {
		private final Long hallId;

		private final String hallName;

		private final String location;

		private final Integer capacity;

		private final LocalDateTime createdAt;

		private final LocalDateTime modifiedAt;

		private final List<String> imageUrls;

		private final Boolean isOpen;

		public static ReadDto toDto(Hall hall) {
			return ReadDto.builder()
				.hallId(hall.getId())
				.hallName(hall.getName())
				.location(hall.getLocation())
				.capacity(hall.getCapacity())
				.createdAt(hall.getCreatedAt())
				.modifiedAt(hall.getModifiedAt())
				.imageUrls(hall.getHallImages().stream().map(HallImage::getImageUrl).toList())
				.isOpen(hall.getIsOpen())
				.build();
		}
	}

	@Getter
	@Builder
	public static class UpdateDto {
		private final Long hallId;

		private final String hallName;

		private final String location;

		private final Integer capacity;

		private final LocalDateTime createdAt;

		private final LocalDateTime modifiedAt;

		private final List<String> imageUrls;

		private final Boolean isOpen;

		public static UpdateDto toDto(Hall hall) {
			return UpdateDto.builder()
				.hallId(hall.getId())
				.hallName(hall.getName())
				.location(hall.getLocation())
				.capacity(hall.getCapacity())
				.createdAt(hall.getCreatedAt())
				.modifiedAt(hall.getModifiedAt())
				.imageUrls(hall.getHallImages().stream().map(HallImage::getImageUrl).toList())
				.isOpen(hall.getIsOpen())
				.build();
		}
	}

	@Getter
	@Builder
	public static class DeleteDto {
		private final String message;

		public static DeleteDto message() {
			return DeleteDto.builder()
				.message("공연장 삭제완료")
				.build();
		}
	}
}
