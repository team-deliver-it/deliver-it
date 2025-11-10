package com.deliverit.global.exception;

import com.deliverit.global.response.code.ResponseCode;

public class PaymentException extends DomainException{
    public PaymentException(ResponseCode responseCode) {
        super(responseCode);
    }
}
