package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

public enum ClientResponseCode implements ResponseCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
    INVALID_PATH(HttpStatus.BAD_REQUEST, "잘못된 경로 요청입니다."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "잘못된 타입입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "유효성 검증에 실패했습니다."),
    UNREADABLE_MESSAGE(HttpStatus.BAD_REQUEST, "요청 데이터를 읽을 수 없습니다."),
    BINDING_FAILED(HttpStatus.BAD_REQUEST, "데이터 바인딩에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ClientResponseCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
