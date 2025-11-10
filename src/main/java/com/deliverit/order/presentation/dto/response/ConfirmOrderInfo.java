package com.deliverit.order.presentation.dto.response;

import com.deliverit.order.domain.entity.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConfirmOrderInfo {
    private final String orderId;
    private final String orderStatus;
    private final String confirmedAt;

    @Builder
    private ConfirmOrderInfo(String orderId, String orderStatus, String confirmedAt) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.confirmedAt = confirmedAt;
    }

    public static ConfirmOrderInfo of(String orderId, String orderStatus, String confirmedAt) {
        return ConfirmOrderInfo.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .confirmedAt(confirmedAt)
                .build();
    }

    public static ConfirmOrderInfo create(Order order) {
        return ConfirmOrderInfo.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus().toString())
                .confirmedAt(order.getUpdatedAt().toString())
                .build();
    }
}
