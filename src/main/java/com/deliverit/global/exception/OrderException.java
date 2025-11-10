package com.deliverit.global.exception;

import com.deliverit.global.response.code.OrderResponseCode;

public class OrderException extends DomainException {
    public OrderException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
