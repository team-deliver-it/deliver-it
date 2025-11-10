package com.deliverit.review.domain.vo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Embedded
    private Star star;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT", nullable = true)
    @Comment("리뷰 내용")
    private String description;

    public Review(Star star) {
        if (star == null) {
            throw new IllegalArgumentException("별점은 필수값입니다.");
        }
        this.star = star;
    }

    public Review(Star star, String description) {
        this(star);
        this.description = description;
    }

    public BigDecimal getStar() {
        return star.getValue();
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof Review review)) return false;
        return star.equals(review.star) && Objects.equals(description, review.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(star, description);
    }
}
