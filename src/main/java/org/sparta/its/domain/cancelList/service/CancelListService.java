package org.sparta.its.domain.cancelList.service;

import java.util.List;

import org.sparta.its.domain.cancelList.dto.CancelListResponse;
import org.sparta.its.domain.cancelList.entity.CancelList;
import org.sparta.its.domain.cancelList.repository.CancelListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 14.
 * create by IntelliJ IDEA.
 *
 * 취소 리스트 Service.
 *
 * @author Jun Heo
 */
@Service
@RequiredArgsConstructor
public class CancelListService {

	private final CancelListRepository cancelListRepository;

	/**
	 * 취소 리스트 조회
	 *
	 * @param email 유저 이메일
	 * @param title 콘서트 이름
	 * @param orderBy 정렬 방식
	 * @param pageable 페이징
	 * @return {@link CancelListResponse.CancelListDtoRead}
	 */
	@Transactional(readOnly = true)
	public List<CancelListResponse.CancelListDtoRead> getCancelLists(
		String email,
		String title,
		String orderBy,
		Pageable pageable) {

		Page<CancelList> cancelLists
			= cancelListRepository.findCancelLists(email, title, orderBy, pageable);

		return cancelLists.stream().map(CancelListResponse.CancelListDtoRead::toDto).toList();
	}

}
