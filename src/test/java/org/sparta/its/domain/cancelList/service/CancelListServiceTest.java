package org.sparta.its.domain.cancelList.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sparta.its.domain.cancelList.dto.CancelListResponse;
import org.sparta.its.domain.cancelList.entity.CancelList;
import org.sparta.its.domain.cancelList.entity.CancelStatus;
import org.sparta.its.domain.cancelList.repository.CancelListRepository;
import org.sparta.its.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CancelListServiceTest {

	@Mock
	private CancelListRepository cancelListRepository;

	@InjectMocks
	private CancelListService cancelListService;

	private Pageable pageable;
	private User testUser;

	@BeforeEach
	public void setUp() {
		// 페이징 정보 설정 (예시: 페이지 0, 10개의 항목)
		pageable = PageRequest.of(0, 10);

		// 테스트 유저 생성
		testUser = User.builder()
			.email("test@example.com")
			.build();

		ReflectionTestUtils.setField(testUser, "id", 1L);

	}

	@Test
	@DisplayName("취소 리스트 조회 테스트")
	public void testGetCancelLists() {
		// Mock 데이터 준비
		CancelList cancelList1 = CancelList.builder()
			.user(testUser)
			.rejectComment("Concert A was cancelled")
			.concertDate(LocalDate.of(2025, 1, 14))
			.status(CancelStatus.ACCEPTED)
			.concertTitle("Concert A")
			.seatNum(5)
			.build();

		CancelList cancelList2 = CancelList.builder()
			.user(testUser)
			.rejectComment("Concert B was cancelled")
			.concertDate(LocalDate.of(2025, 1, 15))
			.status(CancelStatus.ACCEPTED)
			.concertTitle("Concert B")
			.seatNum(6)
			.build();

		// Mock 객체로 반환할 페이지 설정
		List<CancelList> cancelListData = List.of(cancelList1, cancelList2);
		Page<CancelList> cancelListsPage = new PageImpl<>(cancelListData, pageable, cancelListData.size());

		// cancelListRepository Mocking
		when(cancelListRepository.findCancelLists(any(), any(), any(), eq(pageable)))
			.thenReturn(cancelListsPage);

		// 서비스 메서드 호출
		List<CancelListResponse.CancelListDtoRead> result = cancelListService.getCancelLists(
			"test@example.com", "Concert", "title", pageable);

		// 결과 검증
		assertNotNull(result);  // 결과가 null이 아님
		assertEquals(2, result.size());  // 데이터가 두 개 있어야 함
		assertEquals("Concert A", result.get(0).getTitle());  // 첫 번째 데이터 확인
		assertEquals("Concert B", result.get(1).getTitle());  // 두 번째 데이터 확인

		// verify that the repository method was called with correct parameters
		verify(cancelListRepository, times(1))
			.findCancelLists(anyString(), anyString(), anyString(), eq(pageable));
	}

	@Test
	@DisplayName("빈 취소 리스트 조회 테스트")
	public void testGetCancelLists_Empty() {
		// 빈 페이지로 Mock 데이터 설정
		Page<CancelList> cancelListsPage = Page.empty();

		// cancelListRepository Mocking
		when(cancelListRepository.findCancelLists(anyString(), anyString(), anyString(), eq(pageable)))
			.thenReturn(cancelListsPage);

		// 서비스 메서드 호출
		List<CancelListResponse.CancelListDtoRead> result = cancelListService.getCancelLists(
			"test@example.com", "Concert", "title", pageable);

		// 결과 검증
		assertNotNull(result);  // 결과가 null이 아님
		assertTrue(result.isEmpty());  // 빈 리스트여야 함
	}
}
