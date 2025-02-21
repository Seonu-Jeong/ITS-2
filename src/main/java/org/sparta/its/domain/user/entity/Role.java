package org.sparta.its.domain.user.entity;

import static org.sparta.its.global.exception.errorcode.UserErrorCode.*;

import org.sparta.its.global.exception.UserException;

/**
 * create on 2025. 01. 07.
 * create by IntelliJ IDEA.
 *
 * 유저 역할 관련 Enum.
 *
 * @author Seonu-Jeong
 */
public enum Role {
	ADMIN, USER;

	public static Role of(String roleName) throws IllegalArgumentException {

		for (Role role : values()) {
			if (role.name().equals(roleName)) {
				return role;
			}
		}

		throw new UserException(ILLEGAL_ROLE);
	}
}