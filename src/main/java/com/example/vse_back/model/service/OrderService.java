package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.NotEnoughCoinsException;
import com.example.vse_back.infrastructure.order.OrderCreationRequest;
import com.example.vse_back.infrastructure.order_detail.OrderCreationDetails;
import com.example.vse_back.model.entity.OrderDetailEntity;
import com.example.vse_back.model.entity.OrderEntity;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.enums.OrderStatusEnum;
import com.example.vse_back.model.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.example.vse_back.model.service.utils.LocalUtil.getCurrentMoscowDate;

@Service
public class OrderService {
    private final UserService userService;
    private final ProductService productService;
    private final OrderDetailService orderDetailService;
    private final OrderRepository orderRepository;

    public OrderService(UserService userService,
                        ProductService productService,
                        OrderDetailService orderDetailService,
                        OrderRepository orderRepository) {
        this.userService = userService;
        this.productService = productService;
        this.orderDetailService = orderDetailService;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void createOrder(OrderCreationRequest orderCreationRequest, UserEntity user) {
        List<OrderCreationDetails> orderCreationDetails = orderCreationRequest.getOrderCreationDetails();
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatusEnum.CREATED.toString());
        order.setUser(user);
        order.setCreationDate(getCurrentMoscowDate());
        Integer total = getTotal(orderCreationDetails);
        Integer userBalance = user.getUserBalance();
        if (total > userBalance) {
            throw new NotEnoughCoinsException(userBalance);
        }
        order.setTotal(total);
        orderRepository.save(order);
        orderDetailService.createOrderDetails(orderCreationDetails, order);
        productService.setupProductAmount(orderCreationDetails);
        userService.changeUserBalance(user, user, userBalance - total, "Оформление заказа");
    }

    private Integer getTotal(List<OrderCreationDetails> orderCreationDetails) {
        int total = 0;
        for (OrderCreationDetails orderCreationDetail : orderCreationDetails) {
            UUID productId = orderCreationDetail.getProductId();
            ProductEntity product = productService.getProductById(productId);
            total += product.getPrice() * orderCreationDetail.getQuantity();
        }
        return total;
    }

    public boolean changeOrderStatus(UUID id, OrderStatusEnum status) {
        if (orderRepository.existsById(id)) {
            OrderEntity order = orderRepository.getReferenceById(id);
            order.setStatus(status.toString());
            if (OrderStatusEnum.CREATED.equals(status)) {
                order.setCreationDate(getCurrentMoscowDate());
            } else if (OrderStatusEnum.PROCESSING.equals(status)) {
                order.setProcessingDate(getCurrentMoscowDate());
            } else if (OrderStatusEnum.COMPLETED.equals(status)) {
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

    // Refactor
    @Transactional
    public boolean deleteOrderById(UUID id, UserEntity subjectUser) {
        if (orderRepository.existsById(id)) {
            OrderEntity order = orderRepository.getReferenceById(id);
            if (!order.getStatus().equals(OrderStatusEnum.CREATED.toString())) {
                return false;
            }
            UserEntity user = order.getUser();
            Integer userBalance = user.getUserBalance();
            Integer total = order.getTotal();
            List<OrderDetailEntity> orderDetails = orderDetailService.getOrderDetailsByOrderId(id);
            for (OrderDetailEntity orderDetail : orderDetails) {
                ProductEntity product = orderDetail.getProduct();
                Integer quantity = orderDetail.getQuantity();
                boolean isDeleted = orderDetailService.deleteOrderDetailById(orderDetail.getId());
                if (!isDeleted) {
                    return false;
                }
                productService.setupProductAmount(product, product.getAmount() + quantity);
            }
            orderRepository.deleteById(id);
            userService.changeUserBalance(user, subjectUser, userBalance + total, "Отмена заказа");
            return true;
        } else {
            return false;
        }
    }
}
