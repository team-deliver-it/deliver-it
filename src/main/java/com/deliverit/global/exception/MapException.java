package com.deliverit.global.exception;

import com.deliverit.global.response.code.ResponseCode;

public class MapException extends DomainException {
    public MapException(ResponseCode responseCode) {
        super(responseCode);
    }
}

