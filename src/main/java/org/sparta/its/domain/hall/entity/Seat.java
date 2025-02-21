package org.sparta.its.domain.hall.entity;

import java.util.ArrayList;
import java.util.List;

import org.sparta.its.domain.reservation.entity.Reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 좌석 Entity.
 *
 * @author TaeHyeon Kim
 */
@Getter
@Entity(name = "seat")
@NoArgsConstructor
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 연관관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hall_id")
	private Hall hall;

	@OneToMany(mappedBy = "seat")
	private final List<Reservation> reservations = new ArrayList<>();

	// 필드
	@Column(nullable = false)
	private Integer seatNumber;

	public Seat(Hall savedHall, int i) {
		this.hall = savedHall;
		this.seatNumber = i;
	}
}
