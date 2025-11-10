package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

public enum ServerResponseCode implements ResponseCode {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다. ");

    private final HttpStatus httpStatus;
    private final String message;

    ServerResponseCode(HttpStatus httpStatus, String message) {
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
