package com.example.vse_back;

import com.example.vse_back.model.entity.OrderDetailEntity;
import com.example.vse_back.model.service.OrderDetailService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
public class OrderDetailControllerIntegrationTest {
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
    public void getMyOrderDetailsByOrderId_Returns_200() throws Exception {
        testService.createAccount();
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/user/order_details/{orderId}", testService.getOrderId())
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
        testService.deleteProduct();
    }

    @Test
    public void getMyOrderDetailsByOrderId_Returns_404() throws Exception {
        testService.createAccount();
        mvc.perform(MockMvcRequestBuilders.get("/user/order_details/{orderId}", "fb96924c-f4a2-4576-8b8b-42b903d9a822")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrderDetails_Returns_200() throws Exception {
        testService.createAccount();
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_details")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
        testService.deleteProduct();
    }

    @Test
    public void getOrderDetailById_Returns_200() throws Exception {
        testService.createAccount();
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        List<OrderDetailEntity> orderDetails = orderDetailService.getOrderDetailsByOrderId(UUID.fromString(testService.getOrderId()));
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_details/{orderDetailId}", orderDetails.get(0).getId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
        testService.deleteProduct();
    }

    @Test
    public void getOrderDetailById_Returns_403_When_OrderDetailIsNotFound() throws Exception {
        testService.createAccount();
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_details/{orderDetailId}", "d6a3e216-fb57-4f0e-81c9-400b25d1b32c")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isForbidden());
    }
}
