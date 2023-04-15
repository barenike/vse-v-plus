package com.example.vse_back.controller;

import com.example.vse_back.infrastructure.order.OrderCreationRequest;
import com.example.vse_back.model.entity.OrderEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.OrderService;
import com.example.vse_back.model.service.utils.LocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {
    private final OrderService orderService;
    private final LocalUtil localUtil;

    public OrderController(OrderService orderService, LocalUtil localUtil) {
        this.orderService = orderService;
        this.localUtil = localUtil;
    }

    @Operation(summary = "Create the new order")
    @PostMapping("/user/orders")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderCreationRequest orderCreationRequest,
                                         @RequestHeader(name = "Authorization") String token) {
        UserEntity user = localUtil.getUserFromToken(token);
        orderService.createOrder(orderCreationRequest, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get user's orders (id is extracted from JWT)")
    @GetMapping("/user/orders")
    public ResponseEntity<?> getMyOrders(@RequestHeader(name = "Authorization") String token) {
        UserEntity user = localUtil.getUserFromToken(token);
        final List<OrderEntity> orders = orderService.getOrdersByUserId(user.getId());
        return orders != null && !orders.isEmpty()
                ? new ResponseEntity<>(orders, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete the user's order")
    @DeleteMapping("/user/orders/{orderId}")
    public ResponseEntity<?> deleteMyOrder(@PathVariable(name = "orderId") UUID orderID) {
        boolean isDeleted = orderService.deleteOrderById(orderID);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @Operation(summary = "no parameters - get all orders from all users; " +
            "userId parameter - get all orders from the user; " +
            "orderId parameter - delete the order; " +
            "orderId and status parameters - change the order's status")
    @GetMapping("/admin/orders")
    public ResponseEntity<?> manipulateOrders(@RequestParam(value = "userId", required = false) UUID userId,
                                              @RequestParam(value = "orderId", required = false) UUID orderId,
                                              @RequestParam(value = "status", required = false) String status) {
        if (userId == null && orderId == null) {
            final List<OrderEntity> orders = orderService.getAllOrders();
            return getListResponseEntity(orders);
        } else if (userId != null) {
            final List<OrderEntity> orders = orderService.getOrdersByUserId(userId);
            return getListResponseEntity(orders);
        } else if (status != null) {
            final boolean isChanged = orderService.changeOrderStatus(orderId, status);
            return isChanged
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } else {
            final boolean isDeleted = orderService.deleteOrderById(orderId);
            return isDeleted
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    private ResponseEntity<List<OrderEntity>> getListResponseEntity(List<OrderEntity> orders) {
        return orders != null && !orders.isEmpty()
                ? new ResponseEntity<>(orders, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }
}
