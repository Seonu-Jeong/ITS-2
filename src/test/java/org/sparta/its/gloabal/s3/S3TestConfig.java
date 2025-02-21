package org.sparta.its.gloabal.s3;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.amazonaws.services.s3.AmazonS3Client;

@TestConfiguration
public class S3TestConfig {

	@Bean
	public AmazonS3Client amazonS3Client() {
		AmazonS3Client mockS3Client = Mockito.mock(AmazonS3Client.class);

		// S3 Mock 동작 정의 (필요에 따라 추가)
		Mockito.when(mockS3Client.getResourceUrl(Mockito.anyString(), Mockito.anyString()))
			.thenAnswer(invocation -> "https://mock-s3-url/" + invocation.getArgument(1));

		return mockS3Client;
	}
}
