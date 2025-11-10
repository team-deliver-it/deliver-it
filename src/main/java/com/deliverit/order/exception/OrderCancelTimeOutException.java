package com.deliverit.order.exception;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.response.code.OrderResponseCode;

public class OrderCancelTimeOutException extends OrderException {
    public OrderCancelTimeOutException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
