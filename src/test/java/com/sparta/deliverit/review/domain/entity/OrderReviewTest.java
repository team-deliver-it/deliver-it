package com.sparta.deliverit.review.domain.entity;

import com.deliverit.order.domain.entity.Order;
import com.deliverit.review.domain.entity.OrderReview;
import com.deliverit.review.domain.vo.Review;
import com.deliverit.review.domain.vo.Star;
import com.deliverit.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderReviewTest {

    @Test
    @DisplayName("주문 리뷰를 생성할 수 있다")
    void createOrderReview() {
        Star star = new Star(BigDecimal.valueOf(4.5));
        var review = new Review(star, "리뷰 내용");
        OrderReview orderReview = orderReview(review);

        assertNotNull(orderReview);
    }

    @Test
    @DisplayName("리뷰가 Null 이라면 예외가 발생한다")
    void failWhenReviewIsNull() {
        assertThrows(IllegalArgumentException.class, () -> orderReview(null));
    }

    @Test
    @DisplayName("유저가 Null 이라면 예외가 발생한다")
    void failWhenUserIsNull() {
        Review review = new Review(new Star(BigDecimal.valueOf(1.0)));
        assertThrows(IllegalArgumentException.class, () -> orderReview(review, null));
    }

    @Test
    @DisplayName("주문이 Null 이라면 예외가 발생한다")
    void failWhenOrderIsNull() {
        Review review = new Review(new Star(BigDecimal.valueOf(1.0)));
        User user = new User();

        assertThrows(IllegalArgumentException.class, () -> orderReview(review, user, null));
    }

    @Test
    @DisplayName("주문의 리뷰는 변경할 수 있다")
    void changeReview() {
        Star star1 = new Star(BigDecimal.valueOf(4.5));
        var oldReview = new Review(star1, "예전 리뷰");
        OrderReview orderReview = orderReview(oldReview);

        Star star2 = new Star(BigDecimal.valueOf(1.0));
        var newReview = new Review(star2, "새로운 리뷰");
        orderReview.changeReview(newReview);

        assertEquals(newReview, orderReview.getReview());
        assertEquals(newReview.getStar(), orderReview.getStar());
        assertEquals(newReview.getDescription(), orderReview.getDescription());
    }

    private OrderReview orderReview(Review review) {
        return new OrderReview(review, new User(), Order.builder().build());
    }

    private OrderReview orderReview(Review review, User user) {
        return new OrderReview(review, user, Order.builder().build());
    }

    private OrderReview orderReview(Review review, User user, Order order) {
        return new OrderReview(review, user, order);
    }
}
