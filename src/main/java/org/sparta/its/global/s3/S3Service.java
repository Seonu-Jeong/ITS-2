package org.sparta.its.global.s3;

import static org.sparta.its.global.exception.errorcode.ImageErrorCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sparta.its.global.exception.ImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;

/**
 * create on 2025. 01. 08.
 * create by IntelliJ IDEA.
 *
 * S3 관련 Service.
 *
 * @author TaeHyeon Kim
 */
@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3Client amazonS3;

	private static final String TOP_LEVEL_DOMAIN_WITH_DOT_AND_SLASH = ".com/";

	@Value("${spring.cloud.aws.s3.bucket}")
	private String BUCKET;

	/**
	 * 이미지 파일 전처리
	 *
	 * @param images 이미지파일
	 * @param imageFormat 이미지 관련 포맷팅
	 * @param domainId 이미지 저장을 원하는 도메인 고유 식별자
	 * @return {@link List<String>} S3에 저장된 이미지 URL 리스트
	 * @throws IOException S3 관련 IO 예외
	 */
	public List<String> uploadImages(
		MultipartFile[] images,
		ImageFormat imageFormat,
		Long domainId) throws IOException {

		// 이미지가 빈값인지 확인
		long count = Arrays.stream(images).filter(MultipartFile::isEmpty).count();
		if (count > 0) {
			throw new ImageException(BAD_IMAGE_FILE);
		}

		List<String> uploadedUrls = new ArrayList<>();
		for (MultipartFile image : images) {
			String fileUrl = saveFileToS3(image, imageFormat, domainId);
			uploadedUrls.add(fileUrl);
		}
		return uploadedUrls;
	}

	/**
	 * S3 이미지를 업데이트
	 *
	 * @param imageFormat 이미지 관련 포맷팅
	 * @param id 이미지 수정을 원하는 도메인 고유 식별자
	 * @param imageUrl 이미지 수정을 원하는 이미지 URL
	 * @param images 이미지파일
	 * @return {@link String} S3에 저장된 이미지 URL
	 * @throws IOException S3 관련 IO 예외
	 */
	public String updateImage(
		ImageFormat imageFormat,
		Long id,
		String imageUrl,
		MultipartFile[] images) throws IOException {

		// 이미지파일이 1개 일때만 업데이트 가능
		if (images.length != 1) {
			throw new ImageException(BAD_IMAGE_FILE);
		}

		// 기존 이미지 삭제
		String objectKey = getObjectKey(imageUrl);
		amazonS3.deleteObject(BUCKET, objectKey);

		// 이미지를 새로 업로드하고 url 을 받아옴
		return saveFileToS3(images[0], imageFormat, id);
	}

	/**
	 * S3 폴더 단위 이미지 삭제
	 *
	 * @param hallId 공연장 고유 식별자
	 * @param imageFormat S3 관련 이미지 포멧팅
	 */
	public void deleteImprovementDelete(Long hallId, ImageFormat imageFormat) {
		ListObjectsV2Result result;
		List<String> keysToDelete = new ArrayList<>();
		String folderName = imageFormat.getPath().substring(1) + "/" + hallId;

		ListObjectsV2Request listRequest = new ListObjectsV2Request()
			.withBucketName(BUCKET)
			.withPrefix(folderName);

		do {
			/**
			 * S3에서 path에 존재하는 객체 목록 가져오기
			 */
			result = amazonS3.listObjectsV2(listRequest);

			/**
			 * 객체(파일) Key 수집
			 * 최대 1000개씩 잘라서 DeleteObjectsRequest를 보내야 함
			 */
			for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
				keysToDelete.add(objectSummary.getKey());

				/**
				 * keysToDelete 사이즈가 1000개에 도달하면, 즉시 deleteObjects() 호출[삭제]
				 * getObjectSummaries는 1000개씩 들고 오는 것으로 알고 있으나 테스트 해보아야함
				 */
				if (keysToDelete.size() == 1000) {
					deleteObjectsBatch(keysToDelete);
					keysToDelete.clear();
				}
			}

			/**
			 * S3 객체 목록을 페이징 방식으로 계속해서 불러오기 위한 토큰셋팅
			 */
			listRequest.setContinuationToken(result.getNextContinuationToken());
		} while (result.isTruncated());

		/**
		 * 반복문 끝났는데, 1000개 미만으로 남은 key가 있다면 마지막으로 한번 더 요청
		 */
		if (!keysToDelete.isEmpty()) {
			deleteObjectsBatch(keysToDelete);
		}
	}

	/**
	 *  S3 삭제를 위한 함수
	 *
	 * @param keysToDelete 삭제할 이미지의 키 리스트
	 */
	private void deleteObjectsBatch(List<String> keysToDelete) {
		DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(BUCKET)
			.withKeys(keysToDelete.toArray(new String[0]));
		amazonS3.deleteObjects(deleteRequest);
	}

	/**
	 * url 을 통해 aws-s3에 오브젝트 경로를 찾는 함수
	 *
	 * @param imageUrl 이미지 url
	 * @return {@link String} S3 에 필요한 오브젝트 키
	 */
	private String getObjectKey(String imageUrl) {
		// ".com/" 이후의 위치를 찾음
		int objectKeyStartIndex = imageUrl.indexOf(TOP_LEVEL_DOMAIN_WITH_DOT_AND_SLASH) + 5; // ".com/" 다음 위치
		String objectKeyEncoded = imageUrl.substring(objectKeyStartIndex);

		// 객체 키 디코딩
		return URLDecoder.decode(objectKeyEncoded, StandardCharsets.UTF_8);
	}

	/**
	 * 실질적으로 s3에 저장하는 함수
	 *
	 * @param image 이미지파일
	 * @param imageFormat 이미지 관련 포맷팅
	 * @param id 이미지 수정을 원하는 도메인 고유 식별자
	 * @return {@link String} S3에 저장된 이미지 URL
	 * @throws IOException
	 */
	private String saveFileToS3(MultipartFile image, ImageFormat imageFormat, Long id) throws IOException {

		// 파일 확장자 가져오기
		String fileExtension = getFileExtensionWithDot(image.getOriginalFilename());

		// 확장자 유효성 검사
		validateFileExtension(fileExtension, imageFormat);

		// 기본 버킷 설정
		StringBuilder packageName = new StringBuilder(BUCKET);

		// imageFormat 에 정의된 경로에 따라 설정
		StringBuilder secondPackageName = new StringBuilder(imageFormat.getPath());

		// 해당 도메인 id에 따른 패키지 추가
		secondPackageName.append("/").append(id);

		// 이미지 이름과 확장자를 가져옴
		String s3FileName = image.getOriginalFilename();

		// 최종 패키지
		packageName.append(secondPackageName);

		// s3 저장
		ObjectMetadata objMeta = new ObjectMetadata();

		try (InputStream inputStream = image.getInputStream()) {
			objMeta.setContentLength(inputStream.available());
			amazonS3.putObject(packageName.toString(), s3FileName, inputStream, objMeta);
		} catch (IOException e) {
			// 예외 처리 로직
			throw new ImageException(FILE_UPLOAD_FAILED);
		}

		// substring 은 '/' 해주기 위함
		return amazonS3.getResourceUrl(BUCKET, secondPackageName.substring(1) + "/" + s3FileName);
	}

	/**
	 * 확장자가 있는지 확인하기 위함
	 *
	 * @param originalFileName 이미지 파일의 이름과 파일 확장자
	 * @return 파일 확장자
	 */
	private String getFileExtensionWithDot(String originalFileName) {
		int lastIndexOfDot = originalFileName.lastIndexOf('.');

		if (lastIndexOfDot == -1) {
			throw new ImageException(NO_EXTENSION_FILE);
		}

		return originalFileName.substring(lastIndexOfDot);
	}

	/**
	 * 허용된 확장자인지 확인하기 위함
	 *
	 * @param fileExtension 파일 확장자
	 * @param imageFormat 이미지 관련 포맷팅
	 */
	private void validateFileExtension(String fileExtension, ImageFormat imageFormat) {
		if (!Arrays.asList(imageFormat.getWhiteList()).contains(fileExtension)) {
			throw new ImageException(NOT_ALLOW_FILE_EXTENSION);
		}
	}

}
