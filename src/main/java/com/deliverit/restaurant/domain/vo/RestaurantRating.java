package com.deliverit.restaurant.domain.vo;

import com.deliverit.review.domain.vo.Review;
import com.deliverit.review.domain.vo.Star;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@NoArgsConstructor
@Getter
public class RestaurantRating {
    private static final int CALCULATION_PRECISION_SCALE = Star.SCALE + 2;

    @Column(name = "star_avg", precision = 2, scale = 1, nullable = false)
    @Comment("평균 별점")
    private BigDecimal starAvg = BigDecimal.ZERO.setScale(Star.SCALE, RoundingMode.DOWN);

    @Column(name = "reviews_count", nullable = false)
    @Comment("리뷰 개수")
    private Long reviewsCount = 0L;

    public RestaurantRating(BigDecimal starAvg, Long reviewsCount) {
        this.starAvg = starAvg;
        this.reviewsCount = reviewsCount;
    }

    public RestaurantRating addReview(Review newReview) {
        BigDecimal totalRating = getTotalRating();
        long newCount = this.reviewsCount + 1;

        BigDecimal newTotalRating = totalRating.add(newReview.getStar());
        BigDecimal newRating = calculateRatingAverage(newTotalRating, newCount);

        return new RestaurantRating(newRating, newCount);
    }

    public RestaurantRating updateReview(Review oldReview, Review newReview) {
        if (reviewsCount <= 0) {
            throw new IllegalStateException("리뷰 개수가 0 입니다. 수정이 불가능합니다");
        }

        BigDecimal totalRating = getTotalRating();
        BigDecimal newTotalRating = totalRating
                .subtract(oldReview.getStar())
                .add(newReview.getStar());
        BigDecimal newRating = calculateRatingAverage(newTotalRating, reviewsCount);

        return new RestaurantRating(newRating, reviewsCount);
    }

    public RestaurantRating removeReview(Review review) {
        if (reviewsCount <= 0) {
            throw new IllegalStateException("현재 리뷰가 없으므로 삭제할 수 없습니다.");
        }
        if (reviewsCount == 1) return new RestaurantRating();

        BigDecimal totalRating = getTotalRating();
        long newCount = this.reviewsCount - 1;

        BigDecimal newTotalRating = totalRating.subtract(review.getStar());
        BigDecimal newRating = calculateRatingAverage(newTotalRating, newCount);

        return new RestaurantRating(newRating, newCount);
    }

    private BigDecimal getTotalRating() {
        return starAvg.multiply(BigDecimal.valueOf(reviewsCount));
    }

    private BigDecimal calculateRatingAverage(BigDecimal total, long count) {
        BigDecimal bigDecimalOfCount = BigDecimal.valueOf(count);
        return total
                .divide(bigDecimalOfCount, CALCULATION_PRECISION_SCALE, RoundingMode.DOWN)
                .setScale(Star.SCALE, RoundingMode.DOWN);
    }
}
