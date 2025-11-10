package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ReviewResponseCode implements ResponseCode {
    USER_REVIEW_QUERY_SUCCESS(OK, "유저의 주문 리뷰 조회에 성공하였습니다."),
    ORDER_REVIEW_QUERY_SUCCESS(OK, "주문 리뷰 조회에 성공하였습니다."),
    ORDER_REVIEW_CREATE_SUCCESS(OK, "주문 리뷰를 생성하였습니다."),
    ORDER_REVIEW_UPDATE_SUCCESS(OK, "주문 리뷰를 수정하였습니다."),
    ORDER_REVIEW_DELETE_SUCCESS(OK, "주문 리뷰를 삭제하였습니다."),
    NOT_FOUND_ORDER_REVIEW(BAD_REQUEST, "해당 주문 리뷰를 찾을 수 없습니다."),
    DUPLICATE_ORDER_REVIEW(BAD_REQUEST, "이미 해당 주문에 대한 리뷰가 존재합니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ReviewResponseCode(HttpStatus httpStatus, String message) {
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
