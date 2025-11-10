package com.sparta.deliverit.review.domain.vo;

import com.deliverit.review.domain.vo.Review;
import com.deliverit.review.domain.vo.Star;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    @DisplayName("리뷰를 생성할 수 있다")
    void createReview() {
        Star star = new Star(BigDecimal.valueOf(4.5));
        var review = new Review(star, "리뷰 내용");

        assertNotNull(review);
        assertEquals(star.getValue(), review.getStar());
    }

    @Test
    @DisplayName("별점이 Null 이라면 예외가 발생한다")
    void failWhenStarIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Review(null));
    }

    @Test
    @DisplayName("리뷰 내용이 없더라도 생성할 수 있다")
    void createReviewWithoutDescription() {
        Star star = new Star(BigDecimal.valueOf(4.5));
        Review review = new Review(star);

        assertNotNull(review);
        assertNull(review.getDescription());
    }

    @Test
    @DisplayName("리뷰의 별점과 설명이 같으면 같은 리뷰이다")
    void whenStarAndDescriptionAreSameThenEquals() {
        Star star = new Star(BigDecimal.valueOf(4.5));
        var review1 = new Review(star, "리뷰 생성");
        var review2 = new Review(star, "리뷰 생성");

        assertEquals(review1, review2);
    }

    @Test
    @DisplayName("리뷰의 별점과 설명이 다르면 다른 리뷰이다")
    void whenStarAndDescriptionAreDifferentThenNotEquals() {
        Star star1 = new Star(BigDecimal.valueOf(4.5));
        Star star2 = new Star(BigDecimal.valueOf(1.0));

        var review1 = new Review(star1, "리뷰1");
        var review2 = new Review(star2, "리뷰1");
        var review3 = new Review(star1, "리뷰2");

        assertNotEquals(review1, review2);
        assertNotEquals(review1, review3);
    }
}
