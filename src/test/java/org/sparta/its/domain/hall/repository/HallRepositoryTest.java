package org.sparta.its.domain.hall.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.hall.dto.HallRequest;
import org.sparta.its.domain.hall.entity.Hall;
import org.sparta.its.global.exception.HallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HallRepositoryTest {

	@Autowired
	private HallRepository hallRepository;

	@Test
	@DisplayName("이미 존재하는 공연장 이름 조회")
	void existsByName() {
		String name = "잠실 경기장";
		Hall hall = new Hall(name, "잠실", 400, true);

		hallRepository.save(hall);
		boolean isExist = hallRepository.existsByName(name);

		Assertions.assertThat(isExist).isTrue();
	}

	@Test
	@DisplayName("공연장 고유 식별자 및 boolean 값으로 운영중인 공연장 조회")
	void findHallByIdAndIsOpen() {
		Hall hall = new Hall("잠실 경기장", "잠실", 400, true);
		boolean trueStatus = true;

		hallRepository.save(hall);
		Hall findHall = hallRepository.findHallByIdAndIsOpen(hall.getId(), trueStatus);

		Assertions.assertThat(findHall).isNotNull();
		Assertions.assertThat(findHall.getIsOpen()).isEqualTo(true);
		Assertions.assertThat(findHall.getId()).isEqualTo(hall.getId());
	}

	@Test
	@DisplayName("공연장 고유 식별자로 공연장 조회 시 존재하지 않는 공연장 조회 시 예외 처리")
	void findByIdOrThrow() {
		Long hallId = 400L;
		Hall hall = new Hall("잠실 경기장", "잠실", 400, true);
		hallRepository.save(hall);

		Assertions.assertThatThrownBy(() -> hallRepository.findByIdOrThrow(hallId))
			.isInstanceOf(HallException.class);
	}

	@Test
	@DisplayName("공연장명 공연장 장소로 페이징 조회 정상 조회")
	void findByNameAndLocation() {
		String name = "";
		String location = "";
		Pageable pageable = PageRequest.of(0, 2);

		Hall hall1 = new Hall("잠실 경기장", "잠실", 400, true);
		Hall hall2 = new Hall("상암 경기장", "상암", 400, true);
		Hall hall3 = new Hall("수원 경기장", "수원", 400, true);

		hallRepository.save(hall1);
		hallRepository.save(hall2);
		hallRepository.save(hall3);

		Page<Hall> hallPage = hallRepository.findByNameAndLocation(name, location, pageable);

		Assertions.assertThat(hallPage.getTotalElements()).isEqualTo(3);
		Assertions.assertThat(hallPage.getTotalPages()).isEqualTo(2);
		Assertions.assertThat(hallPage.getContent().get(0).getName()).isEqualTo("상암 경기장");
	}

	@Test
	@DisplayName("공연장 정보 부분 수정 요청 정상 작동")
	void updateHall() {
		Hall hall = new Hall("잠실 경기장", "잠실", 400, true);

		HallRequest.UpdateDto requestDto =
			new HallRequest.UpdateDto("상암 경기장 수정", null);

		hallRepository.save(hall);
		hallRepository.updateHall(hall.getId(), requestDto);
		Hall findHall = hallRepository.findByIdOrThrow(hall.getId());

		Assertions.assertThat(findHall.getName()).isEqualTo("상암 경기장 수정");
		Assertions.assertThat(findHall.getLocation()).isEqualTo("잠실");
	}
}
