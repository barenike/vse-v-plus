package com.example.vse_back.controller;

import com.example.vse_back.exceptions.OrderDetailIsNotFoundException;
import com.example.vse_back.model.entity.OrderDetailEntity;
import com.example.vse_back.model.service.OrderDetailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    // Security vulnerability - user can see the details of other's orders
    @Operation(summary = "Get the order details")
    @GetMapping("/user/order_details/{orderId}")
    public ResponseEntity<List<OrderDetailEntity>> getMyOrderDetailsByOrderId(@PathVariable(name = "orderId") UUID orderId) {
        final List<OrderDetailEntity> orders = orderDetailService.getOrderDetailsByOrderId(orderId);
        return orders != null && !orders.isEmpty()
                ? new ResponseEntity<>(orders, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get all order details of all users' orders")
    @GetMapping("/admin/order_details")
    public ResponseEntity<List<OrderDetailEntity>> getOrderDetails() {
        List<OrderDetailEntity> orderDetails = orderDetailService.getAllOrderDetails();
        return orderDetails != null && !orderDetails.isEmpty()
                ? new ResponseEntity<>(orderDetails, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get the order detail")
    @GetMapping("/admin/order_details/{orderDetailId}")
    public ResponseEntity<?> getOrderDetailById(@PathVariable(name = "orderDetailId") UUID orderDetailId) {
        List<OrderDetailEntity> orderDetails = orderDetailService.getAllOrderDetailsById(orderDetailId);
        if (orderDetails.isEmpty()) {
            throw new OrderDetailIsNotFoundException(orderDetailId);
        }
        return new ResponseEntity<>(orderDetails, HttpStatus.OK);
    }
}
