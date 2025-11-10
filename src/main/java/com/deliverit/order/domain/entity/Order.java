package com.deliverit.order.domain.entity;

import com.deliverit.global.entity.BaseEntity;
import com.deliverit.payment.domain.entity.Payment;
import com.deliverit.restaurant.domain.entity.Restaurant;
import com.deliverit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_order_restaurant"))
    private Restaurant restaurant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "ordered_at", nullable = false, updatable = false)
    LocalDateTime orderedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private String address;

    @Column(name = "total_price", nullable = false, updatable = false)
    private BigDecimal totalPrice;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected Order() {

    }

    @Builder
    private Order(String orderId, User user, Restaurant restaurant, LocalDateTime orderedAt, OrderStatus orderStatus, String address, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.user = user;
        this.restaurant = restaurant;
        this.orderedAt = orderedAt;
        this.orderStatus = orderStatus;
        this.address = address;
        this.totalPrice = totalPrice;
    }

    public static Order create(User user, Restaurant restaurant, LocalDateTime orderedAt, OrderStatus orderStatus, String address, BigDecimal totalPrice) {
        return Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderedAt(orderedAt)
                .orderStatus(orderStatus)
                .address(address)
                .totalPrice(totalPrice)
                .build();
    }
}
