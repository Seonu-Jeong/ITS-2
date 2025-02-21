package org.sparta.its.domain.hall.service;

import static org.sparta.its.global.exception.errorcode.HallErrorCode.*;
import static org.sparta.its.global.exception.errorcode.ImageErrorCode.*;

import java.io.IOException;
import java.util.List;

import org.sparta.its.domain.hall.dto.HallRequest;
import org.sparta.its.domain.hall.dto.HallResponse;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.hall.repository.SeatRepository;
import org.sparta.its.domain.hallImage.entity.HallImage;
import org.sparta.its.domain.hallImage.repository.HallImageRepository;
import org.sparta.its.global.exception.HallException;
import org.sparta.its.global.exception.ImageException;
import org.sparta.its.global.s3.ImageFormat;
import org.sparta.its.global.s3.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.SdkClientException;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장 관련 Service.
 *
 * @author TaeHyeon Kim
 */
@Service
@RequiredArgsConstructor
public class HallService {

	private final HallRepository hallRepository;
	private final HallImageRepository hallImageRepository;
	private final SeatRepository seatRepository;
	private final S3Service s3Service;

	// TODO 성능 개선 시, 사용
	// private final HallBulkRepository hallBulkRepository;

	/**
	 * 공연장을 저장 + 공연장 이미지 S3 업로드 + 업로드 URL 저장
	 *
	 * @param createDto 제목, 위치, 수용인원, 이미지
	 * @return {@link HallResponse.CreatDto}
	 */
	@Transactional
	public HallResponse.CreatDto creatHall(HallRequest.CreateDto createDto) {

		if (hallRepository.existsByName(createDto.getName())) {
			throw new HallException(DUPLICATED_NAME);
		}

		Hall savedHall = hallRepository.save(createDto.toEntity());

		List<String> publicUrls;

		// TODO 좋지 않은 방법 예외 전환으로 생각해볼 것
		try {
			// 이미지 저장
			publicUrls = s3Service.uploadImages(createDto.getImages(), ImageFormat.HALL, savedHall.getId());
		} catch (SdkClientException | IOException e) {
			throw new ImageException(FILE_UPLOAD_FAILED);
		}

		// 성능 낮음
		for (String publicUrl : publicUrls) {
			HallImage hallImage = new HallImage(savedHall, publicUrl);
			hallImageRepository.save(hallImage);
		}

		for (int i = 1; i <= savedHall.getCapacity(); i++) {
			Seat seat = new Seat(savedHall, i);
			seatRepository.save(seat);
		}

		// TODO 성능 개선 시, 사용
		// hallBulkRepository.saveAllHallImage(savedHall.getId(), publicUrls);
		// List<Integer> numberList = IntStream.rangeClosed(1, savedHall.getCapacity())
		// 	.boxed()
		// 	.toList();
		// hallBulkRepository.saveAllSeat(savedHall.getId(), numberList);

		return HallResponse.CreatDto.toDto(savedHall, publicUrls);
	}

	/**
	 * 동적 쿼리(이름, 위치에 따른) + 페이징을 통한 공연장 조회
	 *
	 * @param name 공연장 이름
	 * @param location 공연장 위치
	 * @param pageable 페이징
	 * @return  {@link HallResponse.ReadDto}
	 */
	@Transactional(readOnly = true)
	public List<HallResponse.ReadDto> getHalls(String name, String location, Pageable pageable) {

		Page<Hall> halls = hallRepository.findByNameAndLocation(name, location, pageable);

		return halls.stream().map(HallResponse.ReadDto::toDto).toList();
	}

	/**
	 * 공연장 세부 조회
	 *
	 * @param hallId 공연장 고유 식별자
	 * @return {@link HallResponse.ReadDto} dto 응답
	 */
	public HallResponse.ReadDto getDetailHall(Long hallId) {

		Hall findHall = hallRepository.findByIdOrThrow(hallId);

		return HallResponse.ReadDto.toDto(findHall);
	}

	/**
	 * 공연장 수정
	 *
	 * @param hallId 공연장 고유 식별자
	 * @param updateDto 이름, 위치
	 * @return {@link HallResponse.ReadDto} dto 응답
	 */
	@Transactional
	public HallResponse.UpdateDto updateHall(Long hallId, HallRequest.UpdateDto updateDto) {

		// 공연장 isOpen 상태가 true 인 공연장을 찾음
		Hall findHallByOpenStatus
			= hallRepository.findHallByIdAndIsOpen(hallId, true);

		if (findHallByOpenStatus == null) {
			throw new HallException(NOT_FOUND_HALL);
		}

		hallRepository.updateHall(findHallByOpenStatus.getId(), updateDto);

		Hall updateHall = hallRepository.findByIdOrThrow(hallId);
		return HallResponse.UpdateDto.toDto(updateHall);
	}

	/**
	 * 공연장 Soft Delete + 이미지 삭제 + 이미지 테이블 삭제 + s3 이미지 삭제
	 *
	 * @param hallId 공연장 고유 식별자
	 * @return {@link HallResponse.DeleteDto}
	 */
	@Transactional
	public HallResponse.DeleteDto deleteHall(Long hallId) {

		Hall findHall = hallRepository.findByIdOrThrow(hallId);

		// 삭제를 원하는 공연장에 저장된 이미지 urlList 을 받아옴
		List<String> imageUrls = findHall.getHallImages().stream().map(HallImage::getImageUrl).toList();

		// 삭제를 원하는 공연장의 공연장 이미지 테이블의 레코드들을 삭제
		hallImageRepository.deleteAllByIdInBatch(findHall.getHallImages().stream().map(HallImage::getId).toList());
		findHall.updateClosed();

		try {
			// 이미지 삭제
			s3Service.deleteImprovementDelete(hallId, ImageFormat.HALL);
		} catch (SdkClientException e) {
			throw new ImageException(FILE_DELETE_FAILED);
		}

		return HallResponse.DeleteDto.message();
	}

}
