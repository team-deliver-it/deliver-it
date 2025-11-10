package com.deliverit.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.deliverit.order.domain.entity.OrderStatus;
import com.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.deliverit.order.infrastructure.dto.OrderDetailForUser;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class OrderInfo {
    @JsonProperty("orderId")
    private final String orderId;

    @JsonProperty("restaurantName")
    private final String restaurantName;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("orderTime")
    private final String orderTime;

    @JsonProperty("orderStatus")
    private final String orderStatus;

    @JsonProperty("deliveryAddress")
    private final String deliveryAddress;

    @JsonProperty("menus")
    private final List<MenuInfo> menus;

    @JsonProperty("totalPrice")
    private final BigDecimal totalPrice;

    @Builder
    private OrderInfo(String orderId, String restaurantName, String username, String orderTime, OrderStatus orderStatus, String deliveryAddress, List<MenuInfo> menus, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.username = username;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus.getDescription();
        this.deliveryAddress = deliveryAddress;
        this.menus = menus;
        this.totalPrice = totalPrice;
    }

    public static OrderInfo of(OrderDetailForUser orderDetail, List<MenuInfo> menuInfoList) {
        return OrderInfo.builder()
                .orderId(orderDetail.getOrderId())
                .restaurantName(orderDetail.getRestaurantName())
                .username(orderDetail.getUserName())
                .orderTime(orderDetail.getOrderedAt().toString())
                .orderStatus(orderDetail.getOrderStatus())
                .deliveryAddress(orderDetail.getAddress())
                .menus(menuInfoList)
                .totalPrice(orderDetail.getTotalPrice())
                .build();
    }

    public static OrderInfo of(OrderDetailForOwner orderDetail, List<MenuInfo> menuInfoList) {
        return OrderInfo.builder()
                .orderId(orderDetail.getOrderId())
                .restaurantName(orderDetail.getRestaurantName())
                .username(orderDetail.getUserName())
                .orderTime(orderDetail.getOrderedAt().toString())
                .orderStatus(orderDetail.getOrderStatus())
                .deliveryAddress(orderDetail.getAddress())
                .menus(menuInfoList)
                .totalPrice(orderDetail.getTotalPrice())
                .build();
    }
}