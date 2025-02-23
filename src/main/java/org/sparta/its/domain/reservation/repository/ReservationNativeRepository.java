package org.sparta.its.domain.reservation.repository;

public interface ReservationNativeRepository {

	Integer getLock(String key);

	Integer releaseLock(String key);
}