package com.deliverit.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.deliverit.order.domain.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MenuInfo {
    @JsonProperty("menuName")
    private final String menuName;

    @JsonProperty("quantity")
    private final int quantity;

    @JsonProperty("price")
    private final BigDecimal price;

    @Builder
    private MenuInfo(String menuName, int quantity, BigDecimal price) {
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
    }

    public static MenuInfo create(String menuName, int quantity, BigDecimal price) {
        return MenuInfo.builder()
                .menuName(menuName)
                .quantity(quantity)
                .price(price)
                .build();
    }

    public static MenuInfo of(OrderItem orderItem) {
        return MenuInfo.builder()
                .menuName(orderItem.getMenuNameSnapshot())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getMenuPriceSnapshot())
                .build();
    }
}