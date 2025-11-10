package com.deliverit.menu.presentation.dto;

import com.deliverit.menu.domain.entity.MenuStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MenuUpdateRequestDto {
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private MenuStatus status;

    private String description;
}
