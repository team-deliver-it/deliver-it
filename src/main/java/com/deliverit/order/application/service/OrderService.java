package com.deliverit.order.application.service;

import com.deliverit.order.application.dto.CreateOrderCommand;
import com.deliverit.order.domain.entity.Order;
import com.deliverit.order.presentation.dto.response.CancelOrderInfo;
import com.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.deliverit.order.presentation.dto.response.OrderInfo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface OrderService {

    OrderInfo getOrderDetailForUser(String orderId, String userId);

    OrderInfo getOrderDetailForOwner(String orderId, String userId);

    Page<OrderInfo> getOrderListForUser(String userId, LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);

    Page<OrderInfo> getOrderListForOwner(String userId, String restaurantId ,LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);

    ConfirmOrderInfo confirmOrder(String restaurantId, String orderId, String accessUserId);

    CancelOrderInfo cancelOrderForUser(String orderId, String userId);

    CancelOrderInfo cancelOrderForOwner(String restaurantId, String orderId, String accessUserId);

    CreateOrderInfo createOrder(CreateOrderCommand orderCommand, Long userId);

    Order createOrderForPayment(CreateOrderCommand orderCommand, Long userId);

    int completeIfValid(String orderId, String paymentId, Long orderVersion);

    int failIfValid(String orderId, Long version);

    Order loadFresh(String orderId);

//    int cancelOrderOne(String orderId, Long version);
}