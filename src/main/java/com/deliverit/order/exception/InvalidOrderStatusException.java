package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class InvalidOrderStatusException extends OrderException {
    public InvalidOrderStatusException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}