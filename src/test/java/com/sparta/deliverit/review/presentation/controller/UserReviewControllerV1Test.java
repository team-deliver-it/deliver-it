package com.sparta.deliverit.review.presentation.controller;

import com.deliverit.review.application.service.UserReviewService;
import com.deliverit.review.application.service.dto.OrderReviewInfo;
import com.deliverit.review.presentation.controller.UserReviewControllerV1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserReviewControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class UserReviewControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserReviewService userReviewService;

    @Nested
    @DisplayName("유저 리뷰 조회")
    class GetUserReview {
        @Test
        @DisplayName("유저 리뷰 조회 요청이 유효하면 200 상태코드로 주문 리스트를 반환한다")
        void whenRequestIsValid_thenSuccess() throws Exception {
            when(userReviewService.getUserReviews(1L))
                    .thenReturn(List.of(
                            new OrderReviewInfo(
                                    1L,
                                    1L,
                                    "userName",
                                    BigDecimal.valueOf(4.5),
                                    "리뷰 설명"
                            )
                    ));

            mockMvc.perform(get("/v1/users/1/reviews")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list[0].star").isNumber())
                    .andExpect(jsonPath("$.data.list[0].description").isString());
        }
    }
}
