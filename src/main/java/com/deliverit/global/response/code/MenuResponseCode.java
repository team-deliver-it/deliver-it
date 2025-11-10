package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

public enum MenuResponseCode implements ResponseCode {

    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "찾으시는 메뉴가 없습니다."),
    MENU_NOT_IN_RESTAURANT(HttpStatus.NOT_FOUND, "해당 메뉴는 식당에 없는 메뉴입니다."),
    REQUEST_EMPTY_LIST(HttpStatus.BAD_REQUEST, "빈 리스트로 잘못된 요청입니다."),
    MENU_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 메뉴 등록 요청입니다.");

    private final HttpStatus httpStatus;

    private final String message;

    MenuResponseCode(HttpStatus httpStatus, String message) {
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
