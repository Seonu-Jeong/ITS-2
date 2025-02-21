package org.sparta.its.domain.concert.controller;

import static org.sparta.its.global.constant.GlobalConstant.*;

import java.time.LocalDate;
import java.util.List;

import org.sparta.its.domain.concert.dto.ConcertRequest;
import org.sparta.its.domain.concert.dto.ConcertResponse;
import org.sparta.its.domain.concert.service.ConcertService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 08.
 * create by IntelliJ IDEA.
 *
 * 콘서트 관련 Controller.
 *
 * @author UTae Jang
 */
@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {

	private final ConcertService concertService;

	/**
	 * 콘서트 등록 API
	 *
	 * @param createDto 생성 요청 DTO
	 * @return {@link ConcertResponse.CreateDto}
	 */
	@PreAuthorize(ROLE_ADMIN)
	@PostMapping
	public ResponseEntity<ConcertResponse.CreateDto> createConcert(
		@Valid @ModelAttribute ConcertRequest.CreateDto createDto) {

		ConcertResponse.CreateDto response = concertService.createConcert(createDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 콘서트 다건 조회 API
	 *
	 * @param singer 가수명
	 * @param concertTitle 콘서트명
	 * @param order 정릴 방식
	 * @param pageable 페이징
	 * @return {@link ConcertResponse.ReadDto}
	 */
	@GetMapping
	public ResponseEntity<List<ConcertResponse.ReadDto>> getConcerts(
		@RequestParam(required = false) String singer,
		@RequestParam(required = false) String concertTitle,
		@RequestParam(defaultValue = ORDER_DESC) String order,
		@PageableDefault Pageable pageable) {

		List<ConcertResponse.ReadDto> allConcertDto = concertService.getConcerts(singer, concertTitle, order, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(allConcertDto);
	}

	/**
	 * 콘서트 상세 조회 API
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @return {@link ConcertResponse.ReadDto}
	 * */
	@GetMapping("/{concertId}")
	public ResponseEntity<ConcertResponse.ReadDto> getDetailConcert(
		@PathVariable Long concertId) {

		ConcertResponse.ReadDto response = concertService.getDetailConcert(concertId);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 콘서트 정보 수정 API
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param updateDto 수정 요청 DTO
	 * @return {@link ConcertResponse.UpdateDto}
	 */
	@PreAuthorize(ROLE_ADMIN)
	@PatchMapping("/{concertId}")
	public ResponseEntity<ConcertResponse.UpdateDto> updateConcert(
		@PathVariable Long concertId,
		@RequestBody ConcertRequest.UpdateDto updateDto) {

		ConcertResponse.UpdateDto response = concertService.updatedConcert(concertId, updateDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 콘서트 등록 현황 조회 API
	 *
	 * @param title 콘서트명
	 * @param startAt 콘서트 시작 날짜
	 * @param endAt 콘서트 종료 날짜
	 * @param order 정렬 방식
	 * @param pageable 페이징
	 * @return {@link ConcertResponse.StatisticsDto}
	 */
	@PreAuthorize(ROLE_ADMIN)
	@GetMapping("/statistics")
	public ResponseEntity<List<ConcertResponse.StatisticsDto>> getStatistics(
		@RequestParam(required = false) String title,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
		@RequestParam(defaultValue = ORDER_DESC) String order,
		@PageableDefault Pageable pageable) {

		List<ConcertResponse.StatisticsDto> response
			= concertService.getStatistics(title, startAt, endAt, order, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 콘서트 자리 조회 API
	 *
	 * @param concertId  콘서트 고유 식별자
	 * @param date 콘서트 날짜
	 * @return {@link ConcertResponse.ConcertSeatDto}
	 */
	@PreAuthorize(ROLE_USER)
	@GetMapping("/{concertId}/seats")
	public ResponseEntity<List<ConcertResponse.ConcertSeatDto>> getConcertSeats(
		@PathVariable Long concertId,
		@RequestParam(required = false) LocalDate date) {

		List<ConcertResponse.ConcertSeatDto> concertSeats = concertService.getConcertSeats(concertId, date);

		return ResponseEntity.status(HttpStatus.OK).body(concertSeats);
	}
}
