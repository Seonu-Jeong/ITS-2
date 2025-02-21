package org.sparta.its.domain.user.repository;

import static org.sparta.its.global.exception.errorcode.UserErrorCode.*;

import java.util.Optional;

import org.sparta.its.domain.user.entity.User;
import org.sparta.its.global.exception.UserException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 유저 관련 Repository.
 *
 * @author Seonu-Jeong
 */
public interface UserRepository extends JpaRepository<User, Long>, UserQueryDslRepository {

	// 쿼리 메소드
	Boolean existsUserByEmail(@Param("email") String email);

	// @Query 작성 메소드
	@Query("""
		SELECT u
		FROM user u
		WHERE u.email = :email
		AND u.status = 'ACTIVATED'
		""")
	Optional<User> findUserByEmailAndStatusIsActivated(@Param("email") String email);

	@Query("""
		SELECT u
		FROM user u
		WHERE u.id = :id
		AND u.status = 'ACTIVATED'
		""")
	Optional<User> findUserByIdAndStatusIsActivated(@Param("id") Long id);

	// Default 메소드
	default User findUserByEmailAndStatusIsActivatedOrThrow(String email) {
		return findUserByEmailAndStatusIsActivated(email).orElseThrow(() ->
			new UserException(INVALID_LOGIN));
	}

	default User findByIdOrThrow(Long id) {
		return findById(id).orElseThrow(() -> new UserException(NO_EXIST_ID));
	}

	default User findUserByIdAndStatusIsActivatedOrThrow(Long id) {
		return findUserByIdAndStatusIsActivated(id).orElseThrow(() ->
			new UserException(NO_EXIST_ID));
	}
}