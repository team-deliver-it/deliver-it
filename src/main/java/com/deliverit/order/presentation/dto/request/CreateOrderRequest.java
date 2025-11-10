package com.deliverit.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import lombok.*;

import java.util.List;

@Getter
public class CreateOrderRequest {
    @NotBlank(message = "음식점의 UUID 값은 필수입니다.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "음식점의 UUID 형식이 올바르지 않습니다.")
    @JsonProperty("restaurantId")
    private final String restaurantId;

    @Valid
    @NotEmpty(message = "메뉴는 적어도 한 개 이상 주문해야합니다. ")
    private final List<OrderMenuRequest> menus;

    @NotBlank(message = "배송지는 필수 값입니다.")
    @JsonProperty("deliveryAddress")
    private final String deliveryAddress;

    @Builder
    public CreateOrderRequest(List<OrderMenuRequest> menus, String restaurantId, String deliveryAddress) {
        this.menus = menus;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
    }
}
