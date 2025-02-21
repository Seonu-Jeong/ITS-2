package org.sparta.its.domain.concert.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concertimage.entity.ConcertImage;
import org.sparta.its.domain.reservation.entity.ReservationStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 콘서트 관련 ResponseDto.
 *
 * @author UTae Jang
 */
public class ConcertResponse {

	@Getter
	@Builder
	public static class CreateDto {
		private final Long id;

		private final Long hallId;

		private final String title;

		private final String singer;

		private final LocalDate startAt;

		private final LocalDate endAt;

		private final LocalTime runningStartTime;

		private final LocalTime runningEndTime;

		private final Integer price;

		private final List<String> images;

		public static CreateDto toDto(Concert concert, List<String> imageUrls) {
			return CreateDto.builder()
				.id(concert.getId())
				.hallId(concert.getHall().getId())
				.title(concert.getTitle())
				.singer(concert.getSinger())
				.startAt(concert.getStartAt())
				.endAt(concert.getEndAt())
				.runningStartTime(concert.getRunningStartTime())
				.runningEndTime(concert.getRunningEndTime())
				.price(concert.getPrice())
				.images(imageUrls)
				.build();
		}
	}

	@Getter
	@Builder
	public static class ReadDto {
		private final Long id;

		private final String hallName;

		private final String title;

		private final String singer;

		private final LocalDate startAt;

		private final LocalDate endAt;

		private final LocalTime runningStartTime;

		private final LocalTime runningEndTime;

		private final Integer price;

		private final List<String> images;

		public static ReadDto toDto(Concert concert) {
			return ReadDto.builder()
				.id(concert.getId())
				.hallName(concert.getHall().getName())
				.title(concert.getTitle())
				.singer(concert.getSinger())
				.startAt(concert.getStartAt())
				.endAt(concert.getEndAt())
				.runningStartTime(concert.getRunningStartTime())
				.runningEndTime(concert.getRunningEndTime())
				.price(concert.getPrice())
				.images(concert.getConcertImages().stream().map(ConcertImage::getImageUrl).toList())
				.build();
		}
	}

	@Getter
	@Builder
	public static class UpdateDto {

		private final Long id;

		private final String hallName;

		private final String title;

		private final String singer;

		private final LocalDate startAt;

		private final LocalDate endAt;

		private final LocalTime runningStartTime;

		private final LocalTime runningEndTime;

		private final Integer price;

		private final List<String> images;

		public static UpdateDto toDto(Concert concert) {
			return UpdateDto.builder()
				.id(concert.getId())
				.hallName(concert.getHall().getName())
				.title(concert.getTitle())
				.singer(concert.getSinger())
				.startAt(concert.getStartAt())
				.endAt(concert.getEndAt())
				.runningStartTime(concert.getRunningStartTime())
				.runningEndTime(concert.getRunningEndTime())
				.price(concert.getPrice())
				.images(concert.getConcertImages().stream().map(ConcertImage::getImageUrl).toList())
				.build();
		}
	}

	@Getter
	@Builder
	public static class StatisticsDto {

		private final Long concertId;

		private final String concertTitle;

		private final Integer allSeat;

		private final Integer reservationSeat;

		private final Integer sumPrice;

		private final LocalDate startAt;

		public StatisticsDto(
			Long concertId,
			String concertTitle,
			Integer allSeat,
			Integer reservationSeat,
			Integer sumPrice,
			LocalDate startAt) {

			this.concertId = concertId;
			this.concertTitle = concertTitle;
			this.allSeat = allSeat;
			this.reservationSeat = reservationSeat;
			this.sumPrice = sumPrice;
			this.startAt = startAt;
		}
	}

	@Getter
	public static class ConcertSeatDto {

		private final Long seatId;

		private final Integer seatNumber;

		private final String status;

		public ConcertSeatDto(Long seatId, Integer seatNumber, ReservationStatus reservationStatus) {
			this.seatId = seatId;
			this.seatNumber = seatNumber;
			this.status = reservationStatus == null ? "AVAILABLE" : reservationStatus.toString();
		}
	}
}
