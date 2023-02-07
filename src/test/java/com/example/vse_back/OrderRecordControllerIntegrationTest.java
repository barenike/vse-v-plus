package com.example.vse_back;

import com.example.vse_back.model.entity.OrderRecordEntity;
import com.example.vse_back.model.service.OrderRecordService;
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
public class OrderRecordControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestService testService;

    @Autowired
    private OrderRecordService orderRecordService;

    @AfterEach
    void cleanup() {
        testService.deleteProduct();
    }

    @Test
    public void getMyOrderRecordsByOrderId_Returns_200() throws Exception {
        testService.register();
        testService.enableUser();
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/user/order_records/{orderId}", testService.getOrderId())
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
        testService.deleteProduct();
    }

    @Test
    public void getMyOrderRecordsByOrderId_Returns_404() throws Exception {
        testService.register();
        testService.enableUser();
        mvc.perform(MockMvcRequestBuilders.get("/user/order_records/{orderId}", "fb96924c-f4a2-4576-8b8b-42b903d9a822")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrderRecords_Returns_200() throws Exception {
        testService.register();
        testService.enableUser();
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_records")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
        testService.deleteProduct();
    }

    @Test
    public void getOrderRecordById_Returns_200() throws Exception {
        testService.register();
        testService.enableUser();
        testService.setUserBalance(20);
        testService.createProduct();
        testService.createOrder();
        List<OrderRecordEntity> orderRecords = orderRecordService.findOrderRecordsByOrderId(UUID.fromString(testService.getOrderId()));
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_records/{orderRecordId}", orderRecords.get(0).getId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
        testService.deleteProduct();
    }

    @Test
    public void getOrderRecordById_Returns_403_When_OrderRecordIsNotFound() throws Exception {
        testService.register();
        testService.enableUser();
        mvc.perform(MockMvcRequestBuilders.get("/admin/order_records/{orderRecordId}", "d6a3e216-fb57-4f0e-81c9-400b25d1b32c")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isForbidden());
    }
}
