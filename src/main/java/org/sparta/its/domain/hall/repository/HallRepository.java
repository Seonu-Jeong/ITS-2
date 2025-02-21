package org.sparta.its.domain.hall.repository;

import static org.sparta.its.global.exception.errorcode.HallErrorCode.*;

import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.global.exception.HallException;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장 관련 Repository.
 *
 * @author TaeHyeon Kim
 */
public interface HallRepository extends JpaRepository<Hall, Long>, HallQueryDslRepository {

	// 쿼리 메소드
	boolean existsByName(String name);

	Hall findHallByIdAndIsOpen(Long hallId, boolean trueStatus);

	// @Query 작성 메소드

	// Default 메소드
	default Hall findByIdOrThrow(Long hallId) {
		return findById(hallId)
			.orElseThrow(() -> new HallException(NOT_FOUND_HALL));
	}

}
