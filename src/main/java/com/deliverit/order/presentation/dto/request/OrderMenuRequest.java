package com.deliverit.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderMenuRequest {

    @NotBlank(message = "메뉴의 UUID 값은 필수입니다.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "메뉴의 UUID 형식이 올바르지 않습니다.")
    @JsonProperty("menuId")
    private final String menuId;

    @Positive(message = "메뉴 수량은 양의 정수여야 합니다.")
    private final int quantity;

    @Builder
    protected OrderMenuRequest(String menuId, int quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }
}
