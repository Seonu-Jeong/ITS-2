package org.sparta.its.domain.reservation.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@Profile("mysql")
public class ReservationRepositoryMySQLImpl implements ReservationNativeRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${WAIT_TIME}")
	private Long WAIT_TIME;

	@Override
	public Integer getLock(String key) {
		String sql = "SELECT GET_LOCK(?, ?)";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, key);
		query.setParameter(2, WAIT_TIME);

		// 결과는 1 (성공) 또는 0 (실패)
		Integer result = (Integer)query.getSingleResult();
		return result;
	}

	@Override
	public Integer releaseLock(String key) {
		String sql = "SELECT RELEASE_LOCK(?)";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, key);

		Integer result = (Integer)query.getSingleResult();
		return result;
	}
}