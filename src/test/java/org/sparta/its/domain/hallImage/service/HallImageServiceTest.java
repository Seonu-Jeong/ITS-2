package org.sparta.its.domain.hallImage.service;

import static org.mockito.ArgumentMatchers.*;

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.hallImage.dto.HallImageRequest;
import org.sparta.its.domain.hallImage.dto.HallImageResponse;
import org.sparta.its.domain.hallImage.entity.HallImage;
import org.sparta.its.domain.hallImage.repository.HallImageRepository;
import org.sparta.its.global.exception.HallImageException;
import org.sparta.its.global.s3.ImageFormat;
import org.sparta.its.global.s3.S3Service;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HallImageServiceTest {

	@Mock
	private HallImageRepository hallImageRepository;

	@Mock
	private HallRepository hallRepository;

	@Mock
	private S3Service s3Service;

	@InjectMocks
	private HallImageService hallImageService;

	@Test
	@DisplayName("공연장 이미지 고유 식별자와 공연장 고유 식별자 검증 로직 예외 처리")
	void updateHallImage() {
		Long hallId = 1L;
		Long hallImageId = 1L;

		String imageUrl = "imageUrl";
		Hall hall = new Hall("잠실 경기장123", "잠실123", 400, true);
		ReflectionTestUtils.setField(hall, "id", hallId);
		// 공연장 이미지 생성 시 Mock 객체 생성
		HallImage hallImage = new HallImage(BDDMockito.mock(Hall.class), imageUrl);
		ReflectionTestUtils.setField(hallImage, "id", hallImageId);

		BDDMockito.given(hallRepository.findByIdOrThrow(hallId)).willReturn(hall);
		BDDMockito.given(hallImageRepository.findByIdOrThrow(hallImageId)).willReturn(hallImage);

		Assertions.assertThatThrownBy(() ->
				hallImageService.updateHallImage(
					hallId,
					hallImageId,
					any(HallImageRequest.UpdateImageDto.class)))
			.isInstanceOf(HallImageException.class)
			.hasMessage(null);
	}

	@Test
	@DisplayName("")
	void deleteHall_Test() throws Exception {
		Long hallId = 1L;
		Hall hall = new Hall("잠실 경기장123", "잠실123", 400, true);
		ReflectionTestUtils.setField(hall, "id", hallId);

		Long hallImageId = 2L;
		String imageUrl = "imageUrl";
		HallImage hallImage = new HallImage(hall, imageUrl);
		ReflectionTestUtils.setField(hallImage, "id", hallImageId);

		MockMultipartFile multipartFile1 = new MockMultipartFile(
			"images",
			"test1.png",
			ImageFormat.HALL.toString(),
			"test1.png".getBytes(StandardCharsets.UTF_8));

		MockMultipartFile[] images = {multipartFile1};

		HallImageRequest.UpdateImageDto requestDto = new HallImageRequest.UpdateImageDto(
			ImageFormat.HALL,
			images);
		String publicUrl = "URL 변경 완료";

		BDDMockito.given(hallRepository.findByIdOrThrow(hallId)).willReturn(hall);
		BDDMockito.given(hallImageRepository.findByIdOrThrow(hallImageId)).willReturn(hallImage);
		BDDMockito
			.given(s3Service.updateImage(requestDto.getImageFormat(), hallId, imageUrl, images))
			.willReturn(publicUrl);

		HallImageResponse.UpdateDto updateDto = hallImageService.updateHallImage(hallId, hallImageId, requestDto);

		Assertions.assertThat(updateDto.getImageUrl()).isEqualTo("URL 변경 완료");
	}
}
