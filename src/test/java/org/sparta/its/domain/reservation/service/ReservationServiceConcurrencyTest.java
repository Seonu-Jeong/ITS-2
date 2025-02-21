package org.sparta.its.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.sparta.its.global.exception.ReservationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest
@SqlGroup({
	@Sql(
		value = {"/sql/domain/reservation/service/concurrency-setup.sql"},
		config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	),
	@Sql(
		value = {"/sql/domain/reservation/service/concurrency-delete.sql"},
		config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
	)
})
public class ReservationServiceConcurrencyTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReservationFacadeService reservationService;

	@Test
	@DisplayName("좌석 임시 선택 동시성 테스트 - 네임드 락 기반")
	public void lockSelectSeatTest() throws InterruptedException {
		//given
		List<User> users = userRepository.findAll();
		Long concertId = 1L;
		Long seatId = 1L;
		LocalDate date = LocalDate.of(2025, 5, 05);

		int numThreads = 10;
		CountDownLatch doneSignal = new CountDownLatch(numThreads);
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		// when
		for (int i = 0; i < numThreads; i++) {
			int userIdx = i;
			executorService.execute(() -> {
				try {
					reservationService.lockSelectSeat(concertId, seatId, date, users.get(userIdx).getId());

					successCount.getAndIncrement();
				} catch (ReservationException e) {
					failCount.getAndIncrement();
				} finally {
					doneSignal.countDown();
				}
			});
		}
		doneSignal.await();
		executorService.shutdown();

		//then
		assertAll(
			() -> assertThat(successCount.get()).isEqualTo(1),
			() -> assertThat(failCount.get()).isEqualTo(9)
		);
	}

	@Test
	@DisplayName("좌석 임시 선택 동시성 테스트 - redis 기반")
	public void redisSelectSeatTest() throws InterruptedException {
		//given
		List<User> users = userRepository.findAll();
		Long concertId = 1L;
		Long seatId = 1L;
		LocalDate date = LocalDate.of(2025, 5, 05);

		int numThreads = 10;
		CountDownLatch doneSignal = new CountDownLatch(numThreads);
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		// when
		for (int i = 0; i < numThreads; i++) {
			int userIdx = i;
			executorService.execute(() -> {
				try {
					reservationService.redisSelectSeat(concertId, seatId, date, users.get(userIdx).getId());

					successCount.getAndIncrement();
				} catch (ReservationException e) {
					failCount.getAndIncrement();
				} finally {
					doneSignal.countDown();
				}
			});
		}
		doneSignal.await();
		executorService.shutdown();

		//then
		assertAll(
			() -> assertThat(successCount.get()).isEqualTo(1),
			() -> assertThat(failCount.get()).isEqualTo(9)
		);
	}
}