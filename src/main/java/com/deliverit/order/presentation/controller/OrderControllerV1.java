package com.deliverit.order.presentation.controller;

import com.deliverit.global.response.ApiResponse;
import com.deliverit.global.response.code.OrderResponseCode;
import com.deliverit.order.application.service.OrderPaymentService;
import com.deliverit.order.application.service.OrderService;
import com.deliverit.order.application.dto.CreateOrderCommand;
import com.deliverit.order.presentation.dto.request.OrderPaymentRequest;
import com.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.deliverit.order.presentation.dto.response.OrderInfo;
import com.deliverit.order.presentation.dto.response.OrderPaymentResponse;
import com.sparta.deliverit.order.presentation.dto.response.*;
import com.deliverit.order.presentation.dto.request.CreateOrderRequest;

import com.deliverit.payment.application.service.dto.PaymentRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@RestController
public class OrderControllerV1 implements OrderController {

    private final OrderService orderService;
    private final OrderPaymentService orderPaymentService;

    @Autowired
    public OrderControllerV1(OrderService orderService, OrderPaymentService orderPaymentService) {
        this.orderService = orderService;
        this.orderPaymentService = orderPaymentService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/v1/orders")
    public ApiResponse<Page<OrderInfo>> getOrderListForUser(LocalDateTime from, LocalDateTime to, Integer pageNumber, Integer pageSize) {
        // 임시 로그인
        String userId = "1";

        Page<OrderInfo> orderInfoList = orderService.getOrderListForUser(
                userId,
                from,
                to,
                pageNumber,
                pageSize
        );
        return ApiResponse.create(OrderResponseCode.ORDER_LIST_SUCCESS,"고객 본인의 주문 목록을 조회했습니다.", orderInfoList);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/v1/orders/{orderId}")
    public ApiResponse<OrderInfo> getOrderForUser(String orderId) {
        // 임시 로그인
        String userId = "1";

        OrderInfo orderInfo = orderService.getOrderDetailForUser(orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_DETAIL_SUCCESS,"고객의 주문을 조회했습니다.", orderInfo);
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/v1/restaurants/{restaurantId}/orders")
    public ApiResponse<Page<OrderInfo>> getOrderListForOwner(String restaurantId, LocalDateTime from, LocalDateTime to, Integer pageNumber, Integer pageSize) {
        // 임시 로그인
        String userId = "1";

        Page<OrderInfo> orderInfoList = orderService.getOrderListForOwner(
                userId,
                restaurantId,
                from,
                to,
                pageNumber,
                pageSize
        );

        return ApiResponse.create(OrderResponseCode.ORDER_LIST_SUCCESS,"음식점에서 주문 목록을 조회했습니다.", orderInfoList);
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/v1/restaurants/{restaurantId}/orders/{orderId}")
    public ApiResponse<OrderInfo> getOrderForOwner(String restaurantId, String orderId) {
        // 임시 로그인
        String userId = "1";

        OrderInfo orderInfo = orderService.getOrderDetailForOwner(orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_DETAIL_SUCCESS,"음식점이 주문을 조회했습니다.", orderInfo);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/v1/orders")
    public ApiResponse<CreateOrderInfo> createOrder(CreateOrderRequest request) {

        // 임시 로그인
        String userId = "1";

        CreateOrderInfo orderInfo = orderService.createOrder(CreateOrderCommand.of(request), Long.valueOf(userId));
        return ApiResponse.create(OrderResponseCode.ORDER_CREATE_SUCCESS, "주문을 정상적으로 생성했습니다.", orderInfo);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/v1/restaurants/{restaurantId}/orders/{orderId}/confirm")
    public ApiResponse<ConfirmOrderInfo> confirmOrder(String restaurantId, String orderId) {
        // 임시 로그인
        String userId = "2";

        ConfirmOrderInfo orderInfo = orderService.confirmOrder(restaurantId, orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_CONFIRM_SUCCESS,"주문 확인이 완료되었습니다.", orderInfo);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/v1/orders/{orderId}")
    public ApiResponse<OrderPaymentResponse> cancelOrderForUser(String orderId) {
        // 임시 로그인
        String userId = "2";

        OrderPaymentResponse orderPaymentResponse = orderPaymentService.cancelOrderPaymentForUser(orderId, userId);

        if (orderPaymentResponse.getMessage().equals("결제 취소 및 주문 취소가 완료되었습니다.")) {
            return ApiResponse.create(OrderResponseCode.ORDER_CANCEL_SUCCESS, orderPaymentResponse.getMessage(), orderPaymentResponse);
        } else {
            return ApiResponse.create(OrderResponseCode.ORDER_CANCEL_FAIL, orderPaymentResponse.getMessage(), orderPaymentResponse);
        }


    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/v1/restaurants/{restaurantId}/orders/{orderId}")
    public ApiResponse<OrderPaymentResponse> cancelOrderForOwner(String restaurantId, String orderId) {
        // 임시 로그인
        String userId = "2";

        OrderPaymentResponse orderPaymentResponse = orderPaymentService.cancelOrderPaymentForOwner(restaurantId, orderId, userId);

        if (orderPaymentResponse.getMessage().equals("결제 취소 및 주문 취소가 완료되었습니다.")) {
            return ApiResponse.create(OrderResponseCode.ORDER_CANCEL_SUCCESS, orderPaymentResponse.getMessage(), orderPaymentResponse);
        } else {
            return ApiResponse.create(OrderResponseCode.ORDER_CANCEL_FAIL, orderPaymentResponse.getMessage(), orderPaymentResponse);
        }
    }

    @PostMapping("/orders/pay")
    public ApiResponse<OrderPaymentResponse> orderPayment(OrderPaymentRequest request) {
        // 임시 로그인
        String userId = "2";

        CreateOrderRequest orderRequest = request.getCreateOrderRequest();
        PaymentRequestDto paymentRequest = request.getPaymentRequestRequest();

        OrderPaymentResponse response = orderPaymentService.checkout(CreateOrderCommand.of(orderRequest), paymentRequest, Long.valueOf(userId));
        if (response.equals("결제에 실패했습니다.")) {
            return ApiResponse.create(OrderResponseCode.ORDER_FAILED,response.getMessage(), response);
        } else if(response.equals("결제가 완료되었습니다.")) {
            return ApiResponse.create(OrderResponseCode.ORDER_SUCCESS,response.getMessage(), response);
        }

        return ApiResponse.create(OrderResponseCode.ORDER_SUCCESS,response.getMessage(), response);
    }
}