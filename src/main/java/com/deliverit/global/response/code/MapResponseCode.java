package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum MapResponseCode implements ResponseCode {
    INVALID_ADDRESS_INPUT(BAD_REQUEST, "유효하지 않은 주소입니다."), // 400
    ADDRESS_GEOCODING_FAILED(BAD_REQUEST, "주소를 좌표로 변환할 수 없습니다."), // 400
    GEOCODING_API_ERROR(BAD_GATEWAY, "API 호출 중 문제가 발생했습니다."), // 502
    GEOCODING_API_TIMEOUT(GATEWAY_TIMEOUT, "API를 호출할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    MapResponseCode(HttpStatus httpStatus, String message) {
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