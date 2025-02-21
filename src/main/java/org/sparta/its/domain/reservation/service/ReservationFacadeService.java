package org.sparta.its.domain.reservation.service;

import static org.sparta.its.global.exception.errorcode.ReservationErrorCode.*;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.its.domain.reservation.dto.ReservationResponse;
import org.sparta.its.domain.reservation.repository.ReservationRepository;
import org.sparta.its.global.exception.ReservationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 02. 02.
 * create by IntelliJ IDEA.
 *
 * 예약 관련 FacadeService.
 *
 * @author Seonu-Jeong, TaeHyeon Kim
 */
@Service
@RequiredArgsConstructor
public class ReservationFacadeService {

	private final ReservationService reservationService;
	private final ReservationRepository reservationRepository;
	private final RedissonClient redissonClient;

	@Value("${WAIT_TIME}")
	long waitTime;

	@Value("${LEASE_TIME}")
	long leaseTime;

	/**
	 * 네임드 락 기반 좌석 선택
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param seatId 좌석 고유 식별자
	 * @param date 공연 날짜
	 * @param userId 유저 고유 식별자
	 * @return {@link ReservationResponse.SelectDto}
	 */
	public ReservationResponse.SelectDto lockSelectSeat(Long concertId, Long seatId, LocalDate date, Long userId) {
		String key = keyGenerator(concertId, seatId, date);

		Boolean isGetLock = null;

		ReservationResponse.SelectDto selectDto = null;

		try {
			isGetLock = reservationRepository.getLock(key) == 1;

			if (isGetLock) {
				selectDto = reservationService.selectSeat(concertId, seatId, date, userId);
			} else {
				throw new ReservationException(TIME_OUT);
			}
		} finally {
			if (isGetLock) {
				reservationRepository.releaseLock(key);
			}
		}

		return selectDto;
	}

	/**
	 * 레디스 기반 좌석 선택
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param seatId 좌석 고유 식별자
	 * @param date 공연 날짜
	 * @param userId 유저 고유 식별자
	 * @return {@link ReservationResponse.SelectDto}
	 */
	public ReservationResponse.SelectDto redisSelectSeat(Long concertId, Long seatId, LocalDate date, Long userId) {
		RLock lock = redissonClient.getLock(keyGenerator(concertId, seatId, date));

		ReservationResponse.SelectDto resultDto = null;

		try {
			boolean acquireLock = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

			if (!acquireLock) {
				throw new ReservationException(TIME_OUT);
			}

			resultDto = reservationService.selectSeat(concertId, seatId, date, userId);
		} catch (InterruptedException e) {
			// controller advice 예외 처리 위임
			throw new RuntimeException(e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}

		return resultDto;

	}

	private String keyGenerator(Long concertId, Long seatId, LocalDate date) {
		return concertId + "/" + seatId + "/" + date;
	}

}