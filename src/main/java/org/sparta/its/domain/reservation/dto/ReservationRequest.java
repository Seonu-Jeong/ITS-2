package org.sparta.its.domain.reservation.dto;

import java.time.LocalDate;

import org.sparta.its.domain.cancelList.entity.CancelList;
import org.sparta.its.domain.cancelList.entity.CancelStatus;
import org.sparta.its.domain.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 예약 RequestDto.
 *
 * @author Jun Heo
 */
public class ReservationRequest {

	@Getter
	@RequiredArgsConstructor
	public static class CancelDto {

		@NotBlank(message = "취소 사유는 필수 입니다")
		private final String rejectComment;

		public CancelList toEntity(String concertTitle, Integer seatNum, LocalDate date, User user) {
			return CancelList.builder()
				.user(user)
				.rejectComment(rejectComment)
				.concertDate(date)
				.status(CancelStatus.REQUESTED)
				.concertTitle(concertTitle)
				.seatNum(seatNum).build();
		}
	}
}

