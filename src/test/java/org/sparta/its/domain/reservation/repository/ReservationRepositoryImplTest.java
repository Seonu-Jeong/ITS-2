package org.sparta.its.domain.reservation.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concert.repository.ConcertRepository;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.reservation.entity.Reservation;
import org.sparta.its.domain.reservation.entity.ReservationStatus;
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
class ReservationRepositoryImplTest {

	@Autowired
	private ReservationRepository reservationQueryDslRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private HallRepository hallRepository;

	private Hall testHall;
	private Concert testConcert;
	private User testUser;

	@BeforeEach
	void setUp() {
		testHall = hallRepository.save(Hall.builder()
			.name("Test Hall")
			.location("Test Location")
			.capacity(100)
			.isOpen(true)
			.build());

		testConcert = concertRepository.save(Concert.builder()
			.hall(testHall)
			.title("Test Concert")
			.singer("Test Singer")
			.startAt(LocalDate.now().minusDays(5))
			.endAt(LocalDate.now().plusDays(5))
			.runningStartTime(LocalTime.of(19, 0))
			.runningEndTime(LocalTime.of(21, 0))
			.price(10000)
			.build());

		testUser = userRepository.save(User.builder()
			.email("test@email.com")
			.password("PAssword1234@")
			.name("Test User")
			.phoneNumber("01012345678")
			.role(Role.USER)
			.build());

		reservationQueryDslRepository.save(Reservation.builder()
			.user(testUser)
			.concert(testConcert)
			.status(ReservationStatus.PENDING)
			.concertDate(LocalDate.now())
			.build());

		reservationQueryDslRepository.save(Reservation.builder()
			.user(testUser)
			.concert(testConcert)
			.status(ReservationStatus.PENDING)
			.concertDate(LocalDate.now().plusDays(1))
			.build());
	}

	@Test
	void findReservationsByBetweenDateAndConcertInfoTest() {
		// Given
		LocalDate startDate = LocalDate.now().minusDays(1);
		LocalDate endDate = LocalDate.now().plusDays(2);
		String concertTitle = "Test Concert";
		String singer = "Test Singer";
		Pageable pageable = PageRequest.of(0, 10);

		// When
		Page<Reservation> result = reservationQueryDslRepository
			.findReservationsByBetweenDateAndConcertInfo(
				startDate,
				endDate,
				concertTitle,
				singer,
				pageable);

		// Then
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());
		List<Reservation> reservations = result.getContent();
		assertFalse(reservations.isEmpty());
		reservations.forEach(reservation -> {
			assertEquals("Test Concert", reservation.getConcert().getTitle());
			assertEquals("Test Singer", reservation.getConcert().getSinger());
			assertTrue(reservation.getConcertDate().isAfter(startDate.minusDays(1)));
			assertTrue(reservation.getConcertDate().isBefore(endDate.plusDays(1)));
		});
	}

	@Test
	void findReservationsByBetweenDateAndConcertInfoNoResultTest() {
		// Given
		LocalDate startDate = LocalDate.now().plusDays(10);
		LocalDate endDate = LocalDate.now().plusDays(20);
		String concertTitle = "Nonexistent Concert";
		String singer = "Nonexistent Singer";
		Pageable pageable = PageRequest.of(0, 10);

		// When
		Page<Reservation> result = reservationQueryDslRepository
			.findReservationsByBetweenDateAndConcertInfo(
				startDate,
				endDate,
				concertTitle,
				singer,
				pageable);

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
