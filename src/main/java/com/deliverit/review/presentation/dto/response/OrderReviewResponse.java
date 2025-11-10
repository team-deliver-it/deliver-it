package com.deliverit.review.presentation.dto.response;

import com.deliverit.review.application.service.dto.OrderReviewInfo;

import java.math.BigDecimal;

public record OrderReviewResponse(
        Long orderReviewId,
        Long userId,
        String userName,
        BigDecimal star,
        String description
) {
    public static OrderReviewResponse from(OrderReviewInfo review) {
        return new OrderReviewResponse(
                review.orderReviewId(),
                review.userId(),
                review.userName(),
                review.star(),
                review.description()
        );
    }
}
