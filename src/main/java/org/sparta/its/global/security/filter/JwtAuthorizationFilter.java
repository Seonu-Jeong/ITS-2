package org.sparta.its.global.security.filter;

import static org.sparta.its.global.security.JwtUtil.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.sparta.its.global.security.JwtUtil;
import org.sparta.its.global.security.UserDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(
		HttpServletRequest req,
		HttpServletResponse res,
		FilterChain filterChain) throws ServletException, IOException, JwtException {

		String tokenValue = jwtUtil.getTokenFromRequest(req);

		if (StringUtils.hasText(tokenValue)) {
			// JWT 토큰 substring
			tokenValue = jwtUtil.substringToken(tokenValue);

			jwtUtil.validateToken(tokenValue);

			Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

			setAuthentication(info);

		}

		filterChain.doFilter(req, res);
	}

	private void setAuthentication(Claims info) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = createAuthentication(info);
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}

	private Authentication createAuthentication(Claims info) {

		Collection<GrantedAuthority> authorities = getGrantedAuthorities(info);

		UserDetail userDetail = infoToUserDetail(info);

		return new UsernamePasswordAuthenticationToken(userDetail, null, authorities);
	}

	private Collection<GrantedAuthority> getGrantedAuthorities(Claims info) {
		String authority = info.get(ROLE, String.class);

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);
		return authorities;
	}

	private UserDetail infoToUserDetail(Claims info) {
		return new UserDetail(
			info.get(USER_ID, Long.class),
			info.get(USER_NAME, String.class),
			info.getSubject()
		);
	}
}