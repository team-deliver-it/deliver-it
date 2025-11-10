package com.deliverit.restaurant.presentation.dto;

import com.deliverit.restaurant.domain.entity.Restaurant;
import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RestaurantInfoResponseDto {

    private String restaurantId;
    private String name;
    private String phone;
    private String address;
    private String description;
    private RestaurantStatus status;
    private List<RestaurantCategory> categories;
    private BigDecimal starAvg;
    private Long reviewCount;

    // Entity -> DTO 변환 팩토리 메서드
    public static RestaurantInfoResponseDto from(Restaurant restaurant) {
        return RestaurantInfoResponseDto.builder()
                .restaurantId(restaurant.getRestaurantId())
                .name(restaurant.getName())
                .phone(restaurant.getPhone())
                .address(restaurant.getAddress())
                .description(restaurant.getDescription())
                .status(restaurant.getStatus())
                .categories(restaurant.getCategories().stream().toList())
                .starAvg(restaurant.getRating().getStarAvg())
                .reviewCount(restaurant.getRating().getReviewsCount())
                .build();
    }
}
