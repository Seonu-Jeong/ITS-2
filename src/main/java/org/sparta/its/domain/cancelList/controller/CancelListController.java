package org.sparta.its.domain.cancelList.controller;

import static org.sparta.its.global.constant.GlobalConstant.*;

import java.util.List;

import org.sparta.its.domain.cancelList.dto.CancelListResponse;
import org.sparta.its.domain.cancelList.service.CancelListService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 14.
 * create by IntelliJ IDEA.
 *
 * 취소 목록 조회 Controller.
 *
 * @author Jun Heo
 */
@RestController
@RequiredArgsConstructor
public class CancelListController {

	private final CancelListService cancelListService;

	/**
	 * 취소 리스트 조회 API
	 *
	 * @param email 유저 이메일
	 * @param title 콘서트 이름
	 * @param orderBy 정렬 방식
	 * @param pageable 페이징
	 * @return {@link CancelListResponse.CancelListDtoRead}
	 */
	@PreAuthorize(ROLE_ADMIN)
	@GetMapping("/cancelLists")
	public ResponseEntity<List<CancelListResponse.CancelListDtoRead>> getCancelLists(
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String title,
		@RequestParam(defaultValue = ORDER_ASC) String orderBy,
		@PageableDefault(value = 5) Pageable pageable) {

		List<CancelListResponse.CancelListDtoRead> cancelLists
			= cancelListService.getCancelLists(email, title, orderBy, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(cancelLists);
	}
}
