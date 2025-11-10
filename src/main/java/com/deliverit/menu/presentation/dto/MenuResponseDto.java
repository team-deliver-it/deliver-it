package com.deliverit.menu.presentation.dto;

import com.deliverit.menu.domain.entity.Menu;
import com.deliverit.menu.domain.entity.MenuStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MenuResponseDto {
    private String name;
    private String description;
    private BigDecimal price;
    private MenuStatus status;

    public static MenuResponseDto from(Menu menu) {
        return MenuResponseDto.builder()
                .name(menu.getName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .status(menu.getStatus())
                .build();
    }
}
