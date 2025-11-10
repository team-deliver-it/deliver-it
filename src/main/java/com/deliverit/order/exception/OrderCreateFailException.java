package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class OrderCreateFailException extends OrderException {
    public OrderCreateFailException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
