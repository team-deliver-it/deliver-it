package com.deliverit.payment.application.service;

import com.deliverit.order.domain.entity.Order;
import com.deliverit.payment.domain.entity.Payment;
import com.deliverit.payment.application.service.dto.PaymentRequestDto;

public interface PaymentService {

    Payment delegateRequest(PaymentRequestDto requestDto);

    Payment paymentCancel(Order order);

    Payment getPayment(String paymentId);

}
