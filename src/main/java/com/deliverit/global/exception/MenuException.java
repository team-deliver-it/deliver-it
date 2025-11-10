package com.deliverit.global.exception;

import com.deliverit.global.response.code.ResponseCode;

public class MenuException extends DomainException{
    public MenuException(ResponseCode responseCode) {
        super(responseCode);
    }
}
