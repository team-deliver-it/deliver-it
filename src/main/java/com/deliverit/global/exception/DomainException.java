package com.deliverit.global.exception;

import com.deliverit.global.response.code.ResponseCode;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final ResponseCode responseCode;

    public DomainException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}