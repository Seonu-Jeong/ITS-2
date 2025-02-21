package org.sparta.its.global.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparta.its.domain.user.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUtil {
	// Header KEY 값
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// 사용자 권한 값의 KEY
	public static final String ROLE = "role";
	// 사용자 ID 값
	public static final String USER_ID = "id";
	// 사용자 이름
	public static final String USER_NAME = "name";
	// Token 식별자
	public static final String BEARER_PREFIX = "Bearer ";
	// 토큰 만료시간
	private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

	@Value("${JWT_SECRET_KEY}") // Base64 Encode 한 SecretKey
	private String secretKey;
	private Key key;
	private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	// 로그 설정
	public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

	@PostConstruct
	public void init() {
		byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
		key = Keys.hmacShaKeyFor(bytes);
	}

	// 토큰 생성
	public String createToken(Long id, String userEmail, String name, Role role) {
		final Date date = new Date();

		final String token = BEARER_PREFIX +
			Jwts.builder()
				.setSubject(userEmail) // 사용자 식별자값(ID)
				.claim(ROLE, role) // 사용자 권한
				.claim(USER_ID, id)
				.claim(USER_NAME, name)
				.setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
				.setIssuedAt(date) // 발급일
				.signWith(key, signatureAlgorithm) // 암호화 알고리즘
				.compact();

		return token;
	}

	public void addJwtToHeader(String token, HttpServletResponse res) {
		res.addHeader(AUTHORIZATION_HEADER, token);
	}

	// JWT 토큰 substring
	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		logger.error("Not Found Token");
		throw new NullPointerException("Not Found Token");
	}

	// 토큰 검증
	public void validateToken(String token) throws JwtException {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
		} catch (SecurityException | MalformedJwtException e) {
			throw new JwtException("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			throw new JwtException("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			throw new JwtException("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			throw new JwtException("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
	}

	// 토큰에서 사용자 정보 가져오기
	public Claims getUserInfoFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	// HttpServletRequest 에서 JWT 가져오기
	public String getTokenFromRequest(HttpServletRequest req) {

		// 헤더에서 AccessToken 가져오기
		String requestKey = req.getHeader(AUTHORIZATION_HEADER);

		return requestKey;
	}
}