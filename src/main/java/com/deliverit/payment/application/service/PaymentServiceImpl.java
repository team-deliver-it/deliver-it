package com.deliverit.payment.application.service;

import com.deliverit.global.exception.OrderException;
import com.deliverit.global.exception.PaymentException;
import com.deliverit.global.response.code.OrderResponseCode;
import com.deliverit.global.response.code.PaymentResponseCode;
import com.deliverit.order.domain.entity.Order;
import com.deliverit.order.infrastructure.OrderRepository;
import com.deliverit.payment.application.service.dto.PaymentRequestDto;
import com.deliverit.payment.domain.entity.Payment;
import com.deliverit.payment.domain.repository.PaymentRepository;
import com.deliverit.payment.enums.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final List<PaymentProcessor> processorList;

    private final String INVALID_CARD_NUMBER = "9999-9999-9999-9999";
    private final String LIMIT_OVER_CARD_NUMBER = "8888-8888-8888-8888";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment delegateRequest(PaymentRequestDto requestDto) {
        isErrorCard(requestDto.getCardNum());

        PaymentProcessor processor = getProcessor(Company.of(requestDto.getCompany()));
        Payment entity = processor.paymentProcessing(requestDto);

        return paymentRepository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment paymentCancel(Order order) {
        Order findOrder = orderRepository.findById(order.getOrderId()).orElseThrow(
                () -> new OrderException(OrderResponseCode.NOT_FOUND_ORDER)
        );

        Payment payment = paymentRepository.findById(findOrder.getPayment().getPaymentId()).orElseThrow();
        payment.cancel();

        return payment;
    }

    @Transactional(readOnly = true)
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentException(PaymentResponseCode.PAYMENT_NOT_FOUND));
    }

    private PaymentProcessor getProcessor(Company company) {
        return processorList.stream()
                .filter(processor -> processor.findByCompany(company))
                .findFirst()
                .orElseThrow(() -> new PaymentException(PaymentResponseCode.INVALID_COMPANY));
    }

    private void isErrorCard(String cardNum) {
        if(cardNum.equals(INVALID_CARD_NUMBER))
            throw new PaymentException(PaymentResponseCode.INVALID_CARD_NUMBER);
        else if(cardNum.equals(LIMIT_OVER_CARD_NUMBER))
            throw new PaymentException(PaymentResponseCode.CARD_LIMIT_EXCEEDED);
    }
}
