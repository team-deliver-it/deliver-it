package com.deliverit.review.domain.entity;

import com.deliverit.order.domain.entity.Order;
import com.deliverit.review.domain.vo.Review;
import com.deliverit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "p_order_review",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_order_review_order_user",
                columnNames = {"order_id", "user_id", "is_active"}
        )
)
@Where(clause = "is_active = 1")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderReview{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_review_id")
    private Long orderReviewId;

    @Embedded
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_review_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_review_order"))
    private Order order;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public OrderReview(Review review, User user, Order order) {
        validateReview(review);
        validateUser(user);
        validateOrder(order);
        this.review = review;
        this.user = user;
        this.order = order;
    }

    public void changeReview(Review newReview) {
        validateReview(newReview);
        this.review = newReview;
    }

    public BigDecimal getStar() {
        return review.getStar();
    }

    public String getDescription() {
        return review.getDescription();
    }

    public void deactivate() {
        active = false;
    }

    private void validateReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("리뷰는 필수입니다.");
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("유저는 필수입니다.");
        }
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("주문은 필수입니다.");
        }
    }
}
