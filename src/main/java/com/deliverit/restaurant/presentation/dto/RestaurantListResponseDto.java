package com.deliverit.restaurant.presentation.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RestaurantListResponseDto {
    private String restaurantId;
    private String name;
    private BigDecimal rating;
    private Long reviewCount;
    private Double distance;
}