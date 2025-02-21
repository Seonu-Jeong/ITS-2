package org.sparta.its.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDetail {

	private final Long id;

	private final String name;

	private final String email;
	
}