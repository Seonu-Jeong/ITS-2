package org.sparta.its.domain.hall.controller;

import java.util.List;

import org.sparta.its.domain.hall.dto.HallRequest;
import org.sparta.its.domain.hall.dto.HallResponse;
import org.sparta.its.domain.hall.service.HallService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장 관련 Controller.
 *
 * @author TaeHyeon Kim
 */
@RestController
@RequestMapping("/halls")
@RequiredArgsConstructor
public class HallController {

	private final HallService hallService;

	/**
	 * 공연장 등록 API
	 *
	 * @param createDto 공연장 등록 DTO
	 * @return {@link HallResponse.CreatDto}
	 */
	@PostMapping
	public ResponseEntity<HallResponse.CreatDto> createHall(
		@Valid @ModelAttribute HallRequest.CreateDto createDto) {

		HallResponse.CreatDto creatDto
			= hallService.creatHall(createDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(creatDto);
	}

	/**
	 * 공연장 정보 다건 조회 API
	 *
	 * @param name 공연장 이름
	 * @param location 공연장 위치
	 * @param pageable page, size 파라미터
	 * @return {@link HallResponse.ReadDto}
	 */
	@GetMapping
	public ResponseEntity<List<HallResponse.ReadDto>> getHalls(
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String location,
		@PageableDefault(value = 5) Pageable pageable) {

		List<HallResponse.ReadDto> halls = hallService.getHalls(name, location, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(halls);
	}

	/**
	 * 공연장 정보 상세 조회 API
	 *
	 * @param hallId 공연장 고유 식별자
	 * @return {@link HallResponse.ReadDto}
	 */
	@GetMapping("/{hallId}")
	public ResponseEntity<HallResponse.ReadDto> getDetailHall(
		@PathVariable Long hallId) {

		HallResponse.ReadDto detailHall = hallService.getDetailHall(hallId);

		return ResponseEntity.status(HttpStatus.OK).body(detailHall);
	}

	/**
	 * 공연장 정보 수정 API
	 *
	 * @param hallId 공연장 고유 식별자
	 * @param updateDto 수정 정보 Dto
	 * @return {@link HallResponse.UpdateDto}
	 */
	@PatchMapping("/{hallId}")
	public ResponseEntity<HallResponse.UpdateDto> updateHall(
		@PathVariable Long hallId,
		@RequestBody HallRequest.UpdateDto updateDto) {

		HallResponse.UpdateDto updatedHall
			= hallService.updateHall(hallId, updateDto);

		return ResponseEntity.status(HttpStatus.OK).body(updatedHall);
	}

	/**
	 * 공연장 삭제 API
	 *
	 * @param hallId 공연장 고유 식별자
	 * @return {@link HallResponse.ReadDto}
	 */
	@DeleteMapping("/{hallId}")
	public ResponseEntity<HallResponse.DeleteDto> deleteHall(
		@PathVariable Long hallId) {

		HallResponse.DeleteDto deleteDto = hallService.deleteHall(hallId);

		return ResponseEntity.status(HttpStatus.OK).body(deleteDto);
	}
}
