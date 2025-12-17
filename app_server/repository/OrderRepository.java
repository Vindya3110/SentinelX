package com.sentries.SentinelX.app_server.repository;

import com.sentries.SentinelX.app_server.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerEmail(String customerEmail);
    List<Order> findByStatus(Order.OrderStatus status);
}
