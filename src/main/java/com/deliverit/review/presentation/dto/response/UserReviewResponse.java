package com.deliverit.review.presentation.dto.response;

import com.deliverit.review.application.service.dto.OrderReviewInfo;

import java.math.BigDecimal;

public record UserReviewResponse(
        Long orderReviewId,
        BigDecimal star,
        String description
) {
    public static UserReviewResponse from(OrderReviewInfo review) {
        return new UserReviewResponse(
                review.orderReviewId(),
                review.star(),
                review.description()
        );
    }
}
