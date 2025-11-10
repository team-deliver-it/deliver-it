package com.deliverit.payment.domain.entity;

import com.deliverit.global.entity.BaseEntity;
import com.deliverit.payment.enums.Company;
import com.deliverit.payment.enums.PayState;
import com.deliverit.payment.enums.PayType;
import com.deliverit.payment.application.service.dto.PaymentRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE p_payment SET deleted_at = CURRENT_TIMESTAMP WHERE payment_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @NotEmpty
    @Column(name = "card_num")
    private String cardNum;

    @NotEmpty
    @Enumerated(EnumType.STRING)
    @Column(name = "card_company")
    private Company company;

    @NotEmpty
    @PositiveOrZero
    private Integer price;

    @NotEmpty
    @Enumerated(EnumType.STRING)
    private PayType payType;

    @NotEmpty
    @Enumerated(EnumType.STRING)
    private PayState payState;

    @Builder.Default
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "paid_at")
    private ZonedDateTime paidAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    public static Payment of(PaymentRequestDto requestDto, Company company) {
        return Payment.builder()
                .paymentId(UUID.randomUUID().toString().substring(0, 12))
                .cardNum(requestDto.getCardNum())
                .company(company)
                .price(requestDto.getTotalPrice())
                .payType(PayType.of(requestDto.getPayType()))
                .payState(PayState.COMPLETED)
                .build();
    }

    public Payment cancel() {
        this.payState = PayState.CANCELED;
        return this;
    }

}
