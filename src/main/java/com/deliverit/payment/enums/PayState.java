package com.deliverit.payment.enums;

import lombok.Getter;

public enum PayState {
    COMPLETED("결제 완료"),
    CANCELED("결제 취소"),
    ;

    @Getter
    private final String description;

    PayState(String description) {
        this.description = description;
    }
}
