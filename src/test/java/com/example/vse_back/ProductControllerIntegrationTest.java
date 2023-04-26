package com.example.vse_back;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestService testService;

    @AfterEach
    void cleanup() {
        testService.deleteProduct();
    }

    @Test
    void createProduct_Returns_201() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("name", "test_image");
            parameters.add("price", "10");
            parameters.add("description", "Great test_image!");
            parameters.add("amount", "100");
            mvc.perform(MockMvcRequestBuilders.multipart("/admin/product")
                            .file(file)
                            .params(parameters)
                            .header("Authorization", "Bearer " + testService.getAdminJWT()))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void changeProductAmount_Returns_200() throws Exception {
        testService.createProduct();
        JSONObject jo = new JSONObject();
        jo.put("productId", testService.getProductId());
        jo.put("amount", 500);
        mvc.perform(MockMvcRequestBuilders.post("/admin/product_amount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void changeProductAmount_Returns_403_When_ProductDoesNotExist() throws Exception {
        testService.createProduct();
        JSONObject jo = new JSONObject();
        jo.put("productId", "a04096a1-014c-40a8-8471-46dbf85113b4");
        jo.put("amount", 500);
        mvc.perform(MockMvcRequestBuilders.post("/admin/product_amount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllProducts_Returns_200() throws Exception {
        testService.createProduct();
        mvc.perform(MockMvcRequestBuilders.get("/products")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getProduct_Returns_200() throws Exception {
        testService.createProduct();
        mvc.perform(MockMvcRequestBuilders.get("/products/{productId}", testService.getProductId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getProduct_Returns_404_When_ProductDoesNotExist() throws Exception {
        testService.createProduct();
        mvc.perform(MockMvcRequestBuilders.get("/products/{productId}", "a04096a1-014c-40a8-8471-46dbf85113b4")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_Returns_200() throws Exception {
        testService.createProduct();
        mvc.perform(MockMvcRequestBuilders.delete("/admin/product/{productId}", testService.getProductId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_Returns_304_When_ProductDoesNotExist() throws Exception {
        testService.createProduct();
        mvc.perform(MockMvcRequestBuilders.delete("/admin/product/{productId}", "a04096a1-014c-40a8-8471-46dbf85113b4")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isNotModified());
    }
}
