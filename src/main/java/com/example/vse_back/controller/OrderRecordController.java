package com.example.vse_back.controller;

import com.example.vse_back.exceptions.OrderRecordIsNotFoundException;
import com.example.vse_back.model.entity.OrderRecordEntity;
import com.example.vse_back.model.service.OrderRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderRecordController {
    private final OrderRecordService orderRecordService;

    public OrderRecordController(OrderRecordService orderRecordService) {
        this.orderRecordService = orderRecordService;
    }

    @GetMapping("/user/order_records/{orderId}")
    public ResponseEntity<List<OrderRecordEntity>> getMyOrderRecordsByOrderId(@PathVariable(name = "orderId") UUID orderId) {
        final List<OrderRecordEntity> orders = orderRecordService.findOrderRecordsByOrderId(orderId);
        return orders != null && !orders.isEmpty()
                ? new ResponseEntity<>(orders, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/admin/order_records")
    public ResponseEntity<List<OrderRecordEntity>> getOrderRecords() {
        List<OrderRecordEntity> orderRecords = orderRecordService.findAllOrderRecords();
        return orderRecords != null && !orderRecords.isEmpty()
                ? new ResponseEntity<>(orderRecords, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin/order_records/{orderRecordId}")
    public ResponseEntity<?> getOrderRecordById(@PathVariable(name = "orderRecordId") UUID orderRecordId) {
        List<OrderRecordEntity> orderRecords = orderRecordService.findAllOrderRecordsById(orderRecordId);
        if (orderRecords.isEmpty()) {
            throw new OrderRecordIsNotFoundException(orderRecordId);
        }
        return new ResponseEntity<>(orderRecords, HttpStatus.OK);
    }
}
