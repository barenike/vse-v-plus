package com.example.vse_back;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.OrderEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.DropboxService;
import com.example.vse_back.model.service.OrderService;
import com.example.vse_back.model.service.ProductService;
import com.example.vse_back.model.service.UserService;
import com.example.vse_back.model.service.email_verification.AuthCodeService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class TestService {
    // In production change to @chelpipegroup.com
    final String email = "lilo-games@mail.ru";
    final String adminEmail = "admin@chelpipegroup.com";
    final String phoneNumber = "+77777777777";
    final String firstName = "Adam";
    final String lastName = "Smith";
    final String jobTitle = "Economist";
    final String infoAbout = "Member of the Royal Society of Arts.";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private DropboxService dropboxService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AuthCodeService authCodeService;
    @Autowired
    private JwtProvider jwtProvider;

    void createAccount() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", email);
        mvc.perform(MockMvcRequestBuilders.post("/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jo.toString()));
    }

    void setUserBalance(Integer balance) throws Exception {
        UserEntity user = userService.getUserByEmail(email);
        JSONObject jo = new JSONObject();
        jo.put("userId", user.getId());
        jo.put("userBalance", balance);
        jo.put("cause", "Тест");
        mvc.perform(MockMvcRequestBuilders.post("/admin/user_balance")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + getAdminJWT())
                .content(jo.toString()));
    }

    String getUserJWT() {
        return jwtProvider.generateJwt(String.valueOf(userService.getUserByEmail(email).getId()));
    }

    String getAdminJWT() {
        return jwtProvider.generateJwt(String.valueOf(userService.getUserByEmail(adminEmail).getId()));
    }

    void createProduct() throws Exception {
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
                    .header("Authorization", "Bearer " + getAdminJWT()));
        }
    }

    String getProductId() {
        List<ProductResponse> productResponseList = productService.getAllProducts();
        String productId;
        productId = productResponseList
                .stream()
                .filter(productResponse -> productResponse.getName().equals("test_image"))
                .findFirst()
                .map(ProductResponse::getId)
                .orElse(null);
        return productId;
    }

    void deleteProduct() {
        dropboxService.deleteFile("/test_image.jpg");
    }

    void createOrder() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderCreationDetails\" : [{\"productId\" : \"" + getProductId() + "\",\"quantity\" : 2}]}")
                .header("Authorization", "Bearer " + getUserJWT()));
    }

    String getOrderId() {
        List<OrderEntity> orders = orderService.getOrdersByUserId(getUserId());
        return orders.get(0).getId().toString();
    }

    UUID getUserId() {
        return userService.getUserByEmail(email).getId();
    }

    String getCode() {
        return authCodeService.getAuthCodeByUserId(getUserId()).getCode();
    }
}
