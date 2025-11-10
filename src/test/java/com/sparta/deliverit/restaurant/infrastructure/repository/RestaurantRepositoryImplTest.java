package com.sparta.deliverit.restaurant.infrastructure.repository;

import com.deliverit.global.config.AuditingConfig;
import com.deliverit.global.config.QuerydslConfig;
import com.deliverit.restaurant.domain.entity.Restaurant;
import com.deliverit.restaurant.domain.model.PageSize;
import com.deliverit.restaurant.domain.model.RestaurantCategory;
import com.deliverit.restaurant.domain.model.RestaurantStatus;
import com.deliverit.restaurant.domain.vo.RestaurantRating;
import com.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import com.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import com.deliverit.user.domain.entity.User;
import com.deliverit.user.domain.repository.UserRepository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static com.deliverit.restaurant.domain.model.SortType.CREATED_AT;
import static com.deliverit.restaurant.domain.model.SortType.RATING;
import static com.deliverit.user.domain.entity.UserRoleEnum.OWNER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, AuditingConfig.class})
class RestaurantRepositorySliceTest {

    @Autowired
    private RestaurantRepository rr;

    @Autowired
    private UserRepository ur;

    @Autowired
    private EntityManager em;

    private static final int PAGE_NUMBER = 0;
    private static final int REQUEST_PAGE_SIZE = 15;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final double GWANGHWAMUN_LAT = 37.5759;
    private static final double GWANGHWAMUN_LON = 126.9768;
    private static final String KEYWORD = "식당";
    private static final RestaurantCategory CATEGORY = KOREAN_FOOD;


    private void save(
            User user, String id, String name, double longitude, double latitude,
            RestaurantStatus status, double starAvg, Long reviewsCount, Set<RestaurantCategory> categories
    ) throws InterruptedException {
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(id)
                .name(name)
                .address("테스트 주소").phone("00-000-0000")
                .description("테스트용 더미 데이터")
                .longitude(longitude)
                .latitude(latitude)
                .status(status)
                .categories(categories)
                .user(user)
                .rating(new RestaurantRating(new BigDecimal(starAvg), reviewsCount))
                .build();

        em.persist(restaurant);
        Thread.sleep(200);
    }

    @SafeVarargs
    private static Set<RestaurantCategory> cats(RestaurantCategory... cs) {
        return cs.length == 0 ? EnumSet.noneOf(RestaurantCategory.class) : EnumSet.copyOf(Arrays.asList(cs));
    }

    // 하이버네이트 엔티티 필터 활성화 및 시드 데이터 생성
    @BeforeEach
    void seed() throws InterruptedException {
        em.unwrap(Session.class).enableFilter("activeRestaurantFilter");

        User owner = ur.save(new User("owner1", "encoded-password", "김오너", "010-0000-0000", OWNER));

        // 제한 거리 외
        save(owner, "R01", "광화문 한식당", 126.9768, 37.5759, OPEN, 4.4, 1435L, cats(KOREAN_FOOD));
        save(owner, "R02", "청계천 중식당", 126.9865, 37.5697, CLOSED, 4.2, 624L, cats(CHINESE_FOOD));
        save(owner, "R03", "경복궁 스시집", 126.9770, 37.5796, SHUTDOWN, 3.8, 154L, cats(JAPANESE_FOOD));
        save(owner, "R04", "세종로 치킨집", 126.9756, 37.5724, OPEN, 4.7, 1245L, cats(KOREAN_FOOD, CHICKEN));
        save(owner, "R05", "시청 일식당", 126.9779, 37.5663, OPEN, 4.5, 346L, cats(JAPANESE_FOOD));
        // 제한 거리 외
        save(owner, "R06", "홍대 떡볶이집", 126.9230, 37.5500, SHUTDOWN, 2.7, 834L, cats(KOREAN_FOOD, STREET_FOOD));
        save(owner, "R07", "강남 고기집", 127.0280, 37.4979, OPEN, 4.3, 765L, cats(KOREAN_FOOD, MEAT));

        em.flush();
        em.clear();
    }

    // 하이버네이트 엔티티 필터 비활성화
    @AfterEach
    void disableFilter() {
        em.unwrap(Session.class)
                .disableFilter("activeRestaurantFilter");
    }

    @Test
    @DisplayName("제한 거리 밖의 가게와 폐업한 가게는 조회되지 않는다.")
    void exclude_closed_and_out_of_range_restaurants() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, DEFAULT_PAGE_SIZE);

        // when
        var page = rr.searchByCreatedAt(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThat(list.size()).isEqualTo(4);
        assertThat(list).doesNotContain("R03", "R06");
    }

    @Test
    @DisplayName("page 기본 값은 0이며, size에 10, 30, 50외의 값이 들어오면 10으로 변환된다.")
    void page_default_zero_and_invalid_size_normalized_to_ten() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchByCreatedAt(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, null, pageable);
        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThat(page).isNotNull();
        assertThat(page.getSize()).isEqualTo(DEFAULT_PAGE_SIZE);
        assertThat(page.getNumber()).isZero();
    }

    @Test
    @DisplayName("searchByCreatedAt: 생성일 내림차순 정렬")
    void search_by_created_at_desc() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, DEFAULT_PAGE_SIZE);

        // when
        var page = rr.searchByCreatedAt(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThat(list).containsExactly("R05", "R04", "R02", "R01");
    }

    @Test
    @DisplayName("searchByCreatedAt: 생성일 오름차순 정렬")
    void search_by_created_at_asc() {
        // given
        var pageable = getPageableCreatedAtAsc(PAGE_NUMBER, DEFAULT_PAGE_SIZE);

        // when
        var page = rr.searchByCreatedAt(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, null, pageable);
        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThat(list).containsExactly("R01", "R02", "R04", "R05");
    }

    @Test
    @DisplayName("searchByCreatedAt: 이름에 '식당' 키워드가 포함된 결과를 최신순으로 정렬")
    void search_by_created_at_with_keyword_desc() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchByCreatedAt(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, KEYWORD, null, pageable);
        var list = page.getContent();

        // then
        assertThat(list.size()).isEqualTo(3);
        assertThat(list)
                .allMatch(dto -> dto.getName().contains(KEYWORD));
        assertThat(list).extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("R05", "R02", "R01");
    }

    @Test
    @DisplayName("searchByCreatedAt: 카테고리가 한식인 결과를 최신순으로 정렬")
    void search_by_created_at_with_category_desc() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchByCreatedAt(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, CATEGORY, pageable);
        var list = page.getContent();

        // then
        assertThat(list.size()).isEqualTo(2);
        assertThat(list).extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("R04", "R01");
    }

    @Test
    @DisplayName("searchByCreatedAt: 카테고리가 한식이면서 이름에 '식당' 키워드가 들어가는 결과를 최신순으로 정렬")
    void search_by_created_at_with_keyword_and_category_desc() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchByCreatedAt(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, KEYWORD, CATEGORY, pageable);
        var list = page.getContent();

        // then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list)
                .allMatch(dto -> dto.getName().contains(KEYWORD));
        assertThat(list).extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("R01");
    }

    @Test
    @DisplayName("searchOrderByDistance: 강화문 기준 가까운순으로 정렬")
    void search_by_distance_asc() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchOrderByDistance(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, null, pageable);
        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThat(list).containsExactly("R01", "R04", "R05", "R02");
    }

    @Test
    @DisplayName("searchOrderByDistance: 이름에 '식당' 키워드가 포함된 결과 중 강화문 기준 가까운순으로 정렬")
    void search_by_distance_with_search_asc() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchOrderByDistance(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, KEYWORD, null, pageable);
        var list = page.getContent();

        // then
        assertThat(list.size()).isEqualTo(3);
        assertThat(list)
                .allMatch(dto -> dto.getName().contains(KEYWORD));
        assertThat(list).extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("R01", "R05", "R02");
    }

    @Test
    @DisplayName("searchByRating: 별점 내림차순으로 정렬")
    void search_by_rating_desc() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchByRating(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, null, pageable);
        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThat(list).containsExactly("R04", "R05", "R01", "R02");
    }

    @Test
    @DisplayName("searchByRating: 별점 오름차순으로 정렬")
    void search_by_rating_asc() {
        // given
        var pageable = getPageableRatingAsc(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page = rr.searchByRating(GWANGHWAMUN_LAT, GWANGHWAMUN_LON, null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThat(list).containsExactly("R02", "R01", "R05", "R04");
    }

    Pageable getPageableDefault(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    Pageable getPageableCreatedAtAsc(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(CREATED_AT.field())));
    }

    Pageable getPageableRatingAsc(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(RATING.field())));
    }
}
