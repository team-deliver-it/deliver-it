package com.deliverit.order.infrastructure;

import com.deliverit.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    @Query(
            """
                SELECT oi    
                FROM OrderItem oi
                WHERE oi.order.orderId IN :orderIds
            """)
    List<OrderItem> findAllByOrderIn(@Param("orderIds") Collection<String> orderIds);

    @Query(
            """
                SELECT oi    
                FROM OrderItem oi
                WHERE oi.order.orderId = :orderId
            """)
    List<OrderItem> findAllByOrder(@Param("orderId") String orderId);
}
