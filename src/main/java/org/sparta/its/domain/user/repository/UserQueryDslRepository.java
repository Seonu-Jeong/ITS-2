package org.sparta.its.domain.user.repository;

/**
 * create on 2025. 01. 12.
 * create by IntelliJ IDEA.
 *
 * 유저 QueryDsl Interface.
 *
 * @author Seonu-Jeong
 */
public interface UserQueryDslRepository {

	void updateUser(Long id, String email, String name, String phoneNumber, String password);

}