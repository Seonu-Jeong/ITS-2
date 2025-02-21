package org.sparta.its.domain.cancelList.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.cancelList.entity.CancelList;
import org.sparta.its.domain.cancelList.entity.CancelStatus;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CancelListRepositoryTest {

	@Autowired
	private CancelListRepository cancelListRepository;

	@Autowired
	private UserRepository userRepository;

	private User testUser;

	@BeforeEach
	void setUp() {
		// 유저 생성
		testUser = userRepository.save(User.builder()
			.email("test@email.com")
			.password("PAssword1234@")
			.name("Test User")
			.phoneNumber("01012345678")
			.role(Role.USER)
			.build());

		// CancelList 생성
		cancelListRepository.save(CancelList.builder()
			.user(testUser)
			.concertTitle("Test Concert")
			.status(CancelStatus.REQUESTED)
			.concertDate(LocalDate.now())
			.seatNum(1)
			.rejectComment("Test Reject Comment")
			.build());

		cancelListRepository.save(CancelList.builder()
			.user(testUser)
			.concertTitle("Another Concert")
			.status(CancelStatus.REQUESTED)
			.concertDate(LocalDate.now().plusDays(1))
			.seatNum(2)
			.rejectComment("Another Reject Comment")
			.build());
	}

	@Test
	void findCancelListsTest() {
		// Given
		String email = "test@email.com";
		String title = "Test Concert";
		String orderBy = "asc";
		Pageable pageable = PageRequest.of(0, 10);

		// When
		Page<CancelList> result = cancelListRepository.findCancelLists(email, title, orderBy, pageable);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements()); // "Test Concert"만 일치하므로 1개 결과
		List<CancelList> cancelLists = result.getContent();
		assertFalse(cancelLists.isEmpty());
		cancelLists.forEach(cancelList -> {
			assertEquals("Test Concert", cancelList.getConcertTitle());
			assertEquals("Test User", cancelList.getUser().getName());
		});
	}

	@Test
	void findCancelListsNoResultTest() {
		// Given
		String email = "test@email.com";
		String title = "Nonexistent Concert"; // 존재하지 않는 제목
		String orderBy = "asc";
		Pageable pageable = PageRequest.of(0, 10);

		// When
		Page<CancelList> result = cancelListRepository.findCancelLists(email, title, orderBy, pageable);

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty()); // 결과가 없을 것
	}

	@Test
	void findCancelListsByEmailTest() {
		// Given
		String email = "test@email.com";
		String title = null; // 제목을 기준으로 필터링하지 않음
		String orderBy = "desc";  // 내림차순으로 정렬
		Pageable pageable = PageRequest.of(0, 10);

		// When
		Page<CancelList> result = cancelListRepository.findCancelLists(email, title, orderBy, pageable);

		// Then
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());  // 두 개의 항목이 있어야 함
		assertFalse(result.getContent().isEmpty());
		result.getContent().forEach(cancelList -> {
			assertEquals("Test User", cancelList.getUser().getName());
		});
	}
}
