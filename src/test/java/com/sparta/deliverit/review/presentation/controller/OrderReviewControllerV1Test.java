package com.sparta.deliverit.review.presentation.controller;

import com.deliverit.review.presentation.controller.OrderReviewControllerV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.deliverit.review.application.service.OrderReviewService;
import com.deliverit.review.application.service.dto.OrderReviewInfo;
import com.deliverit.review.presentation.dto.request.CreateOrderReviewRequest;
import com.deliverit.review.presentation.dto.request.UpdateReviewRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderReviewControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderReviewControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderReviewService orderReviewService;

    @Nested
    @DisplayName("주문 리뷰 조회")
    class GetOrderReview {
        @Test
        @DisplayName("주문 리뷰 조회 요청이 유효하면 200 상태코드로 주문 리스트를 반환한다")
        void whenRequestIsValid_thenSuccess() throws Exception {
            when(orderReviewService.getOrderReviews("orderId"))
                    .thenReturn(List.of(
                            new OrderReviewInfo(
                                    1L,
                                    1L,
                                    "userName",
                                    BigDecimal.valueOf(4.5),
                                    "리뷰 설명"
                            )
                    ));

            mockMvc.perform(get("/v1/orders/orderId/reviews")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list[0].orderReviewId").isNumber())
                    .andExpect(jsonPath("$.data.list[0].userId").isNumber())
                    .andExpect(jsonPath("$.data.list[0].userName").isString())
                    .andExpect(jsonPath("$.data.list[0].star").isNumber())
                    .andExpect(jsonPath("$.data.list[0].description").isString());
        }
    }

    @Nested
    @DisplayName("주문 리뷰 생성")
    class CreateOrderReview {
        @Test
        @DisplayName("올바른 리뷰 생성 요청을 보내면 200 상태코드로 성공한다")
        void whenRequestIsValid_thenSuccess() throws Exception {
            CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                    1L,
                    BigDecimal.valueOf(4.5),
                    "정말 맛있어요"

            );

            mockMvc.perform(post("/v1/orders/orderId/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.reviewId").exists());
        }

        @Test
        @DisplayName("userId 가 없다면 요청은 400 상태코드로 실패한다")
        void whenUserIdIsNull_thenFail() throws Exception {
            CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                    null,
                    BigDecimal.valueOf(4.5),
                    "정말 맛있어요"

            );

            mockMvc.perform(post("/v1/orders/orderId/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("star 가 없다면 요청은 400 상태코드로 실패한다")
        void whenStarIsNull_thenFail() throws Exception {
            CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                    1L,
                    null,
                    "정말 맛있어요"

            );

            mockMvc.perform(post("/v1/orders/orderId/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("description은 없어도 요청은 200 상태코드로 성공한다")
        void whenDescriptionIsNull_thenSuccess() throws Exception {
            CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                    1L,
                    BigDecimal.valueOf(4.5),
                    null

            );

            mockMvc.perform(post("/v1/orders/orderId/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.reviewId").exists());
        }

        @Nested
        @DisplayName("값 형식과 범위 검증")
        class ValueRoleTest {

            @Test
            @DisplayName("userId 가 0이면 요청은 400 상태코드로 실패한다")
            void whenUserIdIsZero_thenFail() throws Exception {
                CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                        0L,
                        BigDecimal.valueOf(4.5),
                        null
                );

                mockMvc.perform(post("/v1/orders/orderId/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("userId 가 음수면 요청은 400 상태코드로 실패한다")
            void whenUserIdIsNegative_thenFail() throws Exception {
                CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                        -1L,
                        BigDecimal.valueOf(4.5),
                        null
                );

                mockMvc.perform(post("/v1/orders/orderId/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("star 의 값이 1.0 이상이 아니라면 요청은 400 상태코드로 실패한다")
            void whenStarIsLessThenOne_thenFail() throws Exception {
                CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                        1L,
                        BigDecimal.valueOf(0.9),
                        null
                );

                mockMvc.perform(post("/v1/orders/orderId/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("star 의 값이 5.0 이하가 아니라면 요청은 400 상태코드로 실패한다")
            void whenStarIsGreaterThenFive_thenFail() throws Exception {
                CreateOrderReviewRequest request = new CreateOrderReviewRequest(
                        1L,
                        BigDecimal.valueOf(5.1),
                        null
                );

                mockMvc.perform(post("/v1/orders/orderId/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("리뷰 수정")
    class UpdateReview {
        @Test
        @DisplayName("올바른 리뷰 수정 요청을 보내면 200 상태코드로 성공한다")
        void whenRequestIsValid_thenSuccess() throws Exception {
            var request = new UpdateReviewRequest(
                    BigDecimal.valueOf(4.5),
                    "정말 맛있어요"

            );

            mockMvc.perform(put("/v1/order-reviews/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.reviewId").exists());
        }

        @Test
        @DisplayName("star 가 없다면 요청은 400 상태코드로 실패한다")
        void whenStarIsNull_thenFail() throws Exception {
            var request = new UpdateReviewRequest(
                    null,
                    "정말 맛있어요"

            );

            mockMvc.perform(put("/v1/order-reviews/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Nested
        @DisplayName("값 형식과 범위 검증")
        class ValueRoleTest {

            @Test
            @DisplayName("star 의 값이 1.0 이상이 아니라면 요청은 400 상태코드로 실패한다")
            void whenStarIsLessThenOne_thenFail() throws Exception {
                var request = new UpdateReviewRequest(
                        BigDecimal.valueOf(0.9),
                        null
                );

                mockMvc.perform(put("/v1/order-reviews/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("star 의 값이 5.0 이하가 아니라면 요청은 400 상태코드로 실패한다")
            void whenStarIsGreaterThenFive_thenFail() throws Exception {
                var request = new UpdateReviewRequest(
                        BigDecimal.valueOf(5.1),
                        null
                );

                mockMvc.perform(put("/v1/order-reviews/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("리뷰 삭제")
    class DeleteReview {
        @Test
        @DisplayName("올바른 리뷰 삭제 요청을 보내면 200 상태코드로 성공한다")
        void whenRequestIsValid_thenSuccess() throws Exception {
            mockMvc.perform(delete("/v1/order-reviews/{reviewId}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.reviewId").isNumber());
        }
    }
}
