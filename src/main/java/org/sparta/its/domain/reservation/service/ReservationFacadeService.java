package org.sparta.its.domain.reservation.service;

import static org.sparta.its.global.exception.errorcode.ReservationErrorCode.*;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.its.domain.reservation.dto.ReservationResponse;
import org.sparta.its.domain.reservation.repository.RedisLockRepository;
import org.sparta.its.domain.reservation.repository.ReservationNativeRepository;
import org.sparta.its.domain.reservation.repository.ReservationRepository;
import org.sparta.its.global.exception.ReservationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
	private final ReservationNativeRepository reservationNativeRepository;
	private final RedissonClient redissonClient;
	private final RedisLockRepository redisLockRepository;

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
	@Transactional
	public ReservationResponse.SelectDto lockSelectSeat(Long concertId, Long seatId, LocalDate date, Long userId) {
		String key = keyGenerator(concertId, seatId, date);

		Boolean isGetLock = null;

		ReservationResponse.SelectDto selectDto = null;

		isGetLock = reservationNativeRepository.getLock(key) == 1;

		if (isGetLock) {
			selectDto = reservationService.selectSeat(concertId, seatId, date, userId);
		} else {
			throw new ReservationException(TIME_OUT);
		}

		// 트랜잭션 완료 후 (커밋 또는 롤백) 락 해제
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				// 트랜잭션이 커밋되거나 롤백되면 락 해제
				reservationNativeRepository.releaseLock(key);
			}
		});

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
	@Transactional
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
		}

		// 트랜잭션 완료 후 (커밋 또는 롤백) 락 해제
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				// 트랜잭션이 커밋되거나 롤백되면 락 해제
				lock.unlock();
			}
		});

		return resultDto;

	}

	/**
	 * 레디스 기반 좌석 선택(Lettuce)
	 *
	 * @param concertId 콘서트 고유 식별자
	 * @param seatId 좌석 고유 식별자
	 * @param date 공연 날짜
	 * @param userId 유저 고유 식별자
	 * @return {@link ReservationResponse.SelectDto}
	 */
	@Transactional
	public ReservationResponse.SelectDto redisLettuceSelectSeat(Long concertId, Long seatId, LocalDate date,
		Long userId) {

		ReservationResponse.SelectDto resultDto = null;

		if (!redisLockRepository.lock(keyGenerator(concertId, seatId, date)))
			throw new ReservationException(ALREADY_BOOKED);

		resultDto = reservationService.selectSeat(concertId, seatId, date, userId);

		// 트랜잭션 완료 후 (커밋 또는 롤백) 락 해제
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				// 트랜잭션이 커밋되거나 롤백되면 락 해제
				redisLockRepository.unlock(keyGenerator(concertId, seatId, date));
			}
		});

		return resultDto;

	}

	private String keyGenerator(Long concertId, Long seatId, LocalDate date) {
		return concertId + "/" + seatId + "/" + date;
	}

}