package com.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

public enum PaymentResponseCode implements ResponseCode {
    PAYMENT_SUCCESS(HttpStatus.CREATED, "결제에 성공했습니다."),
    PAYMENT_CANCEL_REQUEST(HttpStatus.NO_CONTENT, "결제 취소를 요청했습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 결제내역 입니다"),

    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제에 실패했습니다."),
    PAYMENT_CANCEL(HttpStatus.BAD_REQUEST, "결제 중 취소했습니다."),
    INVALID_PAY_TYPE(HttpStatus.BAD_REQUEST, "현재 지원되지 않는 결제수단입니다."),
    INVALID_COMPANY(HttpStatus.BAD_REQUEST, "현재 지원되지 않는 결제사입니다."),
    INVALID_CARD_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 카드번호입니다."),
    CARD_LIMIT_EXCEEDED(HttpStatus.PAYMENT_REQUIRED, "결제 한도 초과입니다."),
    INSUFFICIENT_FUNDS(HttpStatus.PAYMENT_REQUIRED, "잔액 부족입니다."),
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_GATEWAY, "PG사 결제 서버 오류입니다."),
    NETWORK_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "결제 시간 초과입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    PaymentResponseCode(HttpStatus httpStatus, String message) {
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
