package com.deliverit.order.infrastructure;

import com.deliverit.order.domain.entity.Order;
import com.deliverit.order.domain.entity.OrderStatus;
import com.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.deliverit.order.infrastructure.dto.OrderDetailForUser;
import com.deliverit.payment.enums.PayState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(
    """
        SELECT 
            u.id AS userId,
            u.username AS userName,    
            r.restaurantId AS restaurantId,
            r.name AS restaurantName,
            o.orderId AS orderId,
            o.orderedAt AS orderedAt,
            o.orderStatus AS orderStatus,
            o.address AS address,
            o.totalPrice AS totalPrice,
            o.version AS version,
            p.paymentId AS paymentId
        FROM Order o
        JOIN o.user u 
        JOIN o.restaurant r
        LEFT JOIN o.payment p 
        WHERE o.orderId =:orderId
    """)
    Optional<OrderDetailForUser> getByOrderIdForUser(@Param("orderId") String orderId);

    @Query(
    """
       SELECT 
            u.id AS userId,
            u.username AS userName,    
            r.restaurantId AS restaurantId,
            r.name AS restaurantName,
            r.user.id AS restaurantUserId,
            o.orderId AS orderId,
            o.orderedAt AS orderedAt,
            o.orderStatus AS orderStatus,
            o.address AS address,
            o.totalPrice AS totalPrice,
            o.version AS version,
            p.paymentId AS paymentId
        FROM Order o
        JOIN o.user u 
        JOIN o.restaurant r
        LEFT JOIN o.payment p
        WHERE o.orderId =:orderId 
    """)
    Optional<OrderDetailForOwner> getByOrderIdForOwner(@Param("orderId") String orderId);

    @Query(
    """
        SELECT 
            u.id AS userId,
            u.username AS userName,    
            r.restaurantId AS restaurantId,
            r.name AS restaurantName,
            o.orderId AS orderId,
            o.orderedAt AS orderedAt,
            o.orderStatus AS orderStatus,
            o.address AS address,
            o.totalPrice AS totalPrice
        FROM Order o
        JOIN o.user u 
        JOIN o.restaurant r
        WHERE o.user.id = :userId
            and o.orderedAt >= :from
            and o.orderedAt < :to    
        ORDER BY o.orderedAt DESC, o.orderId desc
    """)
    Page<OrderDetailForUser> findOrdersByUserIdForUser(
            @Param("userId") String userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Query(
    """
        SELECT 
            u.id AS userId,
            u.username AS userName,    
            r.restaurantId AS restaurantId,
            r.name AS restaurantName,
            r.user.id AS restaurantUserId,
            o.orderId AS orderId,
            o.orderedAt AS orderedAt,
            o.orderStatus AS orderStatus,
            o.address AS address,
            o.totalPrice AS totalPrice,
            o.version AS version
        FROM Order o
        JOIN o.user u 
        JOIN o.restaurant r
        WHERE r.restaurantId = :restaurantId
            and o.orderedAt >= :from
            and o.orderedAt < :to    
        ORDER BY o.orderedAt DESC, o.orderId desc
    """)
    Page<OrderDetailForOwner> findOrdersByRestaurantIdForOwner(
            @Param("restaurantId") String restaurantId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
                UPDATE Order o
                SET o.orderStatus = :nextStatus,
                    o.version = o.version + 1
                WHERE o.orderId = :orderId
                    AND o.orderStatus = :currStatus
                    AND o.restaurant.restaurantId = :restaurantId
                    AND o.restaurant.user.id = :accessUserId
                    AND o.version = :version
                    AND o.orderedAt > :nowMinusMinute
            """)
    int updateOrderStatusToConfirm(@Param("orderId") String orderId, @Param("restaurantId") String restaurantId, @Param("accessUserId") Long accessUserId, @Param("currStatus") OrderStatus currStatus, @Param("nextStatus") OrderStatus nextStatus, @Param("version") Long version, @Param("nowMinusMinute") LocalDateTime nowMinusMinute);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE Order o
    SET o.orderStatus = :nextStatus,
        o.version = o.version + 1
    WHERE o.orderId = :orderId
      AND o.orderStatus = :currStatus
      AND o.version = :version
      AND o.orderedAt > :nowMinusMinute
      AND EXISTS (               
            SELECT 1 FROM User u
            WHERE u.id = :accessUserId
              AND u.id = o.user.id
      )
      AND EXISTS (               
            SELECT 1 FROM Payment p
            WHERE p.paymentId = o.payment.paymentId
              AND p.payState = :canceled
      )
    """)
    int updateOrderStatusToCancelForUser(@Param("orderId") String orderId,
                                         @Param("accessUserId") Long accessUserId,
                                         @Param("currStatus") OrderStatus currStatus,
                                         @Param("nextStatus") OrderStatus nextStatus,
                                         @Param("version") Long version,
                                         @Param("nowMinusMinute") LocalDateTime nowMinusMinute,
                                         @Param("canceled") PayState canceled);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE Order o
    SET o.orderStatus = :nextStatus,
        o.version = o.version + 1
    WHERE o.orderId = :orderId
      AND o.orderStatus IN :currStatusList
      AND o.orderStatus <> :nextStatus
      AND o.version = :version
      AND EXISTS (               
            SELECT 1 FROM Restaurant r
            WHERE r.restaurantId = o.restaurant.restaurantId
              AND r.restaurantId = :restaurantId
      )
      AND EXISTS (               
            SELECT 1 FROM User u
            WHERE u.id = :accessUserId
              AND u.id IN (
                    SELECT r2.user.id FROM Restaurant r2
                    WHERE r2.restaurantId = o.restaurant.restaurantId
              )
      )
      AND EXISTS (               
            SELECT 1 FROM Payment p
            WHERE p.paymentId = o.payment.paymentId
              AND p.payState = :canceled
      )
    """)
    int updateOrderStatusToCancelForOwner(@Param("orderId") String orderId,
                                          @Param("restaurantId") String restaurantId,
                                          @Param("accessUserId") Long accessUserId,
                                          @Param("currStatusList") List<OrderStatus> currStatusList,
                                          @Param("nextStatus") OrderStatus nextStatus,
                                          @Param("version") Long version,
                                          @Param("canceled") PayState canceled);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value =
    """
        UPDATE p_order o
        JOIN p_restaurant r ON r.restaurant_id = o.restaurant_id
        SET
          o.payment_id = :paymentId,
          o.order_status = :nextOrderStatus,
          o.version = o.version + 1,
          o.ordered_at = :updateTime,
          o.updated_at = :updateTime
        WHERE o.order_id = :orderId
          AND o.order_status = :currOrderStatus
          AND o.version = :version
          AND r.status = :expectedRestaurantStatus
          AND NOT EXISTS (
              SELECT 1
              FROM p_order_item oi
              JOIN p_menu m ON m.menu_id = oi.menu_id
              WHERE oi.order_id = o.order_id
                AND m.status <> :expectedMenuStatus
        )
    """, nativeQuery = true)
    int completeIfValid(
            @Param("orderId") String orderId,
            @Param("paymentId") String paymentId,
            @Param("version") long expectedVersion,
            @Param("currOrderStatus") String currStatus,
            @Param("nextOrderStatus") String nextStatus,
            @Param("expectedRestaurantStatus") String restaurantStatus,
            @Param("expectedMenuStatus") String menuStatus,
            @Param("updateTime") LocalDateTime updateTime
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value =
    """
        UPDATE p_order o
        JOIN p_restaurant r ON r.restaurant_id = o.restaurant_id
        SET 
          o.order_status = :nextStatus,
          o.version = o.version + 1,
          o.updated_at = :updateTime                      
        WHERE o.order_id = :orderId
          AND o.order_status = :currStatus      
          AND o.order_status <> :nextStatus     
          AND o.version = :version              
    """, nativeQuery = true)
    int updateOrderStatusToFail(@Param("orderId") String orderId,
                                @Param("version") Long version,
                                @Param("currStatus") String currStatus,
                                @Param("nextStatus") String nextStatus,
                                @Param("updateTime") LocalDateTime updateTime
    );
}
