package org.sparta.its.domain.cancelList.repository;

import org.sparta.its.domain.cancelList.entity.CancelList;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 취소 리스트 Repository.
 *
 * @author Jun Heo
 */
public interface CancelListRepository extends JpaRepository<CancelList, Integer>, CancelListQueryDslRepository {
	// 쿼리 메소드

	// @Query 작성 메소드

	// Default 메소드
}
