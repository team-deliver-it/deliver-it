package com.deliverit.review.application.service.dto;

import java.math.BigDecimal;

public record OrderReviewCommand() {
    public record Create(
            String orderId,
            Long userId,
            BigDecimal star,
            String description
    ) {}

    public record Update(
            Long reviewId,
            BigDecimal star,
            String description
    ) {}
}
