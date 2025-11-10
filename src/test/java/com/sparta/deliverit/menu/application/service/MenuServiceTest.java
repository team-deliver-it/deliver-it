package com.sparta.deliverit.menu.application.service;

import com.deliverit.global.exception.MenuException;
import com.deliverit.global.exception.RestaurantException;
import com.deliverit.menu.application.service.MenuService;
import com.deliverit.menu.domain.entity.Menu;
import com.deliverit.menu.domain.entity.MenuStatus;
import com.deliverit.menu.domain.repository.MenuRepository;
import com.deliverit.menu.presentation.dto.MenuCreateRequestDto;
import com.deliverit.menu.presentation.dto.MenuResponseDto;
import com.deliverit.menu.presentation.dto.MenuUpdateRequestDto;
import com.deliverit.restaurant.domain.entity.Restaurant;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import com.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.deliverit.global.response.code.RestaurantResponseCode.RESTAURANT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 조회 성공")
    void successGetMenuByRestaurantId() {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId("1")
                .name("한식당")
                .phone("010-1234-5678")
                .address("서울시 강남구 테헤란로 10")
                .longitude(127.123)
                .latitude(37.456)
                .description("테스트용 식당입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        List<Menu> menuList = List.of(
                Menu.builder()
                        .id("1")
                        .restaurant(restaurant)
                        .name("김치찌개")
                        .price(BigDecimal.valueOf(8000))
                        .status(MenuStatus.SELLING)
                        .description("국물이 진한 김치찌개입니다.")
                        .build(),
                Menu.builder()
                        .id("1")
                        .restaurant(restaurant)
                        .name("된장찌개")
                        .price(BigDecimal.valueOf(8500))
                        .status(MenuStatus.SELLING)
                        .description("구수한 된장찌개입니다.")
                        .build()
        );

        given(restaurantRepository.findById(restaurant.getRestaurantId()))
                .willReturn(Optional.of(restaurant));

        given(menuRepository.findByRestaurant(restaurant))
                .willReturn(menuList);

        List<MenuResponseDto> result = menuService.getMenuByRestaurantId(restaurant.getRestaurantId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("김치찌개");
        assertThat(result.get(0).getPrice()).isEqualTo(BigDecimal.valueOf(8000));
        assertThat(result.get(1).getName()).isEqualTo("된장찌개");
        assertThat(result.get(1).getPrice()).isEqualTo(BigDecimal.valueOf(8500));

        then(restaurantRepository).should(times(1))
                .findById(restaurant.getRestaurantId());
        then(menuRepository).should(times(1)).findByRestaurant(restaurant);
    }

    @Disabled
    @Test
    @DisplayName("메뉴 조회 실패 : 존재하지 않는 식당 아이디 요청으로 예외")
    void failGetMenuByRestaurantId() {
        String invalidRestaurantId = "NOT_EXIST_ID";

        given(restaurantRepository.findById(invalidRestaurantId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuByRestaurantId(invalidRestaurantId))
                .isInstanceOf(RestaurantException.class)
                .hasMessage("일치하는 음식점을 찾을 수 없습니다.");

        then(restaurantRepository).should(times(1)).findById(invalidRestaurantId);
        then(menuRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("메뉴 추가 성공")
    void createMenuItem() {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId("1")
                .name("한식당")
                .phone("010-1234-5678")
                .address("서울시 강남구 테헤란로 10")
                .longitude(127.123)
                .latitude(37.456)
                .description("테스트용 식당입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        MenuCreateRequestDto dto = MenuCreateRequestDto.builder()
                .restaurant(restaurant)
                .name("카레")
                .price(BigDecimal.valueOf(10000))
                .status(MenuStatus.SELLING)
                .isAiDescGenerated(false)
                .build();

        List<Menu> menuList = List.of(Menu.from(dto));

        given(restaurantRepository.findById("1")).willReturn(Optional.of(restaurant));
        given(menuRepository.saveAll(anyList())).willReturn(anyList());

        menuService.createMenuItem(restaurant.getRestaurantId(), List.of(dto));

        then(restaurantRepository).should(times(1))
                .findById(restaurant.getRestaurantId());

        then(menuRepository).should(times(1))
                .saveAll(anyList());
    }

    @Disabled
    @Test
    @DisplayName("메뉴 추가 실패 : 존재하지 않는 음식점 아이디로 예외")
    void failCreateMenuItem() {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId("1")
                .name("한식당")
                .phone("010-1234-5678")
                .address("서울시 강남구 테헤란로 10")
                .longitude(127.123)
                .latitude(37.456)
                .description("테스트용 식당입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        MenuCreateRequestDto dto = MenuCreateRequestDto.builder()
                .restaurant(restaurant)
                .name("카레")
                .price(BigDecimal.valueOf(10000))
                .status(MenuStatus.SELLING)
                .isAiDescGenerated(false)
                .build();

        List<Menu> menuList = List.of(Menu.from(dto));

        String invalidId = "NOT_EXIST_ID";

        given(restaurantRepository.findById(invalidId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.createMenuItem(invalidId, List.of(dto)))
                .isInstanceOf(RestaurantException.class)
                .hasMessage("일치하는 음식점을 찾을 수 없습니다.");

        then(menuRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("메뉴 생성 실패 : 빈 메뉴 리스트로 요청시 예외")
    void failCreateMenuItem_emptyMenuListRequest() {
        assertThatThrownBy(() -> menuService.createMenuItem("1", List.of()))
                .isInstanceOf(MenuException.class)
                .hasMessage("빈 리스트로 잘못된 요청입니다.");

        then(menuRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("메뉴 수정 성공")
    void successUpdateMenu() {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId("1")
                .name("한식당")
                .phone("010-1234-5678")
                .address("서울시 강남구 테헤란로 10")
                .longitude(127.123)
                .latitude(37.456)
                .description("테스트용 식당입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        Menu curry = Menu.builder()
                .name("카레")
                .restaurant(restaurant)
                .price(BigDecimal.valueOf(10000))
                .status(MenuStatus.SELLING)
                .description("신선한 야채와 고기로 만든 카레입니다")
                .build();

        MenuUpdateRequestDto updateReq = MenuUpdateRequestDto.builder()
                .id("101")
                .name("치킨 카레")
                .price(BigDecimal.valueOf(12000))
                .description("치킨이 들어간 카레입니다.")
                .status(MenuStatus.SELLING)
                .build();

        when(restaurantRepository.findById(restaurant.getRestaurantId())).thenReturn(Optional.of(restaurant));
        when(menuRepository.findById("101")).thenReturn(Optional.of(curry));

        menuService.updateMenuItem(restaurant.getRestaurantId(), List.of(updateReq));

        assertAll(
                () -> assertEquals("치킨 카레", curry.getName()),
                () -> assertEquals(BigDecimal.valueOf(12000), curry.getPrice()),
                () -> assertEquals("치킨이 들어간 카레입니다.", curry.getDescription()),
                () -> assertEquals(MenuStatus.SELLING, curry.getStatus())
        );

        verify(restaurantRepository, times(1)).findById(restaurant.getRestaurantId());
        verify(menuRepository, times(1)).findById("101");
    }

    @Test
    @DisplayName("메뉴 수정 실패 : 존재하지 않는 식당 아이디로 수정시 예외")
    void failUpdateMenu_NotExistRestaurantId() {
        String invalidId = "NOT_EXIST_ID";

        MenuUpdateRequestDto req = MenuUpdateRequestDto.builder()
                .id("1")
                .name("치킨 카레")
                .build();

        when(restaurantRepository.findById(invalidId)).thenReturn(Optional.empty());

        RestaurantException e = assertThrows(RestaurantException.class,
                () -> menuService.updateMenuItem(invalidId, List.of(req)));

        assertEquals(RESTAURANT_NOT_FOUND, e.getResponseCode());
    }

    @Test
    @DisplayName("메뉴 수정 실패 : 다른 식당의 메뉴 수정 수정시 예외")
    void failUpdateMenuItem_otherRestaurantMenuUpdateRequest() {
        String otherRestaurantId = "OTHER_RESTAURANT_ID";
        MenuUpdateRequestDto req = MenuUpdateRequestDto.builder()
                .id("101")
                .name("치킨 카레")
                .price(BigDecimal.valueOf(12000))
                .description("치킨이 들어간 카레입니다.")
                .status(MenuStatus.SELLING)
                .build();

        when(restaurantRepository.findById(otherRestaurantId))
                .thenThrow(new MenuException(MENU_NOT_IN_RESTAURANT));

        MenuException e = assertThrows(MenuException.class,
                () -> menuService.updateMenuItem(otherRestaurantId, List.of(req)));

        assertEquals(MENU_NOT_IN_RESTAURANT, e.getResponseCode());
    }

    @Test
    @DisplayName("메뉴 삭제 실패 : 다른 식당의 메뉴 삭제시 예외")
    void failUpdateMenuItem_otherRestaurantMenuDeleteRequest() {
        String otherRestaurantId = "OTHER_RESTAURANT_ID";
        List<String> menuIdList = List.of("1", "2", "3");

        when(restaurantRepository.findById(otherRestaurantId))
                .thenThrow(new MenuException(MENU_NOT_IN_RESTAURANT));

        MenuException e = assertThrows(MenuException.class,
                () -> menuService.deleteMenuItem(otherRestaurantId, menuIdList));

        assertEquals(MENU_NOT_IN_RESTAURANT, e.getResponseCode());
    }

    @Test
    @DisplayName("메뉴 수정 실패 : 메뉴 리스트가 비어있을 경우 예외")
    void failUpdateMenuItem_emptyMenuListRequest() {
        List<MenuUpdateRequestDto> req = List.of();

        MenuException e = assertThrows(MenuException.class,
                () -> menuService.updateMenuItem("1", req));

        assertEquals(REQUEST_EMPTY_LIST, e.getResponseCode());
    }

    @Test
    @DisplayName("메뉴 수정 실패 : 존재하지 않는 메뉴 아이디로 요청시 예외")
    void failUpdateMenu_NotExistMenuId() {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId("1")
                .name("한식당")
                .build();

        MenuUpdateRequestDto req = MenuUpdateRequestDto.builder()
                .id("999")
                .name("없는메뉴")
                .build();

        when(restaurantRepository.findById("1")).thenReturn(Optional.of(restaurant));
        when(menuRepository.findById("999")).thenReturn(Optional.empty());

        MenuException e = assertThrows(MenuException.class,
                () -> menuService.updateMenuItem("1", List.of(req)));

        assertEquals(MENU_NOT_FOUND, e.getResponseCode());
    }

    @Test
    @DisplayName("메뉴 삭제 성공")
    void successDeleteMenu() {
        Restaurant curryRestaurant = Restaurant.builder()
                .restaurantId("1")
                .name("카레 식당")
                .phone("010-1234-5678")
                .address("서울시 강남구 테헤란로 10")
                .longitude(127.123)
                .latitude(37.456)
                .description("테스트용 식당입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        Menu curry = Menu.builder()
                .id("1")
                .restaurant(curryRestaurant)
                .name("카레")
                .price(BigDecimal.valueOf(10000))
                .status(MenuStatus.SELLING)
                .build();

        List<String> menuIdList = List.of(curry.getId());
        List<Menu> foundMenuList = List.of(curry);

        when(restaurantRepository.findById(curryRestaurant.getRestaurantId()))
                .thenReturn(Optional.of(curryRestaurant));

        when(menuRepository.findAllById(menuIdList))
                .thenReturn(foundMenuList);

        menuService.deleteMenuItem(curryRestaurant.getRestaurantId(), menuIdList);

        verify(menuRepository, times(1)).deleteAll(foundMenuList);
        verify(restaurantRepository, times(1)).findById(curryRestaurant.getRestaurantId());
        verify(menuRepository, times(1)).findAllById(menuIdList);
    }

    @Test
    @DisplayName("메뉴 삭제 실패 : 존재하지 않는 식당 아이디로 요청시 예외")
    void failDeleteMenu_NotExistRestaurantId() {
        String invalidId = "NOT_EXIST_ID";
        List<String> menuIdList = List.of(invalidId);

        when(restaurantRepository.findById(invalidId))
                .thenThrow(new RestaurantException(RESTAURANT_NOT_FOUND));

        RestaurantException e = assertThrows(RestaurantException.class,
                () -> menuService.deleteMenuItem(invalidId, menuIdList)
        );

        assertEquals(RESTAURANT_NOT_FOUND, e.getResponseCode());

        verify(menuRepository, never()).deleteAll(anyList());
        verify(restaurantRepository, never()).findAllById(anyList());
    }

    @Test
    @DisplayName("메뉴 삭제 실패 : 식당은 있지만 없는 메뉴 아이디로 삭제 요청시 예외")
    void failDeleteMenu_NotExistMenuId() {
        String restaurantId = "1";
        String invalidMenuId = "NOT_EXIST_ID";

        Restaurant restaurant = Restaurant.builder()
                .restaurantId(restaurantId)
                .name("한식당")
                .build();

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        when(menuRepository.findAllById(List.of(invalidMenuId)))
                .thenReturn(List.of());

        MenuException e = assertThrows(MenuException.class,
                () -> menuService.deleteMenuItem("1", List.of(invalidMenuId))
        );

        assertEquals(MENU_NOT_FOUND, e.getResponseCode());
        verify(menuRepository, never()).deleteAll(anyList());
        verify(menuRepository, times(1)).findAllById(List.of(invalidMenuId));
        verify(restaurantRepository, times(1)).findById(restaurantId);
    }

    @Test
    @DisplayName("메뉴 삭제 실패 : 이이디 리스트가 비어있을 때 예외")
    void failDeleteMenuItem_emptyMenuListRequest() {
        List<String> emptyMenuIdList = List.of();

        MenuException e = assertThrows(MenuException.class,
                () -> menuService.deleteMenuItem("1", emptyMenuIdList));

        assertThat(e.getResponseCode()).isEqualTo(REQUEST_EMPTY_LIST);
    }
}
