package com.deliverit.restaurant.infrastructure.repository;

import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {
    Page<RestaurantListResponseDto> searchByCreatedAt(
            double latitude, double longitude, String keyword, RestaurantCategory category, Pageable pageable
    );

    Page<RestaurantListResponseDto> searchOrderByDistance(
            double latitude, double longitude, String keyword, RestaurantCategory category, Pageable pageable
    );

    Page<RestaurantListResponseDto> searchByRating(
            double latitude, double longitude, String keyword, RestaurantCategory category, Pageable pageable
    );
}