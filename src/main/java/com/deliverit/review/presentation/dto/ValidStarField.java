package com.deliverit.review.presentation.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotNull(message = "star 는 필수값입니다.")
@DecimalMin(value = "1.0", message = "별점은 최소 1.0 이상이어야 합니다.")
@DecimalMax(value = "5.0", message = "별점은 최대 5.0 이하이어야 합니다.")
public @interface ValidStarField {
    String message() default "유효하지 않은 별점 값입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
