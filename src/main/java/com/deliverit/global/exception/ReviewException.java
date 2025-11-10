package com.deliverit.global.exception;

import com.deliverit.global.response.code.ReviewResponseCode;

public class ReviewException extends DomainException {
    public ReviewException(ReviewResponseCode responseCode) {
        super(responseCode);
    }
}
