package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum RestaurantResponseCode implements ResponseCode {
    RESTAURANT_NOT_FOUND(NOT_FOUND, "일치하는 음식점을 찾을 수 없습니다."),
    RESTAURANT_FORBIDDEN(FORBIDDEN, "음식점 접근 권한이 없습니다."),
    RESTAURANT_CREATE_SUCCESS(OK, "음식점 등록에 성공했습니다."),
    RESTAURANT_SEARCH_SUCCESS(OK, "음식점 목록 조회에 성공했습니다."),
    RESTAURANT_DETAIL_SUCCESS(OK, "음식점 상세 조회에 성공했습니다."),
    RESTAURANT_UPDATE_SUCCESS(OK, "음식점 수정에 성공했습니다."),
    RESTAURANT_DELETE_SUCCESS(OK, "음식점 삭제에 성공했습니다."),
    INVALID_OWNER_ID(BAD_REQUEST, "소유자 아이디가 유효하지 않습니다."),
    INVALID_STATUS_TRANSITION(BAD_REQUEST, "허용되지 않는 상태 전이입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    RestaurantResponseCode(HttpStatus httpStatus, String message) {
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