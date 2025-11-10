package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum OrderResponseCode implements ResponseCode {

    ORDER_LIST_SUCCESS(OK, "주문 목록 조회에 성공했습니다."),
    ORDER_DETAIL_SUCCESS(OK, "주문 조회에 성공했습니다."),
    ORDER_CANCEL_SUCCESS(OK, "주문이 취소되었습니다."),
    ORDER_CONFIRM_SUCCESS(OK, "주문이 확인되었습니다."),

    ORDER_CONFIRM_FAIL(CONFLICT, "주문 확인에 실패했습니다."),
    ORDER_CANCEL_FAIL(CONFLICT, "주문 취소에 실패했습니다."),

    INVALID_ORDER_STATUS(CONFLICT, "요청에 유효한 주문 상태가 아닙니다."),

    ORDER_SUCCESS(OK, "주문에 성공했습니다."),

    ORDER_CREATE_FAIL(CONFLICT, "주문 생성에 실패했습니다."),
    ORDER_CREATE_SUCCESS(CONFLICT, "주문 생성에 성공했습니다."),

    ORDER_FAILED(BAD_REQUEST, "주문에 실패했습니다."), // 세분화 필요
    DUPLICATE_ORDER(BAD_REQUEST, "중복 주문입니다."),
    OUT_OF_STOCK(CONFLICT, "재고 부족으로 주문에 실패했습니다."),

    NOT_FOUND_ORDER(NOT_FOUND, "주문을 찾을 수 없습니다."),
    NOT_FOUND_ORDER_ITEM(NOT_FOUND, "주문 아이템을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    OrderResponseCode(HttpStatus httpStatus, String message) {
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
