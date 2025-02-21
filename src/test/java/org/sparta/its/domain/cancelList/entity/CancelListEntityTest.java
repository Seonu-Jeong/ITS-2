package org.sparta.its.domain.cancelList.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.user.entity.User;

public class CancelListEntityTest {
	private User user;
	private CancelList cancelList;

	@BeforeEach
	void setUp() {
		// Mock 데이터 준비
		user = mock(User.class);

		// CancelList 인스턴스 생성
		cancelList = CancelList.builder()
			.user(user)
			.rejectComment("Cancelled by user.")
			.concertDate(LocalDate.of(2025, 1, 31))
			.status(CancelStatus.REQUESTED) // 상태는 PENDING으로 초기화
			.concertTitle("Concert Title")
			.seatNum(12)
			.build();
	}

	@Test
	void testCancelListCreation() {
		// given: CancelList 인스턴스가 생성됨

		// then: 필드 값이 제대로 설정되었는지 확인
		assertNotNull(cancelList);
		assertEquals(user, cancelList.getUser());
		assertEquals("Cancelled by user.", cancelList.getRejectComment());
		assertEquals(LocalDate.of(2025, 1, 31), cancelList.getConcertDate());
		assertEquals(CancelStatus.REQUESTED, cancelList.getStatus());
		assertEquals("Concert Title", cancelList.getConcertTitle());
		assertEquals(12, cancelList.getSeatNum());
	}

	@Test
	void testCancelListStatus() {
		// given: CancelList 상태는 PENDING으로 설정됨
		assertEquals(CancelStatus.REQUESTED, cancelList.getStatus());

		// when: 상태를 COMPLETED로 변경
		cancelList = CancelList.builder()
			.user(user)
			.rejectComment("Concert completed.")
			.concertDate(LocalDate.of(2025, 1, 31))
			.status(CancelStatus.ACCEPTED) // 상태 변경
			.concertTitle("Concert Title")
			.seatNum(12)
			.build();

		// then: 상태가 COMPLETED로 변경됨
		assertEquals(CancelStatus.ACCEPTED, cancelList.getStatus());
	}

	@Test
	void testCancelListRejectComment() {
		// given: rejectComment가 설정된 상태에서
		String rejectComment = cancelList.getRejectComment();

		// then: rejectComment 값이 맞는지 확인
		assertEquals("Cancelled by user.", rejectComment);
	}
}
