package org.sparta.its.domain.reservation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sparta.its.domain.cancelList.repository.CancelListRepository;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concert.repository.ConcertRepository;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.hall.repository.SeatRepository;
import org.sparta.its.domain.reservation.dto.ReservationRequest;
import org.sparta.its.domain.reservation.dto.ReservationResponse;
import org.sparta.its.domain.reservation.entity.Reservation;
import org.sparta.its.domain.reservation.entity.ReservationStatus;
import org.sparta.its.domain.reservation.repository.ReservationRepository;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.sparta.its.global.exception.ReservationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

	@Mock
	private ReservationRepository reservationRepository;
	@Mock
	private SeatRepository seatRepository;
	@Mock
	private ConcertRepository concertRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CancelListRepository cancelListRepository;
	@Mock
	private HallRepository hallRepository;

	@InjectMocks
	private ReservationService reservationService;

	private LocalDate date = LocalDate.of(2025, 1, 31);
	private Reservation testReservation;
	private User testUser;
	private Hall testHall;
	private Concert testConcert;
	private Seat testSeat;

	@BeforeEach
	void setUp() {
		System.out.println("reservationService: " + reservationService); //

		// MockitoAnnotations.openMocks(this);
		testHall = Hall.builder()
			.name("Test Hall")
			.location("Test Location")
			.capacity(100)
			.isOpen(true)
			.build();

		testConcert = Concert.builder()
			.hall(testHall)
			.title("Test Concert")
			.singer("Test Singer")
			.startAt(LocalDate.now().minusDays(5))
			.endAt(LocalDate.now().plusDays(5))
			.runningStartTime(LocalTime.of(19, 0))
			.runningEndTime(LocalTime.of(21, 0))
			.price(10000)
			.build();

		ReflectionTestUtils.setField(testConcert, "id", 1L);

		testUser = User.builder()
			.email("test@email.com")
			.password("PAssword1234@")
			.name("Test User")
			.phoneNumber("01012345678")
			.role(Role.USER)
			.build();

		ReflectionTestUtils.setField(testUser, "id", 1L);

		testSeat = Mockito.mock(Seat.class);

		// Mock 데이터 설정
		testReservation = Reservation.builder()
			.user(testUser)
			.seat(testSeat)
			.concert(testConcert)
			.status(ReservationStatus.PENDING)
			.concertDate(date)
			.build();
	}

	@Test
	@DisplayName("좌석 선택 테스트")
	void testSelectSeat() {
		// given
		ReservationResponse.SelectDto selectDto = ReservationResponse.SelectDto.toDto(testReservation, date);

		// when
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(concertRepository.findByIdOrThrow(anyLong())).thenReturn(testConcert);
		when(seatRepository.findByIdOrThrow(anyLong())).thenReturn(testSeat);

		// then
		ReservationResponse.SelectDto result = reservationService.selectSeat(
			testConcert.getId(), testSeat.getId(), testConcert.getStartAt(), testUser.getId());

		assertNotNull(result);
		assertEquals(selectDto.getConcertTitle(), result.getConcertTitle());
		assertEquals(selectDto.getSeatId(), result.getSeatId());
		verify(reservationRepository, times(1)).save(any(Reservation.class));
	}

	@Test
	@DisplayName("이미 예약된 좌석 선택 테스트")
	void testSelectSeat_AlreadyBooked() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		// 예약이 이미 있는 경우
		when(reservationRepository.findReservationByConcertInfo(any(), any(), any(), any()))
			.thenReturn(Optional.of(testReservation));

		// 예외가 발생하는지 확인
		assertThrows(ReservationException.class, () -> reservationService.selectSeat(
			testConcert.getId(), testSeat.getId(), testConcert.getStartAt(), testUser.getId()));
	}

	@Test
	@DisplayName("좌석 확정 테스트")
	void testCompleteReservation() {
		// given
		ReflectionTestUtils.setField(testReservation, "id", 1L); // ID 설정
		ReflectionTestUtils.setField(testReservation, "status", ReservationStatus.PENDING); // 상태를 PENDING으로 설정
		ReservationResponse.CompleteDto completeDto = ReservationResponse.CompleteDto.toDto(testReservation);

		// when
		// when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(reservationRepository.findByIdOrThrow(anyLong())).thenReturn(testReservation);

		// then
		ReservationResponse.CompleteDto result = reservationService.completeReservation(testReservation.getId(),
			testUser.getId());

		assertNotNull(result);
		assertEquals(completeDto.getReservationId(), result.getReservationId());
	}

	@Test
	@DisplayName("예약 취소 테스트")
	void testCancelReservation() {
		// given
		ReflectionTestUtils.setField(testConcert, "startAt", LocalDate.now().plusDays(1)); // 미래 날짜 설정
		ReservationRequest.CancelDto cancelDto = new ReservationRequest.CancelDto("Test cancel");
		ReflectionTestUtils.setField(testReservation, "status", ReservationStatus.COMPLETED); // 상태를 PENDING으로 설정

		// when
		when(reservationRepository.findByIdOrThrow(testReservation.getId())).thenReturn(testReservation);

		ReservationResponse.CancelDto cancelDtoResponse = reservationService.cancelReservation(
			testReservation.getId(),
			testUser.getId(),
			cancelDto
		);

		// then
		assertNotNull(cancelDtoResponse);
		verify(cancelListRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("예약 미확정 요청 취소 테스트")
	void testCancelReservation_NotCompleted() {
		// given
		ReflectionTestUtils.setField(testReservation, "status", ReservationStatus.PENDING); // 상태 변경

		// when & then
		when(reservationRepository.findByIdOrThrow(testReservation.getId())).thenReturn(testReservation);

		assertThrows(ReservationException.class, () -> reservationService.cancelReservation(
			testReservation.getId(), testUser.getId(), new ReservationRequest.CancelDto("Test cancel")
		));
	}

	@Test
	@DisplayName("예약 조회 테스트")
	void testGetReservations() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		when(reservationRepository.findReservationsByBetweenDateAndConcertInfo(any(), any(), any(), any(), any()))
			.thenReturn(Page.empty());

		List<ReservationResponse.ReservationListDto> result = reservationService.getReservations(
			testConcert.getStartAt(), testConcert.getEndAt(), testConcert.getTitle(), testConcert.getSinger(),
			pageable);

		assertNotNull(result); // 결과가 null이 아님을 확인
		assertTrue(result.isEmpty()); // 빈 리스트인지 확인 (빈 페이지가 반환되었으므로)
	}
}
