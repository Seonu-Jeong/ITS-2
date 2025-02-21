package org.sparta.its.domain.hallImage.service;

import static org.sparta.its.global.exception.errorcode.HallImageErrorCode.*;
import static org.sparta.its.global.exception.errorcode.ImageErrorCode.*;

import java.io.IOException;

import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.hallImage.dto.HallImageRequest;
import org.sparta.its.domain.hallImage.dto.HallImageResponse;
import org.sparta.its.domain.hallImage.entity.HallImage;
import org.sparta.its.domain.hallImage.repository.HallImageRepository;
import org.sparta.its.global.exception.HallImageException;
import org.sparta.its.global.exception.ImageException;
import org.sparta.its.global.s3.S3Service;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 공연장이미지 관련 Service.
 *
 * @author TaeHyeon Kim
 */
@Service
@RequiredArgsConstructor
public class HallImageService {

	private final HallRepository hallRepository;
	private final HallImageRepository hallImageRepository;
	private final S3Service s3Service;

	/**
	 * 공연장 이미지 업데이트
	 *
	 * @param hallId 공연장 고유 식별자
	 * @param hallImagesId 공연장 이미지 고유 식별자
	 * @param updateImageDto 공연장 이미지 포맷과 이미지
	 * @return {@link HallImageResponse.UpdateDto}
	 */
	@Transactional
	public HallImageResponse.UpdateDto updateHallImage(Long hallId, Long hallImagesId,
		HallImageRequest.UpdateImageDto updateImageDto) {

		Hall findHall = hallRepository.findByIdOrThrow(hallId);

		HallImage findHallImage = hallImageRepository.findByIdOrThrow(hallImagesId);

		if (!findHallImage.getHall().getId().equals(findHall.getId())) {
			throw new HallImageException(NOT_MATCHING);
		}

		String publicUrl;

		try {
			// 기존 이미지 삭제 및 새 이미지 업로드 후 url 받아옴
			publicUrl
				= s3Service.updateImage(
				updateImageDto.getImageFormat(),
				findHall.getId(),
				findHallImage.getImageUrl(),
				updateImageDto.getImages());
		} catch (SdkClientException | IOException e) {
			throw new ImageException(FILE_UPLOAD_FAILED);
		}

		// url 업데이트
		findHallImage.updateUrl(publicUrl);

		return HallImageResponse.UpdateDto.toDto(findHallImage);
	}
}
