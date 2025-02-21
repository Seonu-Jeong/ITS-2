package org.sparta.its.domain.reservation.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.reservation.dto.ReservationResponse;
import org.sparta.its.domain.reservation.service.ReservationService;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.global.security.JwtUtil;
import org.sparta.its.global.security.UserDetail;
import org.sparta.its.global.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ReservationController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthorizationFilter.class),
})
class ReservationControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	JwtUtil jwtUtil;

	@MockitoBean
	ReservationService reservationService;

	@MockitoBean
	JpaMetamodelMappingContext context;

	private User testUser;

	@BeforeEach
	void setUp() {
		// testUser = new User(null, "testName", "testEmail");
		testUser = User.builder()
			.email("test@email.com")
			.password("Password1234!")
			.name("Test User")
			.phoneNumber("01012345678")
			.role(Role.USER)
			.build();

		ReflectionTestUtils.setField(testUser, "id", 1L);

		UserDetail userDetails = new UserDetail(testUser.getId(), testUser.getName(), testUser.getEmail());

		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, getGrantedAuthorities()));

	}

	private Collection<GrantedAuthority> getGrantedAuthorities() {
		String authority = "ROLE_USER";
		String authorityAdmin = "ROLE_ADMIN";

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		SimpleGrantedAuthority simpleGrantedAuthorityAdmin = new SimpleGrantedAuthority(authorityAdmin);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);
		authorities.add(simpleGrantedAuthorityAdmin);
		return authorities;
	}

	@Test
	@DisplayName("좌석 선택 API 테스트")
	void selectSeatTest() throws Exception {
		Long concertId = 1L;
		Long seatId = 1L;
		LocalDate date = LocalDate.now();
		Long userId = testUser.getId();

		ReservationResponse.SelectDto mockResponse = ReservationResponse.SelectDto.builder()
			.reservationId(1L)
			.seatId(seatId)
			.concertTitle("Concert Title")
			.concertDate(date)
			.build();

		when(reservationService.selectSeat(eq(concertId), eq(seatId), eq(date), eq(userId))).thenReturn(mockResponse);

		mockMvc.perform(post("/concerts/{concertId}/seats/{seatId}/select", concertId, seatId)
				.with(csrf())
				.param("date", date.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.reservationId").value(mockResponse.getReservationId()))
			.andExpect(jsonPath("$.seatId").value(mockResponse.getSeatId()))
			.andExpect(jsonPath("$.status").value(mockResponse.getStatus()))
			.andExpect(jsonPath("$.concertTitle").value(mockResponse.getConcertTitle()))
			.andExpect(jsonPath("$.concertDate").value(mockResponse.getConcertDate().toString()));
	}

	@Test
	@DisplayName("예약 확정 API 테스트")
	void completeReservationTest() throws Exception {
		// Given
		Long reservationId = 1L;
		Long userId = testUser.getId();

		ReservationResponse.CompleteDto responseDto = ReservationResponse.CompleteDto.builder()
			.reservationId(reservationId)
			.userName(testUser.getName())
			.concertTitle("Concert Title")
			.status("CONFIRMED")
			.build();

		when(reservationService.completeReservation(eq(reservationId), eq(userId))).thenReturn(responseDto);

		// When & Then
		mockMvc.perform(post("/reservations/{reservationId}", reservationId)
				.with(csrf())
				.principal(() -> "testUser"))// 인증된 사용자 정보 설정
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.reservationId").value(responseDto.getReservationId()))
			.andExpect(jsonPath("$.concertTitle").value("Concert Title"))
			.andExpect(jsonPath("$.status").value("CONFIRMED"));
	}

	@Test
	@DisplayName("예약 취소 API 테스트")
	void cancelReservationTest() throws Exception {
		Long reservationId = 1L;
		Long userId = testUser.getId();

		ReservationResponse.CancelDto mockResponse = ReservationResponse.CancelDto.builder()
			.reservationId(reservationId)
			.seatNumber(10)
			.concertDate(LocalDate.now())
			.build();

		when(reservationService.cancelReservation(eq(reservationId), eq(userId), any())).thenReturn(mockResponse);

		mockMvc.perform(post("/reservations/{reservationId}/cancel", reservationId)
				.principal(() -> "user1") // 인증 무시 설정
				.content("{\"reason\":\"Change of plans\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.reservationId").value(mockResponse.getReservationId()))
			.andExpect(jsonPath("$.seatNumber").value(mockResponse.getSeatNumber()))
			.andExpect(jsonPath("$.concertDate").value(mockResponse.getConcertDate().toString()));
	}

	@Test
	@DisplayName("예약 조회 API 테스트")
	void getAllReservationsTest() throws Exception {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = LocalDate.now().plusDays(7);

		ReservationResponse.ReservationListDto mockResponse = ReservationResponse.ReservationListDto.builder()
			.concertId(1L)
			.hallName("Main Hall")
			.concertTitle("Concert Title")
			.concertDate(startDate)
			.runningStartTime(null)
			.runningEndTime(null)
			.price(100)
			.status(null)
			.build();

		when(reservationService.getReservations(eq(startDate), eq(endDate), eq(null), eq(null), any()))
			.thenReturn(Collections.singletonList(mockResponse));

		mockMvc.perform(get("/reservations")
				.param("startDate", startDate.toString())
				.param("endDate", endDate.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].concertId").value(mockResponse.getConcertId()))
			.andExpect(jsonPath("$[0].hallName").value(mockResponse.getHallName()))
			.andExpect(jsonPath("$[0].concertTitle").value(mockResponse.getConcertTitle()))
			.andExpect(jsonPath("$[0].concertDate").value(mockResponse.getConcertDate().toString()));
	}
}
