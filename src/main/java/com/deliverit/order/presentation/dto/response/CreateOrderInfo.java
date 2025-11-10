package com.deliverit.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateOrderInfo {
    @JsonProperty("orderId")
    private final String orderId;

    @JsonProperty("userId")
    private final Long userId;

    @JsonProperty("userMobilePhone")
    private final String phone;

    @JsonProperty("username")
    private final String name;

    @JsonProperty("amount")
    private final Long totalPrice;

    @Builder
    private CreateOrderInfo(String orderId, Long userId, String phone, String name, Long totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.phone = phone;
        this.name = name;
        this.totalPrice = totalPrice;
    }

    public static CreateOrderInfo create(String orderId, Long userId, String phone, String name, BigDecimal totalPrice) {
        return CreateOrderInfo.builder()
                .orderId(orderId)
                .userId(userId)
                .phone(phone)
                .name(name)
                .totalPrice(totalPrice.longValueExact())
                .build();
    }
}
