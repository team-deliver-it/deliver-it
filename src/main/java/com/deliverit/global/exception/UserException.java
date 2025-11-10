package com.deliverit.global.exception;

import com.deliverit.global.response.code.UserResponseCode;

public class UserException extends DomainException {
    public UserException(UserResponseCode responseCode) {
        super(responseCode);
    }
}
