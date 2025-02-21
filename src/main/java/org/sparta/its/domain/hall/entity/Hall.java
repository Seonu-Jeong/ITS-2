package org.sparta.its.domain.hall.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.hallImage.entity.HallImage;
import org.sparta.its.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장 Entity.
 *
 * @author TaeHyeon Kim
 */
@Getter
@Entity(name = "hall")
@NoArgsConstructor
@DynamicUpdate
public class Hall extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 연관관계
	@OneToMany(mappedBy = "hall")
	private final List<HallImage> hallImages = new ArrayList<>();

	@OneToMany(mappedBy = "hall")
	private final List<Concert> concerts = new ArrayList<>();

	@OneToMany(mappedBy = "hall")
	private final List<Seat> seats = new ArrayList<>();

	// 필드
	@Column(nullable = false, unique = true, length = 30)
	private String name;

	@Column(nullable = false, length = 50)
	private String location;

	@Column(nullable = false)
	private Integer capacity;

	@Column(nullable = false)
	private Boolean isOpen;

	@Builder
	public Hall(String name, String location, Integer capacity, Boolean isOpen) {
		this.name = name;
		this.location = location;
		this.capacity = capacity;
		this.isOpen = isOpen;
	}

	public void updateClosed() {
		this.isOpen = false;
	}
}
