package com.deliverit.restaurant.presentation.dto;

import com.deliverit.restaurant.domain.model.RestaurantCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.domain.Sort;

import static com.deliverit.restaurant.domain.model.SortType.CREATED_AT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RestaurantListRequestDto {

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    private String keyword;
    private RestaurantCategory category;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private String sort = CREATED_AT.field();

    @Builder.Default
    private Sort.Direction direction = Sort.Direction.DESC;

    public String getNormalizedKeyword() {
        if (keyword == null) return null;
        String t = keyword.trim();
        return t.isEmpty() ? null : t;
    }
}