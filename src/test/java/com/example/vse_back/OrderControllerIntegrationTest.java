package com.example.vse_back;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestService testService;

    @AfterEach
    void cleanup() {
        testService.deleteProduct();
    }

    @Test
    void createOrder_Returns_201() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        mvc.perform(MockMvcRequestBuilders.post("/user/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderCreationDetails\" : [{\"productId\" : \"" + testService.getProductId() + "\",\"quantity\" : 2}]}")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isCreated());
    }

    @Test
    void createOrder_Returns_403_When_NotEnoughCoins() throws Exception {
        testService.setUserBalance(10);
        testService.createProduct();
        mvc.perform(MockMvcRequestBuilders.post("/user/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderCreationDetails\" : [{\"productId\" : \"" + testService.getProductId() + "\",\"quantity\" : 2}]}")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isForbidden());
    }

    // createOrder_Returns_403_When_NotEnoughProductAmount

    @Test
    void getMyOrders_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/user/orders")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMyOrder_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.delete("/user/orders/{orderId}", testService.getOrderId())
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void manipulateOrders_GetAllOrders_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/admin/orders")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void manipulateOrders_GetOrdersByUserId_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/admin/orders")
                        .param("userId", String.valueOf(testService.getUserId()))
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void manipulateOrders_ChangeStatus_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/admin/orders")
                        .param("orderId", testService.getOrderId())
                        .param("status", "PROCESSING")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void manipulateOrders_Delete_Returns_200() throws Exception {
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/admin/orders")
                        .param("orderId", testService.getOrderId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }
}
