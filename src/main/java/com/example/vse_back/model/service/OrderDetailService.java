package com.example.vse_back.model.service;

import com.example.vse_back.infrastructure.order.OrderCreationDetails;
import com.example.vse_back.model.entity.OrderDetailEntity;
import com.example.vse_back.model.entity.OrderEntity;
import com.example.vse_back.model.repository.OrderDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductService productService;

    public OrderDetailService(OrderDetailRepository orderDetailRepository, ProductService productService) {
        this.orderDetailRepository = orderDetailRepository;
        this.productService = productService;
    }

    public void createOrderDetail(List<OrderCreationDetails> orderCreationDetails, OrderEntity order) {
        for (OrderCreationDetails detail : orderCreationDetails) {
            OrderDetailEntity orderDetail = new OrderDetailEntity();
            orderDetail.setOrder(order);
            orderDetail.setProduct(productService.getProductById(UUID.fromString(detail.getProductId())));
            orderDetail.setQuantity(detail.getQuantity());
            orderDetailRepository.save(orderDetail);
        }
    }

    public List<OrderDetailEntity> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    public List<OrderDetailEntity> getOrderDetailsByOrderId(UUID orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    public List<OrderDetailEntity> getAllOrderDetailsById(UUID id) {
        List<OrderDetailEntity> allOrders = getAllOrderDetails();
        return allOrders.stream().filter(orderDetail -> orderDetail.getId().equals(id)).toList();
    }

    public boolean deleteOrderDetailById(UUID id) {
        if (orderDetailRepository.existsById(id)) {
            orderDetailRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
