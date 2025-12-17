package com.sentries.SentinelX.app_server.service;

import com.sentries.SentinelX.app_server.dto.OrderRequest;
import com.sentries.SentinelX.app_server.dto.OrderResponse;
import com.sentries.SentinelX.app_server.entity.Order;
import com.sentries.SentinelX.app_server.repository.OrderRepository;
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

    /**
     * Get all orders
     */
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID
     */
    public OrderResponse getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        return orderRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    /**
     * Get order by order number
     */
    public OrderResponse getOrderByNumber(String orderNumber) {
        log.info("Fetching order with number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
    }

    /**
     * Get orders by customer email
     */
    public List<OrderResponse> getOrdersByCustomerEmail(String customerEmail) {
        log.info("Fetching orders for customer email: {}", customerEmail);
        return orderRepository.findByCustomerEmail(customerEmail)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by status
     */
    public List<OrderResponse> getOrdersByStatus(String status) {
        log.info("Fetching orders with status: {}", status);
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByStatus(orderStatus)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    /**
     * Create a new order
     */
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating new order with number: {}", orderRequest.getOrderNumber());

        if (orderRepository.findByOrderNumber(orderRequest.getOrderNumber()).isPresent()) {
            throw new RuntimeException("Order with number already exists: " + orderRequest.getOrderNumber());
        }

        Order order = new Order();
        order.setOrderNumber(orderRequest.getOrderNumber());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerEmail(orderRequest.getCustomerEmail());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setTrackingNumber(orderRequest.getTrackingNumber());

        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(orderRequest.getStatus().toUpperCase());
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            order.setStatus(Order.OrderStatus.PENDING);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());

        return convertToResponse(savedOrder);
    }

    /**
     * Update an existing order
     */
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        log.info("Updating order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (!order.getOrderNumber().equals(orderRequest.getOrderNumber())
                && orderRepository.findByOrderNumber(orderRequest.getOrderNumber()).isPresent()) {
            throw new RuntimeException("Order with number already exists: " + orderRequest.getOrderNumber());
        }

        order.setOrderNumber(orderRequest.getOrderNumber());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerEmail(orderRequest.getCustomerEmail());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setTrackingNumber(orderRequest.getTrackingNumber());

        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(orderRequest.getStatus().toUpperCase());
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + orderRequest.getStatus());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully with id: {}", updatedOrder.getId());

        return convertToResponse(updatedOrder);
    }

    /**
     * Delete an order
     */
    public void deleteOrder(Long id) {
        log.info("Deleting order with id: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted successfully with id: {}", id);
    }

    /**
     * Convert Order entity to OrderResponse DTO
     */
    private OrderResponse convertToResponse(Order order) {
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
