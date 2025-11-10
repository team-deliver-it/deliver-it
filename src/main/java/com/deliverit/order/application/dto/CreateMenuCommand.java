package com.deliverit.order.application.dto;

import com.deliverit.order.presentation.dto.request.OrderMenuRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateMenuCommand {
    private final String menuId;

    private final int quantity;

    @Builder
    protected CreateMenuCommand(String menuId, int quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public static CreateMenuCommand of(OrderMenuRequest request) {
        return CreateMenuCommand.builder()
                .menuId(request.getMenuId())
                .quantity(request.getQuantity())
                .build();
    }
}
