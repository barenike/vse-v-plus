package com.example.vse_back.model.service;

import com.example.vse_back.infrastructure.order.OrderCreationDetails;
import com.example.vse_back.model.entity.OrderRecordEntity;
import com.example.vse_back.model.repository.OrderRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderRecordService {
    private final OrderRecordRepository orderRecordRepository;

    public OrderRecordService(OrderRecordRepository orderRecordRepository) {
        this.orderRecordRepository = orderRecordRepository;
    }

    public void createOrderRecord(List<OrderCreationDetails> orderCreationDetails, UUID id) {
        for (OrderCreationDetails details : orderCreationDetails) {
            OrderRecordEntity orderRecord = new OrderRecordEntity();
            orderRecord.setOrderId(id);
            orderRecord.setProductId(UUID.fromString(details.getProductId()));
            orderRecord.setQuantity(details.getQuantity());
            orderRecordRepository.save(orderRecord);
        }
    }

    public List<OrderRecordEntity> getAllOrderRecords() {
        return orderRecordRepository.findAll();
    }

    public List<OrderRecordEntity> getOrderRecordsByOrderId(UUID orderId) {
        return orderRecordRepository.findByOrderId(orderId);
    }

    public List<OrderRecordEntity> getAllOrderRecordsById(UUID id) {
        List<OrderRecordEntity> allOrders = getAllOrderRecords();
        return allOrders.stream().filter(orderRecord -> orderRecord.getId().equals(id)).collect(Collectors.toList());
    }

    public boolean deleteOrderRecordById(UUID id) {
        if (orderRecordRepository.existsById(id)) {
            orderRecordRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
