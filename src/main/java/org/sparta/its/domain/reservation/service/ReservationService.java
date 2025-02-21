package org.sparta.its.domain.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.sparta.its.domain.cancelList.entity.CancelList;
import org.sparta.its.domain.cancelList.repository.CancelListRepository;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concert.repository.ConcertRepository;
import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.domain.hall.repository.SeatRepository;
import org.sparta.its.domain.reservation.dto.ReservationRequest;
import org.sparta.its.domain.reservation.dto.ReservationResponse;
import org.sparta.its.domain.reservation.entity.Reservation;
import org.sparta.its.domain.reservation.entity.ReservationStatus;
import org.sparta.its.domain.reservation.repository.ReservationRepository;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.sparta.its.global.exception.ReservationException;
import org.sparta.its.global.exception.UserException;
import org.sparta.its.global.exception.errorcode.ReservationErrorCode;
import org.sparta.its.global.exception.errorcode.UserErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 25.
 * create by IntelliJ IDEA.
 *
 * 예약 관련 Service.
 *
 * @author Jun Heo
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final SeatRepository seatRepository;
	private final ConcertRepository concertRepository;
	private final UserRepository userRepository;
	private final CancelListRepository cancelListRepository;

	/**
	 * 좌석 선택
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param seatId 좌석 고유 식별자
	 * @return {@link ReservationResponse.SelectDto}
	 */
	@Transactional
	public ReservationResponse.SelectDto selectSeat(Long concertId, Long seatId, LocalDate date, Long userId) {

		// 콘서트 조회
		Concert concert = concertRepository.findByIdOrThrow(concertId);

		// 좌석 조회
		Seat seat = seatRepository.findByIdOrThrow(seatId);

		// 유저 확인
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.FORBIDDEN_ACCESS));

		// 예약 가능 여부 확인
		Optional<Reservation> existingReservation
			= reservationRepository.findReservationByConcertInfo(seat, concert, date, ReservationStatus.PENDING);

		if (existingReservation.isPresent()) {
			throw new ReservationException(ReservationErrorCode.ALREADY_BOOKED);
		}

		// 콘서트 선택 날짜 검증
		boolean isCorrectConcertDate
			= concert.getStartAt().minusDays(1).isBefore(date)
			&& concert.getEndAt().plusDays(1).isAfter(date);

		if (!isCorrectConcertDate) {
			throw new ReservationException(ReservationErrorCode.NOT_CORRECT_DATE);
		}

		// 예약 생성
		Reservation reservation = Reservation.builder()
			.user(user)
			.seat(seat)
			.concert(concert)
			.status(ReservationStatus.PENDING)
			.concertDate(date)
			.build();

		reservationRepository.save(reservation);

		return ReservationResponse.SelectDto.toDto(reservation, date);
	}

	/**
	 * 좌석 선택 완료
	 *
	 * @param reservationId 예약 고유 식별자
	 * @param userId 유저 고유 식별자
	 * @return {@link ReservationResponse.CompleteDto}
	 */
	@Transactional
	public ReservationResponse.CompleteDto completeReservation(Long reservationId, Long userId) {

		// 예약 조회
		Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);

		// 예약과 로그인 사용자 검증
		if (!reservation.getUser().getId().equals(userId)) {
			throw new UserException(UserErrorCode.FORBIDDEN_ACCESS);
		}

		reservation.completeReservation();

		return ReservationResponse.CompleteDto.toDto(reservation);
	}

	/**
	 * 예매된 좌석 취소
	 *
	 * @param reservationId 예약 고유 식별자
	 * @param requestedUserId 유저 고유 식별자
	 * @param cancelDto 취소 DTO
	 * @return {@link ReservationResponse.CancelDto}
	 */
	@Transactional
	public ReservationResponse.CancelDto cancelReservation(
		Long reservationId,
		Long requestedUserId,
		ReservationRequest.CancelDto cancelDto) {

		// 예약 찾기
		Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);

		// 예약과 로그인 사용자 검증
		Long reservedUserId = reservation.getUser().getId();

		if (!reservedUserId.equals(requestedUserId)) {
			throw new UserException(UserErrorCode.FORBIDDEN_ACCESS);
		}

		// 완료된 예약만 취소 가능
		if (!ReservationStatus.COMPLETED.equals(reservation.getStatus())) {
			throw new ReservationException(ReservationErrorCode.CANCEL_COMPLETED);
		}

		// 콘서트 시작 일자 지난 후 취소 예외 처리
		Concert concert = reservation.getConcert();

		if (concert.getStartAt().isBefore(LocalDate.now())) {
			throw new ReservationException(ReservationErrorCode.ALREADY_STARTED);
		}

		// 예약 취소 상태 변경
		reservation.cancelReservation();

		// 취소 내역 저장
		CancelList newCancelList = cancelDto.toEntity(
			concert.getTitle(),
			reservation.getSeat().getSeatNumber(),
			reservation.getConcertDate(),
			reservation.getUser());

		cancelListRepository.save(newCancelList);

		return ReservationResponse.CancelDto.toDto(reservation);
	}

	/**
	 * 예약 취소
	 *
	 * @param startAt 공연 시작 시간
	 * @param endAt 공연 끝나는 시간
	 * @param concertTitle 공연 이름
	 * @param singer 가수 이름
	 * @param pageable 페이징
	 * @return {@link ReservationResponse.ReservationListDto}
	 */
	@Transactional(readOnly = true)
	public List<ReservationResponse.ReservationListDto> getReservations(
		LocalDate startAt,
		LocalDate endAt,
		String concertTitle,
		String singer,
		Pageable pageable) {

		Page<Reservation> reservations = reservationRepository
			.findReservationsByBetweenDateAndConcertInfo(startAt, endAt, concertTitle, singer, pageable);

		return reservations.stream().map(ReservationResponse.ReservationListDto::toDto).toList();
	}
}
