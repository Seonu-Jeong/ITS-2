package org.sparta.its.global.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageErrorCode {

	// 400 BAD REQUEST
	BAD_IMAGE_FILE(HttpStatus.BAD_REQUEST, "파일이 올바르지 않습니다"),

	NO_EXTENSION_FILE(HttpStatus.BAD_REQUEST, "파일 확장자가 없는 파일입니다."),

	NOT_ALLOW_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "해당 확장자는 업로드가 불가능합니다."),

	// 500 INTERNAL SERVER ERROR
	FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패"),

	FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 실패");

	private final HttpStatus httpStatus;
	private final String detail;
}
