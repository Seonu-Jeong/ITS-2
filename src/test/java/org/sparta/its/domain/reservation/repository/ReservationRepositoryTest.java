package org.sparta.its.domain.reservation.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concert.repository.ConcertRepository;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.hall.repository.SeatRepository;
import org.sparta.its.domain.reservation.entity.Reservation;
import org.sparta.its.domain.reservation.entity.ReservationStatus;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)  // 스프링 컨텍스트 리프레시
class ReservationRepositoryTest {

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private HallRepository hallRepository;

	private Seat testSeat;
	private Concert testConcert;
	private User testUser;
	private Reservation testReservation;

	@BeforeEach
	void setUp() {
		// Hall 생성
		Hall testHall = hallRepository.save(Hall.builder()
			.name("Test Hall")
			.location("Test Location")
			.capacity(100)
			.isOpen(true)
			.build());

		// Seat 생성
		testSeat = seatRepository.save(new Seat(testHall, 1));

		// Concert 생성
		testConcert = concertRepository.save(Concert.builder()
			.hall(testHall)
			.title("Test Concert")
			.singer("Test Singer")
			.startAt(LocalDate.now().minusDays(1))
			.endAt(LocalDate.now().plusDays(1))
			.runningStartTime(LocalTime.of(19, 0))
			.runningEndTime(LocalTime.of(21, 0))
			.price(11000)
			.build());

		// User 생성
		testUser = userRepository.save(User.builder()
			.email("test@email.com")
			.password("Password1234!")
			.name("Test User")
			.phoneNumber("01012345678")
			.role(Role.USER)
			.build());

		// Reservation 생성
		testReservation = reservationRepository.save(Reservation.builder()
			.user(testUser)
			.concert(testConcert)
			.seat(testSeat)
			.status(ReservationStatus.PENDING)
			.concertDate(LocalDate.now())
			.build());
	}

	@Test
	void findByIdOrThrowTest() {
		// When
		Reservation foundReservation = reservationRepository.findByIdOrThrow(testReservation.getId());

		// Then
		Assertions.assertThat(foundReservation).isNotNull();
		Assertions.assertThat(testReservation).isSameAs(foundReservation);
	}

	//todo 있는경우, 없는경우 따로
	@Test
	void findReservationByConcertInfoTest() {
		// When
		Optional<Reservation> result = reservationRepository.findReservationByConcertInfo(
			testSeat, testConcert, LocalDate.now(), ReservationStatus.PENDING);

		// Then
		assertTrue(result.isPresent(), "예약이 존재하지 않습니다.");
		Reservation foundReservation = result.get();
		assertEquals(testReservation.getId(), foundReservation.getId(), "예약 ID가 일치하지 않습니다.");
		assertEquals(testUser.getId(), foundReservation.getUser().getId(), "유저 ID가 일치하지 않습니다.");
		assertEquals(testConcert.getId(), foundReservation.getConcert().getId(), "콘서트 ID가 일치하지 않습니다.");
		assertEquals(testSeat.getId(), foundReservation.getSeat().getId(), "좌석 ID가 일치하지 않습니다.");
		assertEquals(LocalDate.now(), foundReservation.getConcertDate(), "콘서트 날짜가 일치하지 않습니다.");
		assertEquals(ReservationStatus.PENDING, foundReservation.getStatus(), "예약 상태가 일치하지 않습니다.");
	}
}
