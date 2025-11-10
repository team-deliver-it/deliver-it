package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

public enum AiResponseCode implements ResponseCode{
    INPUT_DATA_ERROR(HttpStatus.BAD_REQUEST,"요청한 데이터중 일부가 잘못되었습니다."),
    AI_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE,"AI 서버에 문제가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내 문제입니다. 지속적으로 발생시 문의 남겨주세요.");

    private final HttpStatus httpStatus;
    private final String message;

    AiResponseCode(HttpStatus httpStatus, String message) {
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
