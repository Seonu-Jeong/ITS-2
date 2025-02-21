package org.sparta.its.domain.reservation.entity;

import static org.sparta.its.global.exception.errorcode.ReservationErrorCode.*;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.global.entity.BaseEntity;
import org.sparta.its.global.exception.ReservationException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 예약 Entity.
 *
 * @author Jun Heo
 */
@Getter
@Entity(name = "reservation")
@NoArgsConstructor
@DynamicUpdate
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 연관관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id")
	private Concert concert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id")
	private Seat seat;

	// 필드
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 8)
	private ReservationStatus status;

	@Column(nullable = false)
	private LocalDate concertDate;

	@Builder
	public Reservation(User user, Concert concert, Seat seat, ReservationStatus status, LocalDate concertDate) {
		this.user = user;
		this.concert = concert;
		this.seat = seat;
		this.status = status;
		this.concertDate = concertDate;
	}

	public void completeReservation() {
		if (ReservationStatus.COMPLETED.equals(status)) {
			throw new ReservationException(ALREADY_BOOKED);
		}

		this.status = ReservationStatus.COMPLETED;
	}

	public void cancelReservation() {
		this.status = ReservationStatus.CANCEL_WAIT;
	}

}
