package com.sentries.SentinelX.app_server.controller;

import com.sentries.SentinelX.app_server.dto.OrderRequest;
import com.sentries.SentinelX.app_server.dto.OrderResponse;
import com.sentries.SentinelX.app_server.service.OrderService;
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
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        log.info("GET request to retrieve order with number: {}", orderNumber);
        try {
            OrderResponse order = orderService.getOrderByNumber(orderNumber);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Order not found with number: {}", orderNumber);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET - Retrieve orders by customer email
     */
    @GetMapping("/customer/{customerEmail}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerEmail(@PathVariable String customerEmail) {
        log.info("GET request to retrieve orders for customer email: {}", customerEmail);
        List<OrderResponse> orders = orderService.getOrdersByCustomerEmail(customerEmail);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * GET - Retrieve orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
        log.info("GET request to retrieve orders with status: {}", status);
        try {
            List<OrderResponse> orders = orderService.getOrdersByStatus(status);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Invalid order status: {}", status);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST - Create a new order
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        log.info("POST request to create order with number: {}", orderRequest.getOrderNumber());
        try {
            OrderResponse createdOrder = orderService.createOrder(orderRequest);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error creating order: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT - Update an existing order
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        log.info("PUT request to update order with id: {}", id);
        try {
            OrderResponse updatedOrder = orderService.updateOrder(id, orderRequest);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error updating order with id {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
            log.error("Error deleting order with id {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
