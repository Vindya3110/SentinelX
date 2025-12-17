package com.shopvista.controller;

import com.shopvista.dto.OrderRequest;
import com.shopvista.dto.OrderResponse;
import com.shopvista.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    private final OrderService orderService;

    /**
     * POST - Create a new order
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("POST request to create new order");
        OrderResponse order = orderService.createOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * GET - Retrieve all orders
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("GET request to retrieve all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * GET - Retrieve order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("GET request to retrieve order with id: {}", id);
        try {
            OrderResponse order = orderService.getOrderById(id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Order not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET - Retrieve order by order number
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        log.info("GET request to retrieve order with order number: {}", orderNumber);
        try {
            OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Order not found with order number: {}", orderNumber);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET - Retrieve orders by customer email
     */
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerEmail(@PathVariable String email) {
        log.info("GET request to retrieve orders for customer email: {}", email);
        List<OrderResponse> orders = orderService.getOrdersByCustomerEmail(email);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * GET - Retrieve orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
        log.info("GET request to retrieve orders by status: {}", status);
        try {
            List<OrderResponse> orders = orderService.getOrdersByStatus(status);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: {}", status);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET - Search orders by customer name
     */
    @GetMapping("/search")
    public ResponseEntity<List<OrderResponse>> searchOrdersByCustomerName(@RequestParam String name) {
        log.info("GET request to search orders by customer name: {}", name);
        List<OrderResponse> orders = orderService.searchOrdersByCustomerName(name);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * PUT - Update an order
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @RequestBody OrderRequest request) {
        log.info("PUT request to update order with id: {}", id);
        try {
            OrderResponse order = orderService.updateOrder(id, request);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Order not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PATCH - Update order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("PATCH request to update order status for id: {} with status: {}", id, status);
        try {
            OrderResponse order = orderService.updateOrderStatus(id, status);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: {}", status);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Order not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PATCH - Update tracking number
     */
    @PatchMapping("/{id}/tracking")
    public ResponseEntity<OrderResponse> updateTrackingNumber(@PathVariable Long id, @RequestParam String trackingNumber) {
        log.info("PATCH request to update tracking number for order id: {}", id);
        try {
            OrderResponse order = orderService.updateTrackingNumber(id, trackingNumber);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Order not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE - Delete an order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("DELETE request to delete order with id: {}", id);
        try {
            orderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Order not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
