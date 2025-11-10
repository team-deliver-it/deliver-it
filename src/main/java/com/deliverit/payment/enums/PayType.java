package com.deliverit.payment.enums;

import com.deliverit.global.exception.PaymentException;
import com.deliverit.global.response.code.PaymentResponseCode;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PayType {
    CARD("카드");

    @Getter
    private final String type;

    private static final Map<String, PayType> payTypes = Stream.of(values())
            .collect(Collectors.toMap(PayType::getType, p -> p));

    PayType(String type) {
        this.type = type;
    }

    public static PayType of(String name) {
        if(!payTypes.containsKey(name)) throw new PaymentException(PaymentResponseCode.INVALID_PAY_TYPE);
        return payTypes.get(name);
    }
}
