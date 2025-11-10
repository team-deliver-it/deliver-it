package com.deliverit.payment.application.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    @NotEmpty(message = "필수 선택항목입니다")
    private String payType;

    @NotEmpty(message = "필수 선택항목입니다")
    private String company;

    @NotEmpty
    @Size(min = 19, max = 19, message = "카드번호를 정확히 입력해주세요")
    @Pattern(regexp = "(\\d{4}-){3}\\d{4}|\\d{4}-\\d{6}-\\d{5}")
    private String cardNum;

    @PositiveOrZero
    private Integer totalPrice;
}
