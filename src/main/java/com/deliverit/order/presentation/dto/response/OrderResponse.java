package com.deliverit.order.presentation.dto.response;

import com.deliverit.order.domain.entity.Order;
import com.deliverit.order.domain.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private final String orderId;
    private final OrderStatus orderStatus;
    private final BigDecimal totalPrice;
    private final LocalDateTime orderedAt;
    private final Long version;

    @Builder
    private OrderResponse(String orderId, OrderStatus orderStatus, BigDecimal totalPrice, LocalDateTime orderedAt, Long version) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.orderedAt = orderedAt;
        this.version = version;
    }

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .totalPrice(order.getTotalPrice())
                .orderedAt(order.getUpdatedAt()) // 주문 성공 실패 시간 반환
                .version(order.getVersion())
                .build();
    }
}
