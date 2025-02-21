package org.sparta.its.domain.concertimage.service;

import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sparta.its.domain.concert.entity.Concert;
import org.sparta.its.domain.concertimage.dto.ConcertImageRequest;
import org.sparta.its.domain.concertimage.entity.ConcertImage;
import org.sparta.its.domain.concertimage.repository.ConcertImageRepository;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.global.exception.ConcertImageException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ConcertImageServiceTest {

	@Mock
	ConcertImageRepository concertImageRepository;

	@InjectMocks
	ConcertImageService concertImageService;

	// TODO : 예외처리 로직 테스트 중 예외 메세지 ParentException 에 기본 생성자 추가
	@Test
	@DisplayName("콘서트 이미지 고유 식별자와 콘서트 고유 식별자 검증 로직 예외 처리")
	void updatedConcertImage() {
		Long testConcertId = 2L;
		Long concertId = 1L;

		Hall hall = new Hall("잠실 경기장", "잠실", 400, true);
		Concert concert = Concert.builder()
			.hall(hall)
			.title("아이유 콘서트")
			.singer("아이유")
			.startAt(LocalDate.parse("2025-01-31"))
			.endAt(LocalDate.parse("2025-02-28"))
			.runningStartTime(LocalTime.parse("20:00"))
			.runningEndTime(LocalTime.parse("22:00"))
			.price(110000).build();
		ReflectionTestUtils.setField(concert, "id", testConcertId);

		String imageUrl = "imageUrl";
		Long concertImageId = 1L;
		ConcertImage concertImage = new ConcertImage(concert, imageUrl);
		ReflectionTestUtils.setField(concertImage, "id", concertImageId);

		BDDMockito.given(concertImageRepository.findByIdOrThrow(concertImageId)).willReturn(concertImage);

		Assertions.assertThatThrownBy(() ->
				concertImageService.updatedConcertImage(
					concertId,
					concertImageId,
					any(ConcertImageRequest.UpdateDto.class)))
			.isInstanceOf(ConcertImageException.class)
			.hasMessage(null);
	}
}
