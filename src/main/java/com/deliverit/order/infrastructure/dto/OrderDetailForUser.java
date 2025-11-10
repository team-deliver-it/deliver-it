package com.deliverit.order.infrastructure.dto;

import com.deliverit.order.domain.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderDetailForUser {

    String getUserId();
    String getUserName();
    String getRestaurantId();
    String getRestaurantName();
    String getOrderId();
    LocalDateTime getOrderedAt();
    OrderStatus getOrderStatus();
    String getAddress();
    BigDecimal getTotalPrice();
    Long getVersion();
    String getPaymentId();
}
