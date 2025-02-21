package org.sparta.its.domain.cancelList.entity;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.global.entity.BaseEntity;

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
 * 취소 리스트 Entity.
 *
 * @author Jun Heo
 */
@Getter
@Entity(name = "cancel_list")
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
public class CancelList extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 연관관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// 필드
	@Column(length = 40)
	private String rejectComment;

	@Column(nullable = false)
	private LocalDate concertDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private CancelStatus status;

	@Column(nullable = false, length = 30)
	private String concertTitle;

	@Column(nullable = false)
	private Integer seatNum;

	@Builder
	public CancelList(
		User user,
		String rejectComment,
		LocalDate concertDate,
		CancelStatus status,
		String concertTitle,
		Integer seatNum) {

		this.user = user;
		this.rejectComment = rejectComment;
		this.concertDate = concertDate;
		this.status = status;
		this.concertTitle = concertTitle;
		this.seatNum = seatNum;
	}

}
