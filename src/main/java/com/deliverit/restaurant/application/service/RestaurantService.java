package com.deliverit.restaurant.application.service;

import com.deliverit.global.exception.RestaurantException;
import com.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.deliverit.global.persistence.UseActiveRestaurantFilter;
import com.deliverit.menu.application.service.MenuService;
import com.deliverit.menu.presentation.dto.MenuResponseDto;
import com.deliverit.restaurant.domain.entity.Restaurant;
import com.deliverit.restaurant.domain.model.PageSize;
import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import com.deliverit.restaurant.domain.model.SortType;
import com.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.deliverit.restaurant.infrastructure.api.map.MapService;
import com.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import com.deliverit.restaurant.presentation.dto.*;
import com.sparta.deliverit.restaurant.presentation.dto.*;
import com.deliverit.user.domain.entity.User;
import com.deliverit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.deliverit.global.response.code.RestaurantResponseCode.*;
import static com.deliverit.global.response.code.UserResponseCode.NOT_FOUND_USER;
import static com.deliverit.global.response.code.UserResponseCode.UNAUTHORIZED_USER;
import static com.deliverit.restaurant.domain.model.RestaurantStatus.SHUTDOWN;
import static com.deliverit.restaurant.domain.model.SortType.*;
import static com.deliverit.user.domain.entity.UserRoleEnum.OWNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final MapService mapService;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuService menuService;

    /**
     * createRestaurant 음식점 등록 비즈니스 로직
     *
     * @param requestDto 음식점 정보
     * @param user       음식점을 등록하는 현재 사용자 정보
     * @return RestaurantInfoResponseDto 저장한 음식점 정보
     * <p>
     * 요청 정보를 바탕으로 새로운 음식점 정보를 저장합니다.
     * UUID를 사용하여 음식점 고유 식별자를 생성합니다.
     * 요청 status 값이 SHUTDOWN일 경우, 예외를 반환합니다.
     * Kakao Map API를 호출하여 요청 주소를 좌표로 변환합니다.
     * 음식점을 등록하는 사용자가 실제 소유주(OWNER)일 경우 현재 사용자의 정보를 저장하고,
     * 관리자(MANAGER, MASTER)일 경우 실제 소유주의 정보를 불러와서 저장합니다.
     */
    @Transactional
    public RestaurantInfoResponseDto createRestaurant(RestaurantInfoRequestDto requestDto, UserDetailsImpl user) {
        log.info("[RestaurantService] createRestaurant 실행");
        log.debug("createRestaurant - requestDto={}", requestDto);

        validateNotShutdownTransition(requestDto.getStatus());

        Restaurant restaurant = Restaurant.from(requestDto, generateRestaurantId());
        log.debug("createRestaurant - restaurantId 생성: restaurantId={}", restaurant.getRestaurantId().substring(0, 5));

        EnumSet<RestaurantCategory> categories = EnumSet.copyOf(requestDto.getCategories());
        restaurant.assignCategories(categories);
        log.debug("카테고리 등록: categories={}", categories);

        Coordinates geocode = mapService.geocode(requestDto.getAddress());
        restaurant.updateCoordinates(geocode.getLongitude(), geocode.getLatitude());
        log.debug("좌표 정보 등록: lon={}, lat={}", geocode.getLongitude(), geocode.getLatitude());

        boolean isOwner = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(OWNER.getAuthority()));

        if (isOwner) {
            restaurant.assignUser(user.getUser());
        } else {
            String ownerId = requestDto.getOwnerId();
            log.debug("음식점 소유주 아이디: ownerId={}", ownerId);

            if (!StringUtils.hasText(ownerId)) {
                log.error("유효하지 않는 사용자 아이디: ownerId={}", ownerId);
                throw new RestaurantException(INVALID_OWNER_ID);
            }

            User owner = userRepository.findByUsername(ownerId)
                    .orElseThrow(() -> {
                        log.error("일치하는 사용자를 찾을 수 없습니다.: OwnerId={}", requestDto.getOwnerId());
                        return new RestaurantException(NOT_FOUND_USER);
                    });

            if (!owner.getRole().equals(OWNER)) {
                log.error("사용자 권한이 일치하지 않습니다. ownerRole={}", owner.getRole());
                throw new RestaurantException(UNAUTHORIZED_USER);
            }

            restaurant.assignUser(owner);
        }
        log.debug("음식점 소유주 등록: ownerId={}", restaurant.getUser().getUsername());

        restaurantRepository.save(restaurant);
        log.debug("음식점 등록 성공: restaurantName={}", restaurant.getName());

        log.info("[RestaurantService] createRestaurant 종료");
        return RestaurantInfoResponseDto.from(restaurant);
    }

    /**
     * getRestaurantList 음식점 목록 조회 비즈니스 로직
     *
     * @param requestDto 요청 정보
     * @return Page<RestaurantListResponseDto> 조회한 음식점 목록
     * <p>
     * 요청 정보를 바탕으로 음식점 목록을 조회합니다.
     * 페이징, 음식점 이름 검색, 카테고리 필터링, 정렬 기능을 제공합니다.
     * 페이징의 경우 size를 10, 30, 50으로 제한하며, 10을 기본으로 설정합니다.
     * 페이징의 경우 0을 page의 기본 값으로 설정하며, 음수를 제한합니다.
     * 정렬의 경우 생성일순, 거리순, 별점순을 제공하며, 최신일순을 기본으로 정렬합니다.
     */
    @UseActiveRestaurantFilter
    @Transactional(readOnly = true)
    public Page<RestaurantListResponseDto> getRestaurantList(RestaurantListRequestDto requestDto) {
        log.info("[RestaurantService] getRestaurantList 실행: requestDto={}", requestDto.toString());
        log.debug("getRestaurantList - requestDto={}", requestDto);

        int page = Math.max(0, requestDto.getPage());
        int size = PageSize.normalize(requestDto.getSize());
        log.debug("페이징 요청 정보: page={}, size={}", page, size);

        SortType requestType = SortType.normalize(requestDto.getSort());
        Sort.Direction direction = requestDto.getDirection() == null ? Sort.Direction.DESC : requestDto.getDirection();
        log.debug("정렬 요청 정보: sort={}, direction={}", requestType.field(), direction);

        Sort sort = Sort.by(new Sort.Order(direction, requestType.field()));
        Pageable pageable = PageRequest.of(page, size, sort);

        double latitude = requestDto.getLatitude();
        double longitude = requestDto.getLongitude();
        String keyword = requestDto.getNormalizedKeyword();
        RestaurantCategory category = requestDto.getCategory();
        log.debug("위치 정보: latitude={}, longitude={}", latitude, longitude);

        Pageable filtered = sanitizeSort(pageable, Set.of(CREATED_AT.field(), DISTANCE.field(), RATING.field()));
        SortType type = resolveSortType(filtered);
        log.debug("음식점 전체 목록 조회: page={}, sort={}", pageable.getPageSize(), type);

        log.info("[RestaurantService] getRestaurantList 종료");
        return switch (type) {
            case CREATED_AT -> {
                Pageable p = keepOnlyCreatedAtOrDefault(filtered);
                log.debug("createdAt sort={}", p.getSort());
                yield restaurantRepository.searchByCreatedAt(latitude, longitude, keyword, category, p);
            }
            case DISTANCE -> {
                Pageable p = forceDistanceAscFirst(filtered);
                log.debug("distance sort={}", p.getSort());
                yield restaurantRepository.searchOrderByDistance(latitude, longitude, keyword, category, p);
            }
            case RATING -> {
                Pageable p = keepOnlyRatingOrDefault(filtered);
                log.debug("rating sort={}", p.getSort());
                yield restaurantRepository.searchByRating(latitude, longitude, keyword, category, p);
            }
        };
    }

    /**
     * getRestaurantDetail 음식점 단일 조회 비즈니스 로직
     *
     * @param restaurantId 음식점 고유 식별자
     * @return RestaurantDetailResponseDto 일치하는 음식점 정보
     * <p>
     * 요청받은 음식점 고유 식별자와 일치하는 음식점 정보를 조회하고, 일치하는 정보가 없을 시 예외를 반환합니다.
     * 요청받은 음식점이 폐업 상태일 경우 예외를 반환합니다.
     * 음식점 정보를 조회할 때 해당 음식점의 메뉴 목록도 함께 조회합니다.
     */
    @UseActiveRestaurantFilter
    @Transactional(readOnly = true)
    public RestaurantDetailResponseDto getRestaurantDetail(String restaurantId) {
        log.info("[RestaurantService] getRestaurantInfo 실행");
        log.debug("getRestaurantDetail - restaurantId={}", restaurantId);

        Restaurant restaurant = getRestaurant(restaurantId);
        ensureNotShutdown(restaurant);

        List<MenuResponseDto> menu = menuService.getMenuByRestaurantId(restaurantId);
        log.debug("음식점 메뉴 목록 조회: menuSize={}", menu.size());

        log.info("[RestaurantService] getRestaurantInfo 종료");
        return RestaurantDetailResponseDto.from(restaurant, menu);
    }

    /**
     * updateRestaurant 음식점 정보 수정 비즈니스 로직
     *
     * @param restaurantId 음식점 고유 식별자
     * @param requestDto   수정할 음식점 정보
     * @param user         음식점을 수정하는 현재 사용자 정보
     * @return RestaurantInfoResponseDto 수정한 음식점 정보
     * <p>
     * 요청 정보를 바탕으로 기존 음식점 정보를 수정합니다.
     * 요청받은 음식점 고유 식별자와 일치하는 음식점 정보를 조회하고, 일치하는 정보가 없을 시 예외를 반환합니다.
     * 현재 사용자의 권한이 소유주(OWNER)일 경우 음식점 소유자가 맞는지 검증하고, 일치하지 않을 경우 예외를 반환합니다.
     * 요청받은 음식점이 폐업 상태일 경우 예외를 반환합니다.
     * 요청 status 값이 SHUTDOWN일 경우, 예외를 반환합니다.
     */
    @Transactional
    public RestaurantInfoResponseDto updateRestaurant(
            String restaurantId, RestaurantInfoRequestDto requestDto, UserDetailsImpl user) {
        log.info("[RestaurantService] updateRestaurant 실행");
        log.debug("updateRestaurant - restaurantId={}", restaurantId);

        Restaurant restaurant = getRestaurant(restaurantId);

        validateRestaurantOwner(restaurant, user);
        ensureNotShutdown(restaurant);
        validateNotShutdownTransition(requestDto.getStatus());

        EnumSet<RestaurantCategory> categories = EnumSet.copyOf(requestDto.getCategories());
        log.debug("카테고리 매핑: categories={}", categories);

        Coordinates geocode = mapService.geocode(requestDto.getAddress());

        restaurant.update(requestDto, categories, geocode);
        log.debug("음식점 정보 수정 성공: restaurantName={}", restaurant.getName());

        log.info("[RestaurantService] updateRestaurant 종료");
        return RestaurantInfoResponseDto.from(restaurant);
    }

    /**
     * updateRestaurantStatus 음식점 상태 수정 비즈니스 로직
     *
     * @param restaurantId 음식점 고유 식별자
     * @param status       음식점 상태 정보
     * @param user         음식점을 수정하는 현재 사용자 정보
     * @return RestaurantInfoResponseDto 수정한 음식점 정보
     * <p>
     * 요청 정보를 바탕으로 기존 음식점의 상태 정보를 수정합니다.
     * 요청받은 음식점 고유 식별자와 일치하는 음식점 정보를 조회하고, 일치하는 정보가 없을 시 예외를 반환합니다.
     * 현재 사용자의 권한이 소유주(OWNER)일 경우 음식점 소유자가 맞는지 검증하고, 일치하지 않을 경우 예외를 반환합니다.
     * 요청받은 음식점이 폐업 상태일 경우 예외를 반환합니다.
     * 요청 status 값이 SHUTDOWN일 경우, 예외를 반환합니다.
     */
    @Transactional
    public RestaurantInfoResponseDto updateRestaurantStatus(
            String restaurantId, RestaurantStatus status, UserDetailsImpl user
    ) {
        log.info("[RestaurantService] updateRestaurantStatus 실행");
        log.debug("updateRestaurantStatus - restaurantId={}, status={}", restaurantId, status);

        Restaurant restaurant = getRestaurant(restaurantId);

        validateRestaurantOwner(restaurant, user);
        ensureNotShutdown(restaurant);
        validateNotShutdownTransition(status);

        restaurant.updateStatus(status);
        log.debug("음식점 상태 수정 성공: status={}", restaurant.getStatus());

        log.info("[RestaurantService] updateRestaurantStatus 종료");
        return RestaurantInfoResponseDto.from(restaurant);
    }

    /**
     * deleteRestaurant 음식점 삭제 비즈니스 로직 (Soft Delete)
     *
     * @param restaurantId 음식점 고유 식별자
     * @param user         음식점을 삭제하는 현재 사용자 정보
     */
    @Transactional
    public void deleteRestaurant(String restaurantId, UserDetailsImpl user) {
        log.info("[RestaurantService] deleteRestaurant 실행");
        log.debug("deleteRestaurant - restaurantId={}", restaurantId);

        Restaurant restaurant = getRestaurant(restaurantId);

        validateRestaurantOwner(restaurant, user);
        ensureNotShutdown(restaurant);
        validateNotShutdownTransition(restaurant.getStatus());

        restaurant.softDelete();
        log.debug("음식점 삭제 성공: restaurantStatus={}", restaurant.getStatus());

        log.info("[RestaurantService] deleteRestaurant 종료");
    }

    // ====================================== 유틸 메서드 ======================================

    // 음식점 고유 식별자 생성
    private static String generateRestaurantId() {
        return UUID.randomUUID().toString();
    }

    // 음식점 상태 제한 (SHUTDOWN은 DELETE 요청에서만 허용)
    private void validateNotShutdownTransition(RestaurantStatus status) {
        log.debug("요청 음식점 상태 정보: status={}", status);

        if (status.equals(SHUTDOWN)) {
            log.error("SHUTDOWN은 DELETE에서만 접근 가능: status={}", status);
            throw new RestaurantException(INVALID_STATUS_TRANSITION);
        }
    }

    // 음식점 영업 상태 확인
    private void ensureNotShutdown(Restaurant restaurant) {
        RestaurantStatus status = restaurant.getStatus();
        log.debug("음식점 영업 상태: status={}", status);

        if (status == SHUTDOWN) {
            log.error("폐업한 가게는 수정 및 삭제 불가능: restaurantId={}", restaurant.getRestaurantId());
            throw new RestaurantException(RESTAURANT_FORBIDDEN);
        }
    }

    // 음식점 조회
    private Restaurant getRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId)
                .orElseThrow(() -> {
                    log.error("일치하는 음식점을 찾을 수 없습니다. restaurantId={}", restaurantId);
                    return new RestaurantException(RESTAURANT_NOT_FOUND);
                });

        log.debug("Restaurant 조회 결과: restaurantName={}", restaurant.getName());
        return restaurant;
    }

    // 음식점 소유주 정보 및 권한 확인
    private void validateRestaurantOwner(Restaurant restaurant, UserDetailsImpl userDetails) {
        log.debug("소유주 권한 검증: restaurantOwnerId={}, currentUserId={}",
                restaurant.getUser().getUsername(), userDetails.getUsername());

        boolean isOwner = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(OWNER.getAuthority()));
        boolean isOwnerMismatch = !userDetails.getUsername().equals(restaurant.getUser().getUsername());

        if (isOwner && isOwnerMismatch) {
            log.error("음식점 소유주가 일치하지 않습니다.: restaurantOwnerId={}, currentUserId={}",
                    restaurant.getUser().getUsername(), userDetails.getUsername());
            throw new RestaurantException(RESTAURANT_FORBIDDEN);
        }
    }

    // pageable sort 문자열 -> enum 매핑
    private SortType resolveSortType(Pageable pageable) {
        boolean hasCreateAt = pageable.getSort().stream()
                .anyMatch(o -> o.getProperty().equalsIgnoreCase(CREATED_AT.field()));
        boolean hasDistance = pageable.getSort().stream()
                .anyMatch(o -> o.getProperty().equalsIgnoreCase(DISTANCE.field()));
        boolean hasRating = pageable.getSort().stream()
                .anyMatch(o -> o.getProperty().equalsIgnoreCase(RATING.field()));

        if (hasCreateAt) return CREATED_AT;
        if (hasDistance) return DISTANCE;
        if (hasRating) return RATING;

        return CREATED_AT; // 작성일순 default 설정
    }

    // 거리순, 별점순 정렬 외 조건 제한 및 페이지 설정
    private Pageable sanitizeSort(Pageable pageable, Set<String> whitelist) {
        List<Sort.Order> keep = pageable.getSort().stream()
                .filter(o -> whitelist.contains(o.getProperty()))
                .toList();

        Sort sort = keep.isEmpty()
                ? Sort.by(Sort.Order.desc(CREATED_AT.field()))
                : Sort.by(keep);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    // 생성일순(DESC(default), ASC) 정렬 페이지 설정
    private Pageable keepOnlyCreatedAtOrDefault(Pageable pageable) {
        List<Sort.Order> createdOnly = pageable.getSort().stream()
                .filter(o -> o.getProperty().equalsIgnoreCase(CREATED_AT.field()))
                .toList();
        Sort sort = createdOnly.isEmpty()
                ? Sort.by(Sort.Order.desc(CREATED_AT.field()))
                : Sort.by(createdOnly);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    // 거리순(ASC) 정렬 페이지 설정
    private Pageable forceDistanceAscFirst(Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.asc(DISTANCE.field()));

        pageable.getSort().forEach(o -> {
            if (!o.getProperty().equalsIgnoreCase(DISTANCE.field())) orders.add(o);
        });

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }

    // 별점순(DESC(default), ASC) 정렬 페이지 설정
    private Pageable keepOnlyRatingOrDefault(Pageable pageable) {
        List<Sort.Order> ratingOnly = pageable.getSort().stream()
                .filter(o -> o.getProperty().equalsIgnoreCase(RATING.field()))
                .toList();

        Sort sort = ratingOnly.isEmpty()
                ? Sort.by(Sort.Order.desc(RATING.field()))
                : Sort.by(ratingOnly);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
