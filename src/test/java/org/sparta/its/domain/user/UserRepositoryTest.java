package org.sparta.its.domain.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sparta.its.domain.user.entity.Role;
import org.sparta.its.domain.user.entity.User;
import org.sparta.its.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("email로 유저 존재여부 확인 테스트")
	public void existsUserByEmailTest() {

		// given
		String email = "test@gmail.com";

		User user = new User(
			email,
			"Password1234@",
			"testName",
			"010-1234-5679",
			Role.USER
		);

		userRepository.save(user);

		// when
		Boolean reulst = userRepository.existsUserByEmail(email);

		// then
		Assertions.assertTrue(reulst);
	}
}