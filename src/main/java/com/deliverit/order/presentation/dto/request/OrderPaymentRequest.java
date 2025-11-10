package com.deliverit.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.deliverit.payment.application.service.dto.PaymentRequestDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderPaymentRequest {

    @JsonProperty("orderRequest")
    CreateOrderRequest createOrderRequest;

    @JsonProperty("paymentRequest")
    PaymentRequestDto paymentRequestRequest;

    @Builder
    protected OrderPaymentRequest(CreateOrderRequest createOrderRequest, PaymentRequestDto paymentRequestRequest) {
        this.createOrderRequest = createOrderRequest;
        this.paymentRequestRequest = paymentRequestRequest;
    }
}
