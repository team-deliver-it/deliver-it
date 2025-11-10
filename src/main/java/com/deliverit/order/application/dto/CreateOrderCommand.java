package com.deliverit.order.application.dto;

import com.deliverit.order.presentation.dto.request.CreateOrderRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateOrderCommand {

    private final String restaurantId;

    private final List<CreateMenuCommand> menus;

    private final String deliveryAddress;

    @Builder
    private CreateOrderCommand(String restaurantId, List<CreateMenuCommand> menus, String deliveryAddress) {
        this.restaurantId = restaurantId;
        this.menus = menus;
        this.deliveryAddress = deliveryAddress;
    }

    public static CreateOrderCommand of(CreateOrderRequest request) {
        return CreateOrderCommand.builder()
                .restaurantId(request.getRestaurantId())
                .menus(
                        request.getMenus().stream()
                                .map(CreateMenuCommand::of)
                                .toList()
                )
                .deliveryAddress(request.getDeliveryAddress())
                .build();
    }
}
