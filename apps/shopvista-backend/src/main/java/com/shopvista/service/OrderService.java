package com.shopvista.service;

import com.shopvista.dto.OrderRequest;
import com.shopvista.dto.OrderResponse;
import com.shopvista.entity.Order;
import com.shopvista.entity.OrderStatus;
import com.shopvista.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating new order with order number: {}", request.getOrderNumber());
        
        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setShippingAddress(request.getShippingAddress());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.valueOf(request.getStatus()));
        order.setPaymentMethod(request.getPaymentMethod());

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        log.info("Fetching order with order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
    }

    public List<OrderResponse> getOrdersByCustomerEmail(String email) {
        log.info("Fetching orders for customer email: {}", email);
        return orderRepository.findByCustomerEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByStatus(String status) {
        log.info("Fetching orders by status: {}", status);
        return orderRepository.findByStatus(OrderStatus.valueOf(status)).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> searchOrdersByCustomerName(String name) {
        log.info("Searching orders by customer name: {}", name);
        return orderRepository.searchByCustomerName(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrder(Long id, OrderRequest request) {
        log.info("Updating order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setShippingAddress(request.getShippingAddress());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.valueOf(request.getStatus()));
        order.setPaymentMethod(request.getPaymentMethod());

        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully with ID: {}", id);
        return mapToResponse(updatedOrder);
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        log.info("Updating order status for order ID: {} to: {}", id, status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        order.setStatus(OrderStatus.valueOf(status));
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for ID: {}", id);
        return mapToResponse(updatedOrder);
    }

    public OrderResponse updateTrackingNumber(Long id, String trackingNumber) {
        log.info("Updating tracking number for order ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        order.setTrackingNumber(trackingNumber);
        Order updatedOrder = orderRepository.save(order);
        log.info("Tracking number updated successfully for order ID: {}", id);
        return mapToResponse(updatedOrder);
    }

    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getShippingAddress(),
                order.getTotalAmount(),
                order.getStatus().toString(),
                order.getPaymentMethod(),
                order.getTrackingNumber(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
