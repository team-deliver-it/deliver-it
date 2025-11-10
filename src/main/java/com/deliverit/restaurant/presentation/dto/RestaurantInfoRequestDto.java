package com.deliverit.restaurant.presentation.dto;

import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RestaurantInfoRequestDto {

    @NotBlank(message = "음식점 이름이 비어있습니다.")
    private String name;

    @NotBlank(message = "음식점 전화번호가 비어있습니다.")
    private String phone;

    @NotBlank(message = "음식점 주소가 비어있습니다.")
    private String address;

    @NotBlank(message = "음식점 소개글이 비어있습니다.")
    private String description;

    @NotNull(message = "음식점 상태가 NULL 입니다.")
    private RestaurantStatus status;

    @NotEmpty(message = "음식점 카테고리가 비어있습니다.")
    private List<RestaurantCategory> categories;

    private String ownerId; // 가게를 관리자(MANAGER, MASTER)가 저장할 때 사용
}
