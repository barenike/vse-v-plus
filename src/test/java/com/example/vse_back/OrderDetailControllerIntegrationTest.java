package com.example.vse_back;

import com.example.vse_back.model.entity.OrderDetailEntity;
import com.example.vse_back.model.service.OrderDetailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
class OrderDetailControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestService testService;

    @Autowired
    private OrderDetailService orderDetailService;

    @AfterEach
    void cleanup() {
        testService.deleteProduct();
    }

    @Test
    void getMyOrderDetailsByOrderId_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/user/order_details/{orderId}", testService.getOrderId())
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderDetails_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_details")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderDetailById_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        List<OrderDetailEntity> orderDetails = orderDetailService.getOrderDetailsByOrderId(UUID.fromString(testService.getOrderId()));
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_details/{orderDetailId}", orderDetails.get(0).getId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }
}
