package com.shopvista.repository;

import com.shopvista.entity.Order;
import com.shopvista.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    @Query("SELECT o FROM Order o WHERE o.customerEmail = ?1")
    List<Order> findByCustomerEmail(String customerEmail);
    
    @Query("SELECT o FROM Order o WHERE o.status = ?1")
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customerName LIKE %?1%")
    List<Order> searchByCustomerName(String customerName);
}
