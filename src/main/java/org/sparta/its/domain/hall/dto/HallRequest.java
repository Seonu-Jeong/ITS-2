package org.sparta.its.domain.hall.dto;

import org.sparta.its.domain.hall.entity.Hall;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장 관련 요청 DTO.
 *
 * @author TaeHyeon Kim
 */
public class HallRequest {

	@Getter
	@RequiredArgsConstructor
	public static class CreateDto {

		@NotBlank(message = "name 은 필수입니다.")
		private final String name;

		@NotBlank(message = "location 은 필수입니다.")
		private final String location;

		@NotNull(message = "capacity 은 필수입니다.")
		@Positive(message = "수용 인원은 1명 이상으로 적어주세요")
		private final Integer capacity;

		@NotNull(message = "images 는 필수입니다.")
		private final MultipartFile[] images;

		public Hall toEntity() {
			return Hall.builder()
				.name(name)
				.location(location)
				.capacity(capacity)
				.isOpen(true)
				.build();
		}
	}

	@Getter
	@RequiredArgsConstructor
	public static class UpdateDto {

		private final String name;

		private final String location;

	}
}
