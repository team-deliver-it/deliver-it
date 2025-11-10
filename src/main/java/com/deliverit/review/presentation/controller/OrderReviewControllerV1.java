package com.deliverit.review.presentation.controller;

import com.deliverit.global.response.ApiResponse;
import com.deliverit.review.application.service.OrderReviewService;
import com.deliverit.review.presentation.dto.request.CreateOrderReviewRequest;
import com.deliverit.review.presentation.dto.request.UpdateReviewRequest;
import com.deliverit.review.presentation.dto.response.MutateReviewResponse;
import com.deliverit.review.presentation.dto.response.OrderReviewListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.deliverit.global.response.code.ReviewResponseCode.*;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderReviewControllerV1 {
    private final OrderReviewService orderReviewService;

    @GetMapping("/orders/{orderId}/reviews")
    public ApiResponse<OrderReviewListResponse> getOrderReviews(
            @PathVariable String orderId
    ) {
        log.info("=== 주문 리뷰 조회 orderId : {} ===", orderId);
        var orderReviews = orderReviewService.getOrderReviews(orderId);
        log.info("=== 주문 리뷰 조회 성공 ===");
        return ApiResponse.create(
                ORDER_REVIEW_QUERY_SUCCESS,
                ORDER_REVIEW_QUERY_SUCCESS.getMessage(),
                OrderReviewListResponse.from(orderReviews)
        );
    }

    @PostMapping("/orders/{orderId}/reviews")
    public ApiResponse<MutateReviewResponse> create(
            @PathVariable
            String orderId,
            @Valid
            @RequestBody
            CreateOrderReviewRequest request
    ) {
        log.info("=== 주문 리뷰 생성 orderId : {} ===", orderId);
        var command = request.toCommand(orderId);
        Long savedReviewId = orderReviewService.createReview(command);
        log.info("=== 주문 리뷰 생성 성공 ===");
        return ApiResponse.create(
                ORDER_REVIEW_CREATE_SUCCESS,
                ORDER_REVIEW_CREATE_SUCCESS.getMessage(),
                new MutateReviewResponse(savedReviewId)
        );
    }

    @PutMapping("/order-reviews/{orderReviewId}")
    public ApiResponse<MutateReviewResponse> update(
            @PathVariable Long orderReviewId,
            @RequestBody @Valid UpdateReviewRequest request
    ) {
        log.info("=== 주문 리뷰 수정 order-reviewId : {} ===", orderReviewId);
        var command = request.toCommand(orderReviewId);
        Long id = orderReviewService.updateReview(command);
        log.info("=== 주문 리뷰 수정 성공 ===");
        return ApiResponse.create(
                ORDER_REVIEW_UPDATE_SUCCESS,
                ORDER_REVIEW_UPDATE_SUCCESS.getMessage(),
                new MutateReviewResponse(id)
        );
    }

    @DeleteMapping("/order-reviews/{orderReviewId}")
    public ApiResponse<MutateReviewResponse> delete(
            @PathVariable Long orderReviewId
    ) {
        log.info("=== 주문 리뷰 삭제 order-reviewId : {} ===", orderReviewId);
        Long id = orderReviewService.deleteReview(orderReviewId);
        log.info("=== 주문 리뷰 삭제 성공 ===");
        return ApiResponse.create(
                ORDER_REVIEW_DELETE_SUCCESS,
                ORDER_REVIEW_DELETE_SUCCESS.getMessage(),
                new MutateReviewResponse(id)
        );
    }
}
