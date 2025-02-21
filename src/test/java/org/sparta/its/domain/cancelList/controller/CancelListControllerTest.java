package org.sparta.its.domain.cancelList.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.sparta.its.domain.cancelList.dto.CancelListResponse;
import org.sparta.its.domain.cancelList.entity.CancelList;
import org.sparta.its.domain.cancelList.entity.CancelStatus;
import org.sparta.its.domain.cancelList.service.CancelListService;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.global.security.JwtUtil;
import org.sparta.its.global.security.UserDetail;
import org.sparta.its.global.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = CancelListController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthorizationFilter.class),
})
public class CancelListControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	JwtUtil jwtUtil;

	@MockitoBean
	JpaMetamodelMappingContext context;

	@MockitoBean
	private CancelListService cancelListService;

	@InjectMocks
	private CancelListController cancelListController;

	private Pageable pageable;

	private User testUser;

	@BeforeEach
	public void setUp() {
		pageable = PageRequest.of(0, 5);  // 예시로 페이지 크기 5로 설정

		testUser = User.builder()
			.email("test@email.com")
			.password("Password1234!")
			.name("Test User")
			.phoneNumber("01012345678")
			.role(Role.ADMIN)
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
	@DisplayName("취소 리스트 조회 API 테스트")
	public void testGetCancelLists() throws Exception {
		// Mock 데이터 준비
		CancelList cancelList1 = CancelList.builder()
			.user(testUser)
			.rejectComment("Concert A was cancelled")
			.concertDate(LocalDate.of(2075, 1, 14))
			.status(CancelStatus.ACCEPTED)
			.concertTitle("Concert A")
			.seatNum(5)
			.build();

		CancelList cancelList2 = CancelList.builder()
			.user(testUser)
			.rejectComment("Concert B was cancelled")
			.concertDate(LocalDate.of(2075, 1, 15))
			.status(CancelStatus.REQUESTED)
			.concertTitle("Concert B")
			.seatNum(6)
			.build();

		// CancelList를 DTO로 변환
		CancelListResponse.CancelListDtoRead dto1 = CancelListResponse.CancelListDtoRead.toDto(cancelList1);
		CancelListResponse.CancelListDtoRead dto2 = CancelListResponse.CancelListDtoRead.toDto(cancelList2);
		//
		// // Mock 객체로 반환할 리스트 설정
		List<CancelListResponse.CancelListDtoRead> cancelListData = List.of(dto1, dto2);

		// // cancelListService Mocking
		// Page<CancelListResponse.CancelListDtoRead> cancelListsPage = new PageImpl<>(cancelListData, pageable,
		// 	cancelListData.size());

		when(cancelListService.getCancelLists(eq("test@example.com"), eq("Concert"), eq("ASC"), eq(pageable)))
			.thenReturn(cancelListData);

		// MockMvc로 API 호출
		mockMvc.perform(get("/cancelLists")
				.param("email", "test@example.com")
				.param("title", "Concert")
				.param("orderBy", "ASC")
				.param("page", "0")
				.param("size", "5"))
			.andExpect(status().isOk())  // HTTP 200 OK
			.andExpect(jsonPath("$[0].title").value("Concert A"))
			.andExpect(jsonPath("$[1].title").value("Concert B"))
			.andExpect(jsonPath("$[0].description").value("Concert A was cancelled"))
			.andExpect(jsonPath("$[1].description").value("Concert B was cancelled"));

		// // 서비스 메서드 호출 확인
		// verify(cancelListService, times(1))
		// 	.getCancelLists(eq("test@example.com"), eq("Concert"), eq("ASC"), eq(pageable));
	}

	@Test
	@DisplayName("빈 취소 리스트 조회 API 테스트")
	@WithMockUser(roles = "ADMIN")  // ROLE_ADMIN 권한을 가진 사용자로 테스트
	public void testGetCancelLists_Empty() throws Exception {
		// 빈 리스트 반환
		when(cancelListService.getCancelLists(eq("test@example.com"), eq("Concert"), eq("ASC"), eq(pageable)))
			.thenReturn(Collections.emptyList());

		// MockMvc로 API 호출
		mockMvc.perform(get("/cancelLists")
				.param("email", "test@example.com")
				.param("title", "Concert")
				.param("orderBy", "ASC")
				.param("page", "0")
				.param("size", "5"))
			.andExpect(status().isOk())  // HTTP 200 OK
			.andExpect(jsonPath("$").isEmpty());  // 빈 리스트여야 함

		// 서비스 메서드 호출 확인
		verify(cancelListService, times(1))
			.getCancelLists(eq("test@example.com"), eq("Concert"), eq("ASC"), eq(pageable));
	}
}
