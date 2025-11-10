package com.deliverit.restaurant.domain.entity;

import com.deliverit.global.entity.BaseEntity;
import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import com.deliverit.restaurant.domain.vo.RestaurantRating;
import com.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.deliverit.review.domain.vo.Review;
import com.deliverit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

import java.util.EnumSet;
import java.util.Set;

import static com.deliverit.restaurant.domain.model.RestaurantStatus.SHUTDOWN;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "p_restaurant")
@FilterDef(name = "activeRestaurantFilter")
@Filter(
        name = "activeRestaurantFilter",
        condition = "deleted_at IS NULL and status <> 'SHUTDOWN'"
)
public class Restaurant extends BaseEntity {

    @Id
    private String restaurantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    public void updateCoordinates(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantStatus status;

    public void updateStatus(RestaurantStatus status) {
        this.status = status;
    }

    // 사용자 - 음식점 1:N 관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void assignUser(User user) {
        this.user = user;
    }

    // 음식점 - 카테고리 N:M 관계
    @ElementCollection(targetClass = RestaurantCategory.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "restaurant_category",
            joinColumns = @JoinColumn(name = "restaurant_id")
    )
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<RestaurantCategory> categories = java.util.EnumSet.noneOf(RestaurantCategory.class);

    public void assignCategories(Set<RestaurantCategory> categories) {
        this.categories.clear();
        if (categories != null) this.categories.addAll(categories);
    }

    @Embedded
    @Builder.Default
    private RestaurantRating rating = new RestaurantRating();

    // 음식점 수정 메서드
    public void update(RestaurantInfoRequestDto requestDto, EnumSet<RestaurantCategory> categories, Coordinates coordinates) {
        name = requestDto.getName();
        phone = requestDto.getPhone();
        address = requestDto.getAddress();
        longitude = coordinates.getLongitude();
        latitude = coordinates.getLatitude();
        description = requestDto.getDescription();
        status = requestDto.getStatus();
        this.categories.clear();
        this.categories.addAll(categories);
    }

    // 음식점 삭제 메서드 (soft delete)
    public void softDelete() {
        status = SHUTDOWN;
    }

    // DTO -> Entity 변환 팩토리 메서드
    public static Restaurant from(RestaurantInfoRequestDto requestDto, String restaurantId) {
        return Restaurant.builder()
                .restaurantId(restaurantId)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .address(requestDto.getAddress())
                .description(requestDto.getDescription())
                .status(requestDto.getStatus())
                .build();
    }

    public void addReview(Review review) {
        this.rating = this.rating.addReview(review);
    }

    public void updateReview(Review oldReview, Review newReview) {
        this.rating = this.rating.updateReview(oldReview, newReview);
    }

    public void removeReview(Review review) {
        this.rating = this.rating.removeReview(review);
    }
}
