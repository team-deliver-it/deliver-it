package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class NotFoundOrderException extends OrderException {
    public NotFoundOrderException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
