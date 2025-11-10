package com.deliverit.order.presentation.controller;

import com.deliverit.global.response.ApiResponse;

import com.deliverit.order.presentation.dto.request.OrderPaymentRequest;
import com.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.deliverit.order.presentation.dto.response.OrderInfo;
import com.deliverit.order.presentation.dto.response.OrderPaymentResponse;
import com.sparta.deliverit.order.presentation.dto.response.*;
import com.deliverit.order.presentation.dto.request.CreateOrderRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@RestController
public interface OrderController {

    ApiResponse<Page<OrderInfo>> getOrderListForUser(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotNull(message = "조회 시작일(from)은 필수입니다.")
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotNull(message = "조회 종료일(to)은 필수입니다.")
            LocalDateTime to,

            @RequestParam
            @PositiveOrZero(message = "페이지 숫자는 0 이상의 정수여야 합니다.")
            Integer pageNumber,

            @RequestParam
            @Min(value = 1, message = "페이지 크기는 1 이상의 100 이하의 양수여야 합니다.")
            @Max(value = 100, message = "페이지 크기는 1 이상의 100 이하의 양수여야 합니다.")
            Integer pageSize
    );

    ApiResponse<OrderInfo> getOrderForUser(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable String orderId
    );

    ApiResponse<Page<OrderInfo>> getOrderListForOwner(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "음식점의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String restaurantId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotNull(message = "조회 시작일(from)은 필수입니다.")
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotNull(message = "조회 종료일(to)은 필수입니다.")
            LocalDateTime to,

            @RequestParam
            @PositiveOrZero(message = "페이지 숫자는 0 이상의 정수여야 합니다.")
            Integer pageNumber,

            @RequestParam
            @Min(value = 1, message = "페이지 크기는 1 이상의 100 이하의 양수여야 합니다.")
            @Max(value = 100, message = "페이지 크기는 1 이상의 100 이하의 양수여야 합니다.")
            Integer pageSize
    );

    ApiResponse<OrderInfo> getOrderForOwner(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "음식점의 UUID 형식이 올바르지 않습니다.")
            @PathVariable String restaurantId,
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable String orderId);

    ApiResponse<CreateOrderInfo> createOrder(@Valid @RequestBody CreateOrderRequest orderRequest);

    ApiResponse<ConfirmOrderInfo> confirmOrder(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "음식점의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String restaurantId,

            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String orderId);

    ApiResponse<OrderPaymentResponse> cancelOrderForUser(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String orderId);

    ApiResponse<OrderPaymentResponse> cancelOrderForOwner(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "음식점의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String restaurantId,

            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String orderId);

    ApiResponse<OrderPaymentResponse> orderPayment(@Valid @RequestBody OrderPaymentRequest request);
}