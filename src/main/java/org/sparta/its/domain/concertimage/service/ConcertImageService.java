package org.sparta.its.domain.concertimage.service;

import static org.sparta.its.global.exception.errorcode.ConcertImageErrorCode.*;
import static org.sparta.its.global.exception.errorcode.ImageErrorCode.*;

import java.io.IOException;

import org.sparta.its.domain.concert.repository.ConcertRepository;
import org.sparta.its.domain.concertimage.dto.ConcertImageRequest;
import org.sparta.its.domain.concertimage.dto.ConcertImageResponse;
import org.sparta.its.domain.concertimage.entity.ConcertImage;
import org.sparta.its.domain.concertimage.repository.ConcertImageRepository;
import org.sparta.its.global.exception.ConcertImageException;
import org.sparta.its.global.exception.ImageException;
import org.sparta.its.global.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.SdkClientException;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 콘서트이미지 관련 Service.
 *
 * @author UTae Jang
 */
@Service
@RequiredArgsConstructor
public class ConcertImageService {

	private final ConcertImageRepository concertImageRepository;
	private final ConcertRepository concertRepository;
	private final S3Service s3Service;

	/**
	 * 콘서트 이미지 단건 수정
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param concertImageId 콘서트 이미지 고유 식별자
	 * @param updateDto 수정 요청 Dto
	 * @return {@link ConcertImageResponse.UpdateDto}
	 */
	@Transactional
	public ConcertImageResponse.UpdateDto updatedConcertImage(
		Long concertId,
		Long concertImageId,
		ConcertImageRequest.UpdateDto updateDto) {

		ConcertImage findConcertImage = concertImageRepository.findByIdOrThrow(concertImageId);

		if (!findConcertImage.getConcert().getId().equals(concertId)) {
			throw new ConcertImageException(NOT_MATCHING);
		}

		String publicUrl;

		try {
			// 기존 이미지 삭제 및 새 이미지 업로드 후 url 받아옴
			publicUrl = s3Service.updateImage(
				updateDto.getImageFormat(),
				concertId,
				findConcertImage.getImageUrl(),
				updateDto.getImages());
		} catch (SdkClientException | IOException e) {
			throw new ImageException(FILE_UPLOAD_FAILED);
		}

		// url 업데이트
		findConcertImage.updateImageUrl(publicUrl);

		return ConcertImageResponse.UpdateDto.toDto(findConcertImage);
	}
}
