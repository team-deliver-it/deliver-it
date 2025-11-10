package com.sparta.deliverit.restaurant.domain.vo;

import com.deliverit.restaurant.domain.vo.RestaurantRating;
import com.deliverit.review.domain.vo.Review;
import com.deliverit.review.domain.vo.Star;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantRatingTest {

    @Test
    @DisplayName("음식점의 별점을 생성할 수 있다")
    void createRestaurantRating() {
        RestaurantRating restaurantRating = new RestaurantRating();

        assertNotNull(restaurantRating);
    }

    @Test
    @DisplayName("음식점의 별점의 기본값은 0이다")
    void defaultZeroValue() {
        RestaurantRating restaurantRating = new RestaurantRating();

        assertEquals(BigDecimal.valueOf(0.0), restaurantRating.getStarAvg());
        assertEquals(0L, restaurantRating.getReviewsCount());
    }

    @Nested
    class Add {

        @Test
        @DisplayName("리뷰 추가시 리뷰의 개수가 1 더해진다")
        void addReviewCount() {
            RestaurantRating restaurantRating = new RestaurantRating();
            Review newReview = review(1.0);

            RestaurantRating newRestaurantRating = restaurantRating.addReview(newReview);

            assertEquals(1L, newRestaurantRating.getReviewsCount());
        }

        @Test
        @DisplayName("기본값에서 별점 4.5 추가시 평균 4.5, 개수 1이 된다")
        void addFirst() {
            RestaurantRating restaurantRating = new RestaurantRating();
            Review newReview = review(4.5);

            RestaurantRating newRestaurantRating = restaurantRating.addReview(newReview);
            assertEquals(1L, newRestaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(4.5), newRestaurantRating.getStarAvg());
            assertEquals(0L, restaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(0.0), restaurantRating.getStarAvg());
        }

        @Test
        @DisplayName("리뷰 4.5 추가 후 3.2 추가시 평균 별점 3.8, 개수 2가 된다")
        void addSecond() {
            RestaurantRating restaurantRating = new RestaurantRating();
            Review review1 = review(4.5);
            Review review2 = review(3.2);

            RestaurantRating rating1 = restaurantRating.addReview(review1);
            RestaurantRating rating2 = rating1.addReview(review2);

            assertEquals(2L, rating2.getReviewsCount());
            assertEquals(BigDecimal.valueOf(3.8), rating2.getStarAvg());
        }

        @Test
        @DisplayName("리뷰 4.9를 3번 추가하면 평균 4.9가 된다")
        void addThird() {
            var rating = new RestaurantRating()
                    .addReview(review(4.9))
                    .addReview(review(4.9))
                    .addReview(review(4.9));

            assertEquals(BigDecimal.valueOf(4.9), rating.getStarAvg());
        }
    }

    @Nested
    class Update {

        @Test
        @DisplayName("기존 리뷰 1.0 을 4.9로 교체하면 평균 별점은 4.9이다")
        void update() {
            Review oldReview = review(1.0);
            var restaurantRating = new RestaurantRating()
                    .addReview(oldReview);
            Review newReview = review(4.9);

            restaurantRating = restaurantRating.updateReview(oldReview, newReview);

            assertEquals(1L, restaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(4.9), restaurantRating.getStarAvg());
        }

        @Test
        @DisplayName("동일한 별점으로 수정하면 기존값 그대로이다")
        void sameValue() {
            Review oldReview = review(4.9);
            var restaurantRating = new RestaurantRating()
                    .addReview(oldReview);

            restaurantRating = restaurantRating.updateReview(oldReview, review(4.9));

            assertEquals(1L, restaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(4.9), restaurantRating.getStarAvg());
        }

        @Test
        @DisplayName("리뷰 개수가 0인 상태에서 수정할 경우 예외가 발생한다")
        void emptyReviewsCount() {
            var restaurantRating = new RestaurantRating();
            assertThrows(IllegalStateException.class, () ->
                    restaurantRating.updateReview(review(1.0), review(4.9)));
        }
    }

    @Nested
    class Delete {

        @Test
        @DisplayName("기존 리뷰 [4.5, 3.2, 5.0] 에서 리뷰 3.2 를 삭제하면 평균 4.7, 리뷰 개수 2가 된다")
        void delete() {
            var restaurantRating = new RestaurantRating();
            var review1 = review(4.5);
            var review2 = review(3.2);
            var review3 = review(5.0);

            restaurantRating = restaurantRating.addReview(review1);
            restaurantRating = restaurantRating.addReview(review2);
            restaurantRating = restaurantRating.addReview(review3);

            restaurantRating = restaurantRating.removeReview(review2);

            assertEquals(2L, restaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(4.7), restaurantRating.getStarAvg());
        }

        @Test
        @DisplayName("한개 남은 리뷰가 삭제될 경우 평균 별점은 0.0, 리뷰 개수 0 이 된다")
        void removeToEmpty() {
            var restaurantRating = new RestaurantRating();
            var review = review(4.9);
            restaurantRating = restaurantRating.addReview(review);

            restaurantRating = restaurantRating.removeReview(review);

            assertEquals(0L, restaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(0.0), restaurantRating.getStarAvg());
        }

        @Test
        @DisplayName("리뷰 개수가 0개에서 삭제시 예외가 발생한다")
        void emptyReviewsCount() {
            var restaurantRating = new RestaurantRating();
            assertThrows(IllegalStateException.class, () ->
                    restaurantRating.removeReview(review(4.9)));
        }
    }

    private Review review(double value) {
        Star star = new Star(BigDecimal.valueOf(value));
        return new Review(star);
    }
}
