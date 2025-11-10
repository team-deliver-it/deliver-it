package com.deliverit.global.exception;

import com.deliverit.global.response.code.ResponseCode;

public class RestaurantException extends DomainException {
    public RestaurantException(ResponseCode responseCode) {
        super(responseCode);
    }
}

