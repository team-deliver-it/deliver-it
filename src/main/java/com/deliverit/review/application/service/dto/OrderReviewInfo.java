package com.deliverit.review.application.service.dto;

import com.deliverit.review.domain.entity.OrderReview;

import java.math.BigDecimal;
import java.util.List;

public record OrderReviewInfo(
        Long orderReviewId,
        Long userId,
        String userName,
        BigDecimal star,
        String description
) {
    public static List<OrderReviewInfo> fromList(List<OrderReview> reviews) {
        return reviews.stream().map(OrderReviewInfo::from).toList();
    }

    public static OrderReviewInfo from(OrderReview review) {
        var user = review.getUser();

        return new OrderReviewInfo(
                review.getOrderReviewId(),
                user.getId(),
                user.getName(),
                review.getStar(),
                review.getDescription()
        );
    }
}
