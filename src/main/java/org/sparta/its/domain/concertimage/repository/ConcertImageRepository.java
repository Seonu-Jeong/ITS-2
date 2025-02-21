package org.sparta.its.domain.concertimage.repository;

import static org.sparta.its.global.exception.errorcode.ConcertImageErrorCode.*;

import org.sparta.its.domain.concertimage.entity.ConcertImage;
import org.sparta.its.global.exception.ConcertImageException;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 콘서트이미지 관련 Repository.
 *
 * @author UTae Jang
 */
public interface ConcertImageRepository extends JpaRepository<ConcertImage, Long> {

	// 쿼리 메소드

	// @Query 작성 메소드

	// Default 메소드
	default ConcertImage findByIdOrThrow(Long concertImageId) {
		return findById(concertImageId).orElseThrow(() ->
			new ConcertImageException(NOT_FOUND));
	}

}
