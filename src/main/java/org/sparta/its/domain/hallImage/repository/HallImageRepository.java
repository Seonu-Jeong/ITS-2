package org.sparta.its.domain.hallImage.repository;

import static org.sparta.its.global.exception.errorcode.HallImageErrorCode.*;

import org.sparta.its.domain.hallImage.entity.HallImage;
import org.sparta.its.global.exception.HallImageException;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장이미지 관련 Repository.
 *
 * @author TaeHyeon Kim
 */
public interface HallImageRepository extends JpaRepository<HallImage, Long> {

	// 쿼리 메소드

	// @Query 작성 메소드

	// Default 메소드
	default HallImage findByIdOrThrow(Long id) {
		return findById(id)
			.orElseThrow(() -> new HallImageException(NOT_FOUND_HALL_IMAGE));
	}
}
