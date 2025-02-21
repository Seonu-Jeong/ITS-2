package org.sparta.its.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.sparta.its.domain.cancelList.entity.CancelList;
import org.sparta.its.domain.reservation.entity.Reservation;
import org.sparta.its.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * create on 2025. 01. 07.
 * create by IntelliJ IDEA.
 *
 * 유저 Entity.
 *
 * @author Seonu-Jeong
 */
@Getter
@Entity(name = "user")
@NoArgsConstructor
@DynamicUpdate
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 연관관계
	@OneToMany(mappedBy = "user")
	private List<CancelList> cancelLists = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Reservation> reservations = new ArrayList<>();

	// 필드
	@Column(unique = true, nullable = false, length = 255)
	private String email;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 20)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 15)
	private Status status = Status.ACTIVATED;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Role role;

	@Builder
	public User(String email, String password, String name, String phoneNumber, Role role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	public void deActivate() {
		this.status = Status.DEACTIVATED;
	}

}