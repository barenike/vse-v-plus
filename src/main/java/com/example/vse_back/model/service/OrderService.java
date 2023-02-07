package com.example.vse_back.model.service;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.exceptions.NotEnoughCoinsException;
import com.example.vse_back.exceptions.ProductIsNotFoundException;
import com.example.vse_back.infrastructure.order.OrderCreationDetails;
import com.example.vse_back.infrastructure.order.OrderCreationRequest;
import com.example.vse_back.model.entity.*;
import com.example.vse_back.model.repository.OrderRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final UserService userService;
    private final ProductService productService;
    private final OrderRecordService orderRecordService;
    private final OrderRepository orderRepository;
    private final JwtProvider jwtProvider;

    public OrderService(UserService userService, ProductService productService, OrderRecordService orderRecordService, OrderRepository orderRepository, JwtProvider jwtProvider) {
        this.userService = userService;
        this.productService = productService;
        this.orderRecordService = orderRecordService;
        this.orderRepository = orderRepository;
        this.jwtProvider = jwtProvider;
    }

    protected static Date getCurrentYekaterinburgDate() {
        DateTimeZone zoneYekaterinburg = DateTimeZone.forID("Asia/Yekaterinburg");
        DateTime now = DateTime.now(zoneYekaterinburg);
        return now.toDate();
    }

    @Transactional
    public void create(OrderCreationRequest orderCreationRequest, String token) {
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatusEnum.CREATED.toString());
        String userId = jwtProvider.getUserIdFromToken(token.substring(7));
        UserEntity user = userService.findByUserId(userId);
        order.setUserId(UUID.fromString(userId));
        order.setCreationDate(getCurrentYekaterinburgDate());
        int total = 0;
        for (OrderCreationDetails orderCreationDetails : orderCreationRequest.getOrderCreationDetails()) {
            String productId = orderCreationDetails.getProductId();
            ProductEntity product = productService.getProduct(UUID.fromString(productId));
            if (product == null) {
                throw new ProductIsNotFoundException(productId);
            }
            total += product.getPrice() * orderCreationDetails.getQuantity();
        }
        Integer userBalance = user.getUserBalance();
        if (total > userBalance) {
            throw new NotEnoughCoinsException(userBalance);
        }
        order.setTotal(total);
        orderRepository.save(order);
        orderRecordService.create(orderCreationRequest.getOrderCreationDetails(), order.getId());
        productService.changeProductAmount(orderCreationRequest.getOrderCreationDetails());
        userService.changeUserBalance(user, userBalance - total);
    }

    public boolean changeStatus(UUID id, String status) {
        if (orderRepository.existsById(id)) {
            OrderEntity order = orderRepository.getById(id);
            order.setStatus(OrderStatusEnum.valueOf(status).toString());
            if (OrderStatusEnum.SHIPPED.toString().equals(status)) {
                order.setShippingDate(getCurrentYekaterinburgDate());
            } else if (OrderStatusEnum.COMPLETED.toString().equals(status)) {
                order.setCompletionDate(getCurrentYekaterinburgDate());
            }
            orderRepository.save(order);
            return true;
        } else {
            return false;
        }
    }

    public List<OrderEntity> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderEntity> findOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public boolean delete(UUID id) {
        if (orderRepository.existsById(id)) {
            OrderEntity order = orderRepository.getById(id);
            if (!order.getStatus().equals(OrderStatusEnum.CREATED.toString())) {
                return false;
            }
            String userId = String.valueOf(order.getUserId());
            UserEntity user = userService.findByUserId(userId);
            Integer userBalance = user.getUserBalance();
            Integer total = order.getTotal();
            List<OrderRecordEntity> orderRecords = orderRecordService.findOrderRecordsByOrderId(id);
            for (OrderRecordEntity orderRecord : orderRecords) {
                ProductEntity product = productService.getProduct(orderRecord.getProductId());
                if (product == null) {
                    throw new ProductIsNotFoundException(orderRecord.getProductId().toString());
                }
                Integer quantity = orderRecord.getQuantity();
                boolean isDeleted = orderRecordService.delete(orderRecord.getId());
                if (!isDeleted) {
                    return false;
                }
                productService.changeProductAmount(product, product.getAmount() + quantity);
            }
            orderRepository.deleteById(id);
            userService.changeUserBalance(user, userBalance + total);
            return true;
        } else {
            return false;
        }
    }
}
