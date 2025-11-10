package com.deliverit.order.presentation.dto.response;

import com.deliverit.order.domain.entity.Order;
import com.deliverit.order.domain.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CancelOrderInfo {
    private final String orderId;
    private final String previousStatus;
    private final String currentStatus;
    private final String cancelAt;

    @Builder
    private CancelOrderInfo(String orderId, String previousStatus, String currentStatus, String cancelAt) {
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.currentStatus = currentStatus;
        this.cancelAt = cancelAt;
    }

    public static CancelOrderInfo of(String orderid, String previousStatus, String currentStatus, String cancelAt) {
        return CancelOrderInfo.builder()
                .orderId(orderid)
                .previousStatus(previousStatus)
                .currentStatus(currentStatus)
                .cancelAt(cancelAt)
                .build();
    }

    public static CancelOrderInfo create(Order nextOrder) {
        return CancelOrderInfo.builder()
                .orderId(nextOrder.getOrderId())
                .previousStatus(OrderStatus.ORDER_COMPLETED.toString())
                .currentStatus(nextOrder.getOrderStatus().toString())
                .cancelAt(nextOrder.getUpdatedAt().toString())
                .build();
    }

    public static CancelOrderInfo create(Order nextOrder, OrderStatus beforeStatus) {
        return CancelOrderInfo.builder()
                .orderId(nextOrder.getOrderId())
                .previousStatus(beforeStatus.toString())
                .currentStatus(nextOrder.getOrderStatus().toString())
                .cancelAt(nextOrder.getUpdatedAt().toString())
                .build();
    }
}