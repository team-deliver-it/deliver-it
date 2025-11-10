package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class OrderConfirmFailException extends OrderException {
    public OrderConfirmFailException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
