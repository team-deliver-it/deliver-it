package com.deliverit.review.presentation.controller;

import com.deliverit.global.response.ApiResponse;
import com.deliverit.review.application.service.UserReviewService;
import com.deliverit.review.application.service.dto.OrderReviewInfo;
import com.deliverit.review.presentation.dto.response.UserReviewListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.deliverit.global.response.code.ReviewResponseCode.*;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserReviewControllerV1 {
    private final UserReviewService userReviewService;

    @GetMapping("/{userId}/reviews")
    public ApiResponse<UserReviewListResponse> getUserReviews(
            @PathVariable Long userId
    ) {
        log.info("=== 유저의 주문 리뷰 조회 userId : {} ===", userId);
        List<OrderReviewInfo> userReviews = userReviewService.getUserReviews(userId);
        log.info("=== 유저의 주문 리뷰 조회 성공 ===");
        return ApiResponse.create(
                USER_REVIEW_QUERY_SUCCESS,
                USER_REVIEW_QUERY_SUCCESS.getMessage(),
                UserReviewListResponse.from(userReviews)
        );
    }
}
