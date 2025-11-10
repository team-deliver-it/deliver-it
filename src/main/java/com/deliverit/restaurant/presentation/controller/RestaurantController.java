package com.deliverit.restaurant.presentation.controller;

import com.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.deliverit.global.response.ApiResponse;
import com.deliverit.restaurant.application.service.RestaurantService;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import com.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.deliverit.restaurant.presentation.dto.RestaurantListRequestDto;
import com.deliverit.restaurant.presentation.dto.RestaurantDetailResponseDto;
import com.deliverit.restaurant.presentation.dto.RestaurantInfoResponseDto;
import com.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.deliverit.global.response.code.RestaurantResponseCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 음식점 등록
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ApiResponse<?> createRestaurant(
            @Valid @RequestBody RestaurantInfoRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        log.info("Controller - createRestaurant 실행: restaurantName={}", requestDto.getName());

        ResponseEntity<RestaurantInfoResponseDto> restaurant = ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(requestDto, user));

        return ApiResponse.create(RESTAURANT_CREATE_SUCCESS, RESTAURANT_CREATE_SUCCESS.getMessage(), restaurant);
    }

    // 음식점 전체 목록 조회
    @GetMapping
    public ApiResponse<Page<?>> getRestaurantList(
            @Valid @ModelAttribute RestaurantListRequestDto requestDto) {
        log.info("Controller - getAllRestaurants 실행: latitude={}, longitude={}, keyword={}, category={}",
                requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getKeyword(), requestDto.getCategory());

        Page<RestaurantListResponseDto> restaurantList = restaurantService.getRestaurantList(requestDto);

        log.info("Controller - getAllRestaurants 종료: restaurantList Total Page={}", restaurantList.getTotalPages());
        return ApiResponse.create(RESTAURANT_SEARCH_SUCCESS, RESTAURANT_SEARCH_SUCCESS.getMessage(), restaurantList);
    }

    // 음식점 단일 조회
    @GetMapping("/{restaurantId}")
    public ApiResponse<?> getRestaurantDetail(@PathVariable String restaurantId) throws Exception {
        log.info("Controller - getRestaurantInfo 실행: restaurantId={}", restaurantId);

        RestaurantDetailResponseDto restaurant = restaurantService.getRestaurantDetail(restaurantId);

        log.info("Controller - getRestaurantInfo 종료: restaurantId={}", restaurant.getRestaurantId());
        return ApiResponse.create(RESTAURANT_DETAIL_SUCCESS, RESTAURANT_DETAIL_SUCCESS.getMessage(), restaurant);
    }

    // 음식점 수정
    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ApiResponse<?> updateRestaurant(
            @PathVariable String restaurantId,
            @Valid @RequestBody RestaurantInfoRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) throws Exception {
        log.info("Controller - updateRestaurant 실행: restaurantId={}, restaurantName={}", restaurantId, requestDto.getName());

        RestaurantInfoResponseDto restaurant = restaurantService.updateRestaurant(restaurantId, requestDto, user);

        log.info("Controller - updateRestaurant 종료: restaurantId={}, restaurantName={}", restaurant.getRestaurantId(), restaurant.getName());
        return ApiResponse.create(RESTAURANT_UPDATE_SUCCESS, RESTAURANT_UPDATE_SUCCESS.getMessage(), restaurant);
    }

    // 음식점 상태 수정
    @PatchMapping("/{restaurantId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<RestaurantInfoResponseDto> updateRestaurantStatus(
            @PathVariable String restaurantId,
            @RequestParam RestaurantStatus status,
            @AuthenticationPrincipal UserDetailsImpl user
    ) throws Exception {
        log.info("Controller - updateRestaurantStatus 실행: restaurantId={}, status={}", restaurantId, status);

        RestaurantInfoResponseDto restaurant = restaurantService.updateRestaurantStatus(restaurantId, status, user);

        log.info("Controller - updateRestaurantStatus 종료: restaurantId={}, status={}", restaurant.getRestaurantId(), restaurant.getStatus());
        return ResponseEntity.ok(restaurant);
    }

    // 음식점 삭제
    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ApiResponse<String> deleteRestaurant(
            @PathVariable String restaurantId,
            @AuthenticationPrincipal UserDetailsImpl user
    ) throws Exception {
        log.info("Controller - deleteRestaurant 실행: restaurantId={}", restaurantId);

        restaurantService.deleteRestaurant(restaurantId, user);
        return ApiResponse.create(RESTAURANT_DELETE_SUCCESS, RESTAURANT_DELETE_SUCCESS.getMessage(), restaurantId);
    }
}
