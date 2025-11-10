package com.deliverit.order.domain.entity;

import com.deliverit.global.entity.BaseEntity;
import com.deliverit.menu.domain.entity.Menu;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "p_order_item")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id")
    private String orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_menu"))
    private Menu menu;

    @Column(name = "menu_name_snapshot", nullable = false)
    private String menuNameSnapshot;

    @Column(name = "menu_price_snapshot", nullable = false)
    private BigDecimal menuPriceSnapshot;

    @Column(nullable = false)
    private int quantity;

    protected OrderItem() {

    }

    @Builder
    private OrderItem(String orderItemId, Order order, Menu menu, String menuNameSnapshot, BigDecimal menuPriceSnapshot, int quantity) {
        this.orderItemId = orderItemId;
        this.order = order;
        this.menu = menu;
        this.menuNameSnapshot = menuNameSnapshot;
        this.menuPriceSnapshot = menuPriceSnapshot;
        this.quantity = quantity;
    }

    public static OrderItem create(String orderItemId, Order order, Menu menu, String menuNameSnapshot, BigDecimal menuPriceSnapshot, int quantiy) {
        return OrderItem.builder()
                .orderItemId(orderItemId)
                .order(order)
                .menu(menu)
                .menuNameSnapshot(menuNameSnapshot)
                .menuPriceSnapshot(menuPriceSnapshot)
                .quantity(quantiy)
                .build();
    }

    public static OrderItem create(Order order, Menu menu, String menuNameSnapshot, BigDecimal menuPriceSnapshot, int quantiy) {
        return OrderItem.builder()
                .order(order)
                .menu(menu)
                .menuNameSnapshot(menuNameSnapshot)
                .menuPriceSnapshot(menuPriceSnapshot)
                .quantity(quantiy)
                .build();
    }
}
