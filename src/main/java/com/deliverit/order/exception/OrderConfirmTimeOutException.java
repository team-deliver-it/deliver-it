package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class OrderConfirmTimeOutException extends OrderException {
    public OrderConfirmTimeOutException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
