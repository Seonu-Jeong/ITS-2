package org.sparta.its.domain.cancelList.repository;

import org.sparta.its.domain.cancelList.entity.CancelList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * create on 2025. 01. 04.
 * create by IntelliJ IDEA.
 *
 * 취소 리스트 QueryDsl Repository.
 *
 * @author Jun Heo
 */
public interface CancelListQueryDslRepository {
	Page<CancelList> findCancelLists(
		String email,
		String title,
		String orderBy,
		Pageable pageable);
}
