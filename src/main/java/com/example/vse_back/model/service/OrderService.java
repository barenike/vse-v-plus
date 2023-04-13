package com.example.vse_back.model.service;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.exceptions.NotEnoughCoinsException;
import com.example.vse_back.infrastructure.order.OrderCreationDetails;
import com.example.vse_back.infrastructure.order.OrderCreationRequest;
import com.example.vse_back.model.entity.*;
import com.example.vse_back.model.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.example.vse_back.utils.Util.getCurrentMoscowDate;

@Service
public class OrderService {
    private final UserService userService;
    private final ProductService productService;
    private final OrderRecordService orderRecordService;
    private final OrderRepository orderRepository;
    private final JwtProvider jwtProvider;

    public OrderService(UserService userService,
                        ProductService productService,
                        OrderRecordService orderRecordService,
                        OrderRepository orderRepository,
                        JwtProvider jwtProvider) {
        this.userService = userService;
        this.productService = productService;
        this.orderRecordService = orderRecordService;
        this.orderRepository = orderRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public void createOrder(OrderCreationRequest orderCreationRequest, String token) {
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatusEnum.CREATED.toString());
        String userId = jwtProvider.getUserIdFromRawToken(token);
        UserEntity user = userService.getUserById(userId);
        order.setUserId(UUID.fromString(userId));
        order.setCreationDate(getCurrentMoscowDate());
        int total = 0;
        for (OrderCreationDetails orderCreationDetails : orderCreationRequest.getOrderCreationDetails()) {
            String productId = orderCreationDetails.getProductId();
            ProductEntity product = productService.getProductById(UUID.fromString(productId));
            total += product.getPrice() * orderCreationDetails.getQuantity();
        }
        Integer userBalance = user.getUserBalance();
        if (total > userBalance) {
            throw new NotEnoughCoinsException(userBalance);
        }
        order.setTotal(total);
        orderRepository.save(order);
        orderRecordService.createOrderRecord(orderCreationRequest.getOrderCreationDetails(), order.getId());
        productService.changeProductAmount(orderCreationRequest.getOrderCreationDetails());
        userService.changeUserBalance(user, userBalance - total, "Оформление заказа");
    }

    public boolean changeOrderStatus(UUID id, String status) {
        if (orderRepository.existsById(id)) {
            OrderEntity order = orderRepository.getById(id);
            order.setStatus(OrderStatusEnum.valueOf(status).toString());
            if (OrderStatusEnum.SHIPPED.toString().equals(status)) {
                order.setShippingDate(getCurrentMoscowDate());
            } else if (OrderStatusEnum.COMPLETED.toString().equals(status)) {
                order.setCompletionDate(getCurrentMoscowDate());
            }
            orderRepository.save(order);
            return true;
        } else {
            return false;
        }
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderEntity> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public boolean deleteOrderById(UUID id) {
        if (orderRepository.existsById(id)) {
            OrderEntity order = orderRepository.getById(id);
            if (!order.getStatus().equals(OrderStatusEnum.CREATED.toString())) {
                return false;
            }
            String userId = String.valueOf(order.getUserId());
            UserEntity user = userService.getUserById(userId);
            Integer userBalance = user.getUserBalance();
            Integer total = order.getTotal();
            List<OrderRecordEntity> orderRecords = orderRecordService.getOrderRecordsByOrderId(id);
            for (OrderRecordEntity orderRecord : orderRecords) {
                ProductEntity product = productService.getProductById(orderRecord.getProductId());
                Integer quantity = orderRecord.getQuantity();
                boolean isDeleted = orderRecordService.deleteOrderRecordById(orderRecord.getId());
                if (!isDeleted) {
                    return false;
                }
                productService.changeProductAmount(product, product.getAmount() + quantity);
            }
            orderRepository.deleteById(id);
            userService.changeUserBalance(user, userBalance + total, "Отмена заказа");
            return true;
        } else {
            return false;
        }
    }
}
