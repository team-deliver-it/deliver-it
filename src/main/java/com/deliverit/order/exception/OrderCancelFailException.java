package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class OrderCancelFailException extends OrderException {
    public OrderCancelFailException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
