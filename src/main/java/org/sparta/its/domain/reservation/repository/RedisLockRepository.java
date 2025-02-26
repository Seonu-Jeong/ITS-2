package org.sparta.its.domain.reservation.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisLockRepository {
	private final RedisTemplate<String, String> redisTemplate;

	public Boolean lock(Object key) {
		return redisTemplate
			.opsForValue()
			.setIfAbsent(key.toString(), "lock", Duration.ofMillis(3000));
	}

	public Boolean unlock(Object key) {
		return redisTemplate.delete(key.toString());
	}
}