package com.deliverit.restaurant.infrastructure.repository;

import com.deliverit.restaurant.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String>, RestaurantRepositoryCustom {
    @EntityGraph(attributePaths = "categories")
    Optional<Restaurant> findByRestaurantId(String restaurantId);
}