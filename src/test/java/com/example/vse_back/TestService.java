package com.example.vse_back;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.BadgeEntity;
import com.example.vse_back.model.entity.OrderEntity;
import com.example.vse_back.model.entity.PostEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.*;
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
    // In production change to @chelpipegroup.com so that it would comply with the RegExp pattern
    final String testUserEmail = "lilo-games@mail.ru";
    final String userEmail = "user@chelpipegroup.com";
    final String adminEmail = "admin@chelpipegroup.com";
    final String phoneNumber = "+77777777777";
    final String firstName = "Adam";
    final String lastName = "Smith";
    final String jobTitle = "Economist";
    final String infoAbout = "Member of the Royal Society of Arts";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AuthCodeService authCodeService;
    @Autowired
    private PostService postService;
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private JwtProvider jwtProvider;

    void createTestAccount() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", testUserEmail);
        mvc.perform(MockMvcRequestBuilders.post("/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jo.toString()));
    }

    void nullifyUserInfo() throws Exception {
        MultiValueMap<String, String> nullParameters = new LinkedMultiValueMap<>();
        nullParameters.add("phoneNumber", null);
        nullParameters.add("firstName", null);
        nullParameters.add("lastName", null);
        nullParameters.add("jobTitle", null);
        nullParameters.add("infoAbout", null);
        mvc.perform(MockMvcRequestBuilders.multipart("/info/change")
                .params(nullParameters)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + getUserJWT()));
    }

    String getCode() {
        return authCodeService.getAuthCodeByUserId(getTestUserId()).getCode();
    }

    UUID getTestUserId() {
        return userService.getUserByEmail(testUserEmail).getId();
    }

    UUID getUserId() {
        return userService.getUserByEmail(userEmail).getId();
    }

    UUID getAdminId() {
        return userService.getUserByEmail(adminEmail).getId();
    }

    void setUserBalance(Integer balance) throws Exception {
        UserEntity user = userService.getUserByEmail(userEmail);
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
        return jwtProvider.generateJwt(String.valueOf(getUserId()));
    }

    String getAdminJWT() {
        return jwtProvider.generateJwt(String.valueOf(getAdminId()));
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
        String productId = getProductId();
        if (productId != null) {
            productService.deleteProductById(UUID.fromString(productId));
        }
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

    void createPost() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("title", "Test title");
            parameters.add("text", "Test text");
            mvc.perform(MockMvcRequestBuilders.multipart("/admin/post")
                    .file(file)
                    .params(parameters)
                    .header("Authorization", "Bearer " + getAdminJWT()));
        }
    }

    UUID getPostId() {
        List<PostEntity> posts = postService.getPostByUserId(getAdminId());
        if (posts.size() == 0) {
            return null;
        }
        return posts.get(0).getId();
    }

    void deletePost() {
        UUID id = getPostId();
        if (id != null) {
            postService.deletePostById(id);
        }
    }

    void createBadge() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("name", "Test name");
            parameters.add("description", "Test description");
            mvc.perform(MockMvcRequestBuilders.multipart("/admin/badge/create")
                    .file(file)
                    .params(parameters)
                    .header("Authorization", "Bearer " + getAdminJWT()));
        }
    }

    UUID getBadgeId() {
        List<BadgeEntity> badges = badgeService.getAllBadges();
        int size = badges.size();
        if (size == 0) {
            return null;
        }
        return badges.get(size - 1).getId();
    }

    void deleteBadge() {
        UUID id = getBadgeId();
        if (id != null) {
            badgeService.deleteBadgeById(id);
        }
    }
}
