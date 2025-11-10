package com.deliverit.menu.presentation.controller;

import com.deliverit.menu.application.service.MenuService;
import com.deliverit.menu.presentation.dto.MenuCreateRequestDto;
import com.deliverit.menu.presentation.dto.MenuResponseDto;
import com.deliverit.menu.presentation.dto.MenuUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/v1/restaurants/{restaurantId}/menu")
    public ResponseEntity<List<MenuResponseDto>> getMenuByRestaurantId(
            @PathVariable String restaurantId) {
        List<MenuResponseDto> menuResponseDtoList = menuService.getMenuByRestaurantId(restaurantId);

        return ResponseEntity.ok(menuResponseDtoList);
    }

    @PostMapping("/v1/restaurants/{restaurantId}/menu")
    public ResponseEntity<Void> createMenuItem(
            @PathVariable String restaurantId,
            @RequestBody List<@Valid MenuCreateRequestDto> requestDtoList) {
        menuService.createMenuItem(restaurantId, requestDtoList);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/v1/restaurants/{restaurantId}/menu")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable String restaurantId,
            @RequestBody List<String> menuIdList) {
        menuService.deleteMenuItem(restaurantId, menuIdList);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/v1/restaurants/{restaurantId}/menu")
    public ResponseEntity<Void> updateMenuItem(
            @PathVariable String restaurantId,
            @RequestBody List<@Valid MenuUpdateRequestDto> menuList) {
        menuService.updateMenuItem(restaurantId, menuList);

        return ResponseEntity.noContent().build();
    }
}
