package com.deliverit.menu.domain.entity;

import com.deliverit.menu.presentation.dto.MenuCreateRequestDto;
import com.deliverit.menu.presentation.dto.MenuResponseDto;
import com.deliverit.menu.presentation.dto.MenuUpdateRequestDto;
import com.deliverit.restaurant.domain.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_menu")
public class Menu {

    @Id
    @Column(name = "menu_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private MenuStatus status;

    @Column(nullable = false)
    private Boolean isAiDescGenerated;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void applyUpdate(MenuUpdateRequestDto requestDto) {
        if (requestDto.getName() != null) this.name = requestDto.getName();
        if (requestDto.getPrice() != null) this.price = requestDto.getPrice();
        if (requestDto.getStatus() != null) this.status = requestDto.getStatus();
        if (requestDto.getDescription() != null) this.description = requestDto.getDescription();
    }

    public static Menu from(MenuCreateRequestDto dto) {
        return Menu.builder()
                .restaurant(dto.getRestaurant())
                .name(dto.getName())
                .price(dto.getPrice())
                .status(dto.getStatus())
                .isAiDescGenerated(dto.getIsAiDescGenerated() != null && dto.getIsAiDescGenerated())
                .description(dto.getIsAiDescGenerated() ? dto.getDescription() : null)
                .build();
    }

    public MenuResponseDto toResponseDto() {
        return MenuResponseDto.builder()
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .status(this.status)
                .build();
    }
}
