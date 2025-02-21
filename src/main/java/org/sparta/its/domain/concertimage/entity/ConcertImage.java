package org.sparta.its.domain.concertimage.entity;

import org.sparta.its.domain.concert.entity.Concert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * create on 2025. 01. 13.
 * create by IntelliJ IDEA.
 *
 * 콘서트이미지 Entity.
 *
 * @author UTae Jang
 */
@Getter
@Entity(name = "concert_image")
@NoArgsConstructor
public class ConcertImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 연관관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id")
	private Concert concert;

	// 필드
	@Column(nullable = false, length = 255)
	private String imageUrl;

	@Builder
	public ConcertImage(Concert saveConcert, String publicUrl) {
		this.concert = saveConcert;
		this.imageUrl = publicUrl;
	}

	public void updateImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
