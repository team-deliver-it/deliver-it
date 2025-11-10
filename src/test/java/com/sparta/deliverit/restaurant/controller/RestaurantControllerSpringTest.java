package com.sparta.deliverit.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.deliverit.restaurant.presentation.dto.RestaurantInfoResponseDto;
import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import com.deliverit.restaurant.presentation.controller.RestaurantController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.deliverit.restaurant.domain.model.RestaurantCategory.KOREAN_FOOD;
import static com.deliverit.restaurant.domain.model.RestaurantCategory.WESTERN_FOOD;
import static com.deliverit.restaurant.domain.model.RestaurantStatus.CLOSED;
import static com.deliverit.restaurant.domain.model.RestaurantStatus.OPEN;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class RestaurantControllerSpringTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RestaurantController controller;

    String restaurantId;
    String name;
    RestaurantStatus status;
    List<RestaurantCategory> categories;

    @BeforeEach
    void setUp() throws Exception {
        // 매번 식당 하나를 생성해서 id를 저장
        RestaurantInfoRequestDto request = RestaurantInfoRequestDto.builder()
                .name("소보키 강남점")
                .phone("050713530094")
                .address("서울 강남구 봉은사로18길 80 1층")
                .description("정모 필수 코스로 떠오른 핫플")
                .status(OPEN)
                .categories(List.of(KOREAN_FOOD, WESTERN_FOOD))
                .build();

        var perform = mockMvc.perform(post("/v1/restaurants")
                        .with(csrf())
                        .with(user("testOwner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var result = objectMapper.readValue(
                perform.getResponse().getContentAsByteArray(),
                RestaurantInfoResponseDto.class);

        restaurantId = result.getRestaurantId();
        name = result.getName();
        status = result.getStatus();
        categories = result.getCategories();
    }

    @Test
    @DisplayName("가게 등록 성공 - OWNER 권한")
    @WithMockUser(roles = "OWNER")
    void create_success_asOwner() throws Exception {
        // given
        RestaurantInfoRequestDto request = RestaurantInfoRequestDto.builder()
                .name("디핀고")
                .phone("050713718849")
                .address("서울 강남구 언주로150길 51 3층")
                .description("셰프 추천으로 완성되는 와인 페어링")
                .status(CLOSED)
                .categories(List.of(WESTERN_FOOD))
                .build();

        // when
        // 가게 등록
        var perform = mockMvc.perform(post("/v1/restaurants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var create = objectMapper.readValue(
                perform.getResponse().getContentAsByteArray(),
                RestaurantInfoResponseDto.class);

        // then
        assertThat(create.getRestaurantId()).isNotNull();
        assertThat(create.getName()).isEqualTo(request.getName());
        assertThat(create.getStatus()).isEqualTo(request.getStatus());
        assertThat(create.getCategories()).isEqualTo(request.getCategories());
    }

    @Test
    @DisplayName("가게 조회 성공 - CUSTOMER 권한")
    @WithMockUser(roles = "CUSTOMER")
    void get_success_asCUSTOMER() throws Exception {
        // given

        // when
        var perform = mockMvc.perform(get("/v1/restaurants/{id}", restaurantId))
                .andExpect(status().isOk())
                .andReturn();

        var get = objectMapper.readValue(
                perform.getResponse().getContentAsByteArray(),
                RestaurantInfoResponseDto.class);

        // then
        assertThat(get.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(get.getName()).isEqualTo(name);
        assertThat(get.getStatus()).isEqualTo(status);
        assertThat(get.getCategories()).containsExactlyInAnyOrderElementsOf(categories);
    }

    @Test
    @DisplayName("가게 수정 성공 - MASTER 권한")
    @WithMockUser(roles = "MASTER")
    void update_success_asMASTER() throws Exception {
        // given
        RestaurantInfoRequestDto after = RestaurantInfoRequestDto.builder()
                .name("디핀고")
                .phone("050713718849")
                .address("서울 강남구 언주로150길 51 3층")
                .description("셰프 추천으로 완성되는 와인 페어링")
                .status(CLOSED)
                .categories(List.of(WESTERN_FOOD))
                .build();

        // when
        var perform = mockMvc.perform(put("/v1/restaurants/{id}", restaurantId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(after)))
                .andExpect(status().isOk())
                .andReturn();

        var update = objectMapper.readValue(
                perform.getResponse().getContentAsByteArray(),
                RestaurantInfoResponseDto.class);

        // then
        assertThat(update.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(update.getName()).isEqualTo(after.getName());
        assertThat(update.getName()).isNotEqualTo(restaurantId);
        assertThat(update.getStatus()).isEqualTo(after.getStatus());
        assertThat(update.getStatus()).isNotEqualTo(status);
        assertThat(update.getCategories()).containsExactlyInAnyOrderElementsOf(after.getCategories());
    }

    @Test
    @DisplayName("가게 삭제 성공 - OWNER 권한")
    @WithMockUser(roles = "OWNER")
    void delete_success_asOWNER() throws Exception {
        // given

        // when & then
        var perform = mockMvc.perform(delete("/v1/restaurants/{id}", restaurantId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(perform.getResponse().getContentAsString())
                .isEqualTo("삭제가 성공적으로 완료되었습니다.");
    }
}