package com.deliverit.payment.application.service.dto;

import com.deliverit.payment.domain.entity.Payment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponseDto {

    private String paymentId;

    private String cardNum;

    private String cardCompany;

    private String paidAt;

    public static PaymentResponseDto of(Payment payment) {
        return new PaymentResponseDto(
                payment.getPaymentId(),
                mask(payment.getCardNum()),
                payment.getCompany().getName(),
                payment.getPaidAt().toString()
        );
    }

    public static PaymentResponseDto fail(String paymentId, String cardNum, String cardCompany, ZonedDateTime paidAt) {
        return new PaymentResponseDto(
                paymentId,
                mask(cardNum),
                cardCompany,
                paidAt.toString()
        );
    }

    public static PaymentResponseDto cancel(String paymentId, String cardNum, String cardCompany, ZonedDateTime paidAt) {
        return new PaymentResponseDto(
                paymentId,
                mask(cardNum),
                cardCompany,
                paidAt.toString()
        );
    }

    private static String mask(String cardNum) {
        if (cardNum == null || cardNum.length() < 4) return "****";
        String last4 = cardNum.substring(cardNum.length() - 4);
        return "****-****-****-" + last4;
    }


}
