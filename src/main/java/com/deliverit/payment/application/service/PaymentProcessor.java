package com.deliverit.payment.application.service;

import com.deliverit.payment.domain.entity.Payment;
import com.deliverit.payment.enums.Company;
import com.deliverit.payment.application.service.dto.PaymentRequestDto;

public interface PaymentProcessor {

    Payment paymentProcessing(PaymentRequestDto requestDto);

    boolean findByCompany(Company company);
}
