package com.deliverit.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.deliverit.payment.application.service.dto.PaymentResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderPaymentResponse {

    @JsonProperty("requiredId")
    private final String requiredId;

    @JsonProperty("idempotencyKey")
    private final String idempotencyKey;

    @JsonProperty("message")
    private String message;

    @JsonProperty("order")
    private final OrderResponse orderResponse;

    @JsonProperty("payment")
    private final PaymentResponseDto paymentResponseDto;

    @Builder
    private OrderPaymentResponse(String requiredId, String idempotencyKey, String message, OrderResponse orderResponse, PaymentResponseDto paymentResponseDto) {
        this.requiredId = requiredId;
        this.idempotencyKey = idempotencyKey;
        this.message = message;
        this.orderResponse = orderResponse;
        this.paymentResponseDto = paymentResponseDto;
    }

    public static OrderPaymentResponse create(String requiredId, String idempotencyKey, String message, OrderResponse orderResponse, PaymentResponseDto paymentResponseDto) {
        return OrderPaymentResponse.builder()
                .requiredId(requiredId)
                .idempotencyKey(idempotencyKey)
                .message(message)
                .orderResponse(orderResponse)
                .paymentResponseDto(paymentResponseDto)
                .build();
    }
}
