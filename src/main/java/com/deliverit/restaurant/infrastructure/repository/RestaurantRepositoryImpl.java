package com.deliverit.restaurant.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deliverit.restaurant.domain.entity.QRestaurant;
import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.deliverit.restaurant.domain.model.SortType.CREATED_AT;
import static com.deliverit.restaurant.domain.model.SortType.RATING;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory query;

    private static final QRestaurant restaurant = QRestaurant.restaurant;

    @Override
    public Page<RestaurantListResponseDto> searchByCreatedAt(
            double latitude, double longitude, String keyword, RestaurantCategory category, Pageable pageable
    ) {
        BooleanBuilder where = baseFilter(keyword, category);
        NumberExpression<Double> distance = applyDistanceFilter(where, latitude, longitude, keyword, category);

        List<RestaurantListResponseDto> content = query
                .select(Projections.constructor(RestaurantListResponseDto.class,
                        restaurant.restaurantId,
                        restaurant.name,
                        restaurant.rating.starAvg,
                        restaurant.rating.reviewsCount,
                        distance
                ))
                .from(restaurant)
                .where(where)
                .orderBy(orderByForCreatedAt(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query
                .select(restaurant.restaurantId.countDistinct())
                .from(restaurant)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    // 거리순 정렬
    @Override
    public Page<RestaurantListResponseDto> searchOrderByDistance(
            double latitude, double longitude, String keyword, RestaurantCategory category, Pageable pageable
    ) {
        // 1. 거리 계산 및 where절 구성
        BooleanBuilder where = baseFilter(keyword, category);
        NumberExpression<Double> distance = applyDistanceFilter(where, latitude, longitude, keyword, category);

        // 2. 필요한 필드 -> DTO 생성자 매핑
        List<RestaurantListResponseDto> content = query
                .select(Projections.constructor(RestaurantListResponseDto.class,
                        restaurant.restaurantId,
                        restaurant.name,
                        restaurant.rating.starAvg,
                        restaurant.rating.reviewsCount,
                        distance
                ))
                .from(restaurant)
                .where(where)
                .orderBy(orderByForDistance(pageable, distance))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3. 전체 개수 (페이지 계산)
        Long total = query
                .select(restaurant.restaurantId.countDistinct())
                .from(restaurant)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    // 별점순 정렬
    @Override
    public Page<RestaurantListResponseDto> searchByRating(
            double latitude, double longitude, String keyword, RestaurantCategory category, Pageable pageable
    ) {
        // 1. where절 구성
        BooleanBuilder where = baseFilter(keyword, category);
        NumberExpression<Double> distance = applyDistanceFilter(where, latitude, longitude, keyword, category);

        // 2. 필요한 필드 -> DTO 생성자 매핑
        List<RestaurantListResponseDto> content = query
                .select(Projections.constructor(RestaurantListResponseDto.class,
                        restaurant.restaurantId,
                        restaurant.name,
                        restaurant.rating.starAvg,
                        restaurant.rating.reviewsCount,
                        distance
                ))
                .from(restaurant)
                .where(where)
                .orderBy(orderByForRating(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3. 전체 개수 (페이지 계산)
        Long total = query
                .select(restaurant.restaurantId.countDistinct())
                .from(restaurant)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }


    private BooleanBuilder baseFilter(String keyword, RestaurantCategory category) {
        BooleanBuilder where = new BooleanBuilder();

        if (category != null) where.and(restaurant.categories.any().eq(category));
        if (StringUtils.hasText(keyword)) {
            where.and(restaurant.name.contains(keyword));
        }

        return where;
    }

    private static final double EARTH_RADIUS_M = 6371000d; // 지구 반지름(미터)
    private static final int DEFAULT_MAX_DISTANCE_M = 3000; // 제한 거리(미터, 3Km)

    // 거리 계산 및 where절 구현(제한 거리 필터링)
    private NumberExpression<Double> applyDistanceFilter(
            BooleanBuilder where, double latitude, double longitude, String keyword, RestaurantCategory category
    ) {
        NumberExpression<Double> distance = distance(latitude, longitude);
        where.and(distance.loe(DEFAULT_MAX_DISTANCE_M));

        return distance;
    }

    // DB 공통 하버사인 거리(미터) 계산식
    private NumberExpression<Double> distance(double latitude, double longitude) {
        return Expressions.numberTemplate(
                Double.class,
                "({0} * 2 * ASIN(SQRT( " +
                        "POWER(SIN((({1} * PI()/180 - {3} * PI()/180)/2)), 2) +" +
                        "COS({3} * PI()/180) * COS({1} * PI()/180) * " +
                        "POWER(SIN((({2} * PI()/180 - {4} * PI()/180)/2)), 2)" +
                        " )))",
                EARTH_RADIUS_M,
                restaurant.latitude,
                restaurant.longitude,
                latitude,
                longitude
        );
    }

    // 생성자순 정렬 - 거리 없이 createAt만 반영 / 오름차순, 내림차순 지정이 없을 경우 내림차순을 기본 설정
    private OrderSpecifier<?>[] orderByForCreatedAt(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        boolean hasCreated = false;
        for (Sort.Order o : pageable.getSort()) {
            if (CREATED_AT.field().equalsIgnoreCase(o.getProperty())) {
                hasCreated = true;
                orderSpecifiers.add(new OrderSpecifier<>(
                        o.isAscending() ? Order.ASC : Order.DESC, restaurant.createdAt));
            }
        }
        if (!hasCreated) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, restaurant.createdAt));
        }
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    // 거리순 정렬 - 오름차순을 기본 설정
    private OrderSpecifier<?>[] orderByForDistance(Pageable pageable, NumberExpression<Double> distanceKm) {
        List<OrderSpecifier<?>> list = new ArrayList<>();
        list.add(new OrderSpecifier<>(Order.ASC, distanceKm));

        return list.toArray(OrderSpecifier[]::new);
    }

    // 평점순 정렬 - 거리 없이 rating만 반영 / 오름차순, 내림차순 지정이 없을 경우 내림차순을 기본 설정
    private OrderSpecifier<?>[] orderByForRating(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        boolean hasRating = false;

        for (Sort.Order o : pageable.getSort()) {
            if (RATING.field().equalsIgnoreCase(o.getProperty())) {
                hasRating = true;
                orderSpecifiers.add(new OrderSpecifier<>(
                        o.isAscending() ? Order.ASC : Order.DESC, restaurant.rating.starAvg));
            }
        }

        if (!hasRating) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, restaurant.rating.starAvg));
        }
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}