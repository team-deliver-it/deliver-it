package com.deliverit.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Star {
    private static final BigDecimal MIN_VALUE = BigDecimal.valueOf(1.0);
    private static final BigDecimal MAX_VALUE = BigDecimal.valueOf(5.0);
    public static final int SCALE = 1;

    @Column(name = "star", precision = 2, scale = 1, nullable = false)
    @Comment("별점")
    private BigDecimal value;

    public Star(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("별점은 필수값입니다.");
        }

        validateRange(value);
        this.value = normalization(value);
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof Star star)) return false;
        return Objects.equals(value, star.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private void validateRange(BigDecimal value) {
        if (value.compareTo(MIN_VALUE) < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw new IllegalArgumentException("별점은 1.0 이상 5.0 이하이어야 합니다.");
        }
    }

    private BigDecimal normalization(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.DOWN);
    }
}
