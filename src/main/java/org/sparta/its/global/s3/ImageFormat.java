package org.sparta.its.global.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * create on 2025. 01. 08.
 * create by IntelliJ IDEA.
 *
 * S3 관련 Enum.
 *
 * @author TaeHyeon Kim
 */
@Getter
@AllArgsConstructor
public enum ImageFormat {
	CONCERT("/CONCERT", new String[] {".jpg", ".png"}),
	HALL("/HALL", new String[] {".jpg", ".png"});

	private final String path;
	private final String[] whiteList;
}
