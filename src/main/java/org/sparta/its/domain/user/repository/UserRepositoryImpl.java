package org.sparta.its.domain.user.repository;

import static org.sparta.its.domain.user.entity.QUser.*;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import jakarta.persistence.EntityManager;

/**
 * create on 2025. 01. 15.
 * create by IntelliJ IDEA.
 *
 * 유저 QueryDsl 인터페이스 구현체.
 *
 * @author Seonu-Jeong
 */
@Repository
public class UserRepositoryImpl implements UserQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final EntityManager entityManager;

	public UserRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
		this.entityManager = entityManager;
	}

	/**
	 * 유저 정보 수정 동적 쿼리
	 *
	 * @param id 유저 id
	 * @param email 유저 이메일
	 * @param name 수정할 이름
	 * @param phoneNumber 수정할 휴대폰 번호
	 * @param password 수정할 패스워드
	 */
	public void updateUser(Long id, String email, String name, String phoneNumber, String password) {

		JPAUpdateClause clause = jpaQueryFactory.update(user).where(user.id.eq(id));

		if (email != null) {
			clause.set(user.email, email);
		}

		if (name != null) {
			clause.set(user.name, name);
		}

		if (phoneNumber != null) {
			clause.set(user.phoneNumber, phoneNumber);
		}

		if (password != null) {
			clause.set(user.password, password);
		}

		clause.execute();

		entityManager.flush();
		entityManager.clear();
	}
}