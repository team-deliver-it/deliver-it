package com.deliverit.payment.application.service.card;

import com.deliverit.payment.application.service.PaymentProcessor;
import com.deliverit.payment.domain.entity.Payment;
import com.deliverit.payment.enums.Company;
import com.deliverit.payment.application.service.dto.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KbCardProcessor implements PaymentProcessor {

    @Override
    public Payment paymentProcessing(PaymentRequestDto requestDto) {
        return Payment.of(requestDto, Company.KB);
    }

    @Override
    public boolean findByCompany(Company company) {
        return company == Company.KB;
    }
}
