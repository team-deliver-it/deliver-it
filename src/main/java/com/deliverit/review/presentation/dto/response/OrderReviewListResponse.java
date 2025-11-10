package com.deliverit.review.presentation.dto.response;

import com.deliverit.review.application.service.dto.OrderReviewInfo;

import java.util.List;

public record OrderReviewListResponse(
        List<OrderReviewResponse> list
) {
    public static OrderReviewListResponse from(List<OrderReviewInfo> reviews) {
        List<OrderReviewResponse> list = reviews.stream().map(OrderReviewResponse::from).toList();
        return new OrderReviewListResponse(list);
    }
}
