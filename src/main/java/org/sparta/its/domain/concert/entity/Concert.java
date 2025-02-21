package org.sparta.its.domain.concert.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.sparta.its.domain.concertimage.entity.ConcertImage;
import org.sparta.its.domain.hall.entity.Hall;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * create on 2025. 01. 07.
 * create by IntelliJ IDEA.
 *
 * 콘서트 Entity.
 *
 * @author UTae Jang
 */
@Getter
@Entity(name = "concert")
@NoArgsConstructor
@DynamicUpdate
public class Concert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 연관관계
	@OneToMany(mappedBy = "concert")
	private final List<ConcertImage> concertImages = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hall_id")
	private Hall hall;

	@OneToMany(mappedBy = "concert")
	private final List<Reservation> reservations = new ArrayList<>();

	// 필드
	@Column(nullable = false, length = 30)
	private String title;

	@Column(nullable = false, length = 30)
	private String singer;

	@Column(nullable = false)
	private LocalDate startAt;

	@Column(nullable = false)
	private LocalDate endAt;

	@Column(nullable = false)
	private LocalTime runningStartTime;

	@Column(nullable = false)
	private LocalTime runningEndTime;

	@Column(nullable = false)
	private Integer price;

	@Builder
	public Concert(
		Hall hall,
		String title,
		String singer,
		LocalDate startAt,
		LocalDate endAt,
		LocalTime runningStartTime,
		LocalTime runningEndTime,
		Integer price) {

		this.hall = hall;
		this.title = title;
		this.singer = singer;
		this.startAt = startAt;
		this.endAt = endAt;
		this.runningStartTime = runningStartTime;
		this.runningEndTime = runningEndTime;
		this.price = price;
	}
}

