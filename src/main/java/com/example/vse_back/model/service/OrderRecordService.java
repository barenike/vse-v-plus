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

    public void create(List<OrderCreationDetails> orderCreationDetails, UUID id) {
        for (OrderCreationDetails details : orderCreationDetails) {
            OrderRecordEntity orderRecord = new OrderRecordEntity();
            orderRecord.setOrderId(id);
            orderRecord.setProductId(UUID.fromString(details.getProductId()));
            orderRecord.setQuantity(details.getQuantity());
            orderRecordRepository.save(orderRecord);
        }
    }

    public List<OrderRecordEntity> findAllOrderRecords() {
        return orderRecordRepository.findAll();
    }

    public List<OrderRecordEntity> findOrderRecordsByOrderId(UUID orderId) {
        return orderRecordRepository.findByOrderId(orderId);
    }

    public List<OrderRecordEntity> findAllOrderRecordsById(UUID id) {
        List<OrderRecordEntity> allOrders = findAllOrderRecords();
        return allOrders.stream().filter(orderRecord -> orderRecord.getId().equals(id)).collect(Collectors.toList());
    }

    public boolean delete(UUID id) {
        if (orderRecordRepository.existsById(id)) {
            orderRecordRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
