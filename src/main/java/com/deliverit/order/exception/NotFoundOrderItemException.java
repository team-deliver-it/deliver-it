package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class NotFoundOrderItemException extends OrderException {
    public NotFoundOrderItemException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
