package org.sparta.its.domain.hall.service;

import static org.mockito.ArgumentMatchers.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sparta.its.domain.hall.dto.HallRequest;
import org.sparta.its.domain.hall.dto.HallResponse;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.domain.hall.entity.Seat;
import org.sparta.its.domain.hall.repository.HallRepository;
import org.sparta.its.domain.hall.repository.SeatRepository;
import org.sparta.its.domain.hallImage.entity.HallImage;
import org.sparta.its.domain.hallImage.repository.HallImageRepository;
import org.sparta.its.global.exception.HallException;
import org.sparta.its.global.s3.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HallServiceTest {

	@Mock
	private HallRepository hallRepository;

	@Mock
	private HallImageRepository hallImageRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private S3Service s3Service;

	@InjectMocks
	private HallService hallService;

	@Test
	@DisplayName("공연장 이름 중복 예외 처리")
	void duplicatedHallName_Test() {
		MockMultipartFile multipartFile1 = Mockito.mock(MockMultipartFile.class);
		MockMultipartFile multipartFile2 = Mockito.mock(MockMultipartFile.class);
		MockMultipartFile[] images = {multipartFile1, multipartFile2};

		HallRequest.CreateDto requestDto = new HallRequest.CreateDto(
			"올림픽 경기장",
			"잠실",
			400,
			images);

		BDDMockito.given(hallRepository.existsByName(requestDto.getName())).willReturn(true);

		// todo : 공연장 이름 중복 exception -> imageException ?
		Assertions.assertThatThrownBy(() -> hallService.creatHall(requestDto))
			.isInstanceOf(HallException.class)
			.hasMessage(null);
	}

	@Test
	@DisplayName("공연장 등록 및 공연장 이미지 테스트 정상 작동")
	void createHallAndCreateHallImage_TEST() throws Exception {

		List<String> imageUrls = List.of("imageUrl1", "imageUrl2");
		HallImage hallImage = Mockito.mock(HallImage.class);

		MockMultipartFile multipartFile1 = Mockito.mock(MockMultipartFile.class);
		MockMultipartFile multipartFile2 = Mockito.mock(MockMultipartFile.class);
		MockMultipartFile[] images = {multipartFile1, multipartFile2};

		HallRequest.CreateDto requestDto = new HallRequest.CreateDto(
			"올림픽 경기장",
			"잠실",
			400,
			images);

		Long hallId = 1L;
		Hall hall = requestDto.toEntity();
		ReflectionTestUtils.setField(hall, "id", hallId);

		Seat seat = new Seat(hall, 1);

		BDDMockito.given(hallRepository.save(any(Hall.class))).willReturn(hall);
		BDDMockito.given(hallImageRepository.save(any(HallImage.class))).willReturn(hallImage);
		BDDMockito.given(s3Service.uploadImages(any(), any(), anyLong())).willReturn(imageUrls);
		BDDMockito.given(seatRepository.save(any(Seat.class))).willReturn(seat);

		HallResponse.CreatDto responseDto = hallService.creatHall(requestDto);
		ReflectionTestUtils.setField(responseDto, "imageUrls", imageUrls);

		Assertions.assertThat(responseDto).isNotNull();
		Assertions.assertThat(responseDto.getHallName()).isEqualTo("올림픽 경기장");
		Assertions.assertThat(responseDto.getImageUrls().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("공연장 다건 조회 페이징 정상 작동 테스트")
	void getHalls_Test() {
		String name = "";
		String location = "";
		Pageable pageable = PageRequest.of(0, 2);

		Long hallId1 = 1L;
		Hall hall1 = new Hall("올림픽 경기장1", "잠실1", 400, true);
		ReflectionTestUtils.setField(hall1, "id", hallId1);

		Long hallId2 = 2L;
		Hall hall2 = new Hall("올림픽 경기장2", "잠실2", 400, true);
		ReflectionTestUtils.setField(hall2, "id", hallId2);

		Long hallId3 = 3L;
		Hall hall3 = new Hall("올림픽 경기장3", "잠실3", 400, true);
		ReflectionTestUtils.setField(hall3, "id", hallId3);

		List<Hall> hallList = List.of(hall1, hall2, hall3);
		Page<Hall> responseDtoPage = new PageImpl(hallList, pageable, hallList.size());

		BDDMockito
			.given(hallRepository.findByNameAndLocation(name, location, pageable))
			.willReturn(responseDtoPage);

		List<HallResponse.ReadDto> readDtoList = hallService.getHalls(name, location, pageable);

		Assertions.assertThat(readDtoList).isNotNull();
		Assertions.assertThat(readDtoList.size()).isEqualTo(3);
		Assertions.assertThat(readDtoList.get(0).getHallId()).isEqualTo(1L);
		Assertions.assertThat(readDtoList.get(1).getHallId()).isEqualTo(2L);
		Assertions.assertThat(readDtoList.get(2).getHallId()).isEqualTo(3L);
	}

	@Test
	@DisplayName("운영하지 않는 공연장 조회 시 예외 처리")
	void updateHall() {
		Long hallId = 1L;
		HallRequest.UpdateDto requestDto = new HallRequest.UpdateDto(
			"서울 상암 경기장",
			"서울");

		Hall hall = new Hall("올림픽 경기장1", "잠실1", 400, true);
		ReflectionTestUtils.setField(hall, "id", hallId);

		Assertions.assertThatThrownBy(() -> hallService.updateHall(hallId, requestDto))
			.isInstanceOf(HallException.class);
	}

	@Test
	@DisplayName("공연장 정보 수정 요청 정상 작동")
	void updated_Hall() {
		Long hallId = 1L;
		HallRequest.UpdateDto requestDto = new HallRequest.UpdateDto(
			"서울 상암 경기장",
			"서울");

		Hall hall = new Hall("올림픽 경기장", "잠실", 400, true);
		ReflectionTestUtils.setField(hall, "id", hallId);

		BDDMockito.given(hallRepository.findHallByIdAndIsOpen(hallId, true)).willReturn(hall);
		BDDMockito.given(hallRepository.findByIdOrThrow(hallId)).willReturn(hall);

		HallResponse.UpdateDto updateDto = hallService.updateHall(hallId, requestDto);

		Assertions.assertThat(updateDto).isNotNull();
		Assertions.assertThat(updateDto.getHallId()).isEqualTo(hallId);
		Assertions.assertThat(updateDto.getHallName()).isEqualTo("올림픽 경기장");
		Assertions.assertThat(updateDto.getLocation()).isEqualTo("잠실");
	}

	@Test
	@DisplayName("공연장 삭제 시 상태 값 false 변경")
	void deleteHall() {
		Long hallId = 1L;
		Hall hall = new Hall("올림픽 경기장", "잠실", 400, true);
		ReflectionTestUtils.setField(hall, "id", hallId);

		BDDMockito.given(hallRepository.findByIdOrThrow(hallId)).willReturn(hall);
		HallResponse.DeleteDto deleteDto = hallService.deleteHall(hallId);

		Assertions.assertThat(deleteDto.getMessage()).isEqualTo("공연장 삭제완료");
		Assertions.assertThat(hall.getIsOpen()).isEqualTo(false);
	}

	@Test
	@DisplayName("공연장 삭제 시 해당 공연장 이미지 삭제 정상 작동")
	void deleteHallAndHallImages() {
		Long hallId = 1L;
		Hall hall = Mockito.mock(Hall.class);
		ReflectionTestUtils.setField(hall, "id", hallId);

		BDDMockito.given(hallRepository.findByIdOrThrow(hallId)).willReturn(hall);
		HallResponse.DeleteDto responseDto = hallService.deleteHall(hallId);

		BDDMockito.verify(hall, Mockito.times(1)).updateClosed();
		Assertions.assertThat(responseDto.getMessage()).isEqualTo("공연장 삭제완료");
		Assertions.assertThat(hall.getIsOpen()).isEqualTo(false);
	}
}
