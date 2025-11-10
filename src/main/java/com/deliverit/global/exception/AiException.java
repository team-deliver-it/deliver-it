package com.deliverit.global.exception;

import com.deliverit.global.response.code.AiResponseCode;
import lombok.Getter;

@Getter
public class AiException extends DomainException {
    public AiException(AiResponseCode responseCode) {
        super(responseCode);
    }
}
