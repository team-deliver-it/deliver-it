package com.sparta.deliverit.review.domain.vo;

import com.deliverit.review.domain.vo.Star;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StarTest {

    @Test
    @DisplayName("별점이 Null 이라면 예외가 발생한다")
    void failWhenStarIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Star(null));
    }

    @Test
    @DisplayName("별점은 1.0 이상이어야 한다")
    void failWhenStarIsLessThenOne() {
        assertThrows(IllegalArgumentException.class, () ->
                new Star(BigDecimal.valueOf(0.9)));
    }

    @Test
    @DisplayName("별점은 5.0 이하이어야 한다")
    void failWhenStarIsGreaterThenFive() {
        assertThrows(IllegalArgumentException.class, () ->
                new Star(BigDecimal.valueOf(5.1)));
    }

    @Test
    @DisplayName("별점은 소수점 1자리까지만 보장한다")
    void starIsTruncatedToOneDecimalPlace() {
        Star star = new Star(BigDecimal.valueOf(4.5555555555555));

        assertEquals(BigDecimal.valueOf(4.5), star.getValue());
    }

    @Test
    @DisplayName("별점은 값이 같다면 같은 별점이다")
    void sameValue() {
        double sameValue = 4.5;
        Star star1 = new Star(BigDecimal.valueOf(sameValue));
        Star star2 = new Star(BigDecimal.valueOf(sameValue));

        assertEquals(star1, star2);
    }

    @Test
    @DisplayName("별점은 값이 다르다면 다른 별점이다")
    void differentValue() {
        Star star1 = new Star(BigDecimal.valueOf(4.4));
        Star star2 = new Star(BigDecimal.valueOf(4.5));

        assertNotEquals(star1, star2);
    }
}
