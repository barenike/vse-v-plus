package com.example.vse_back;

import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserService;
import org.json.JSONObject;
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
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Test
    void authEmail_Returns_200() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", testService.testUserEmail);
        mvc.perform(MockMvcRequestBuilders.post("/auth/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void authCode_Returns_200() throws Exception {
        testService.createTestAccount();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.testUserEmail);
        jo.put("code", testService.getCode());
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void authCode_Returns_403_When_ThereIsNoUserWithThisEmail() throws Exception {
        testService.createTestAccount();
        JSONObject jo = new JSONObject();
        jo.put("email", "fake-email@mail.ru");
        jo.put("code", testService.getCode());
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }

    // Can falsely fail with probability 1 / 1 000 000
    @Test
    void authCode_Returns_403_When_AuthCodeIsInvalid() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", testService.userEmail);
        jo.put("code", "000000");
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }

    // Can falsely fail with probability 3 / 1 000 000
    @Test
    void authCode_Returns_403_When_TooManyAuthAttempts() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", testService.userEmail);
        jo.put("code", "000000");
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jo.toString()));
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jo.toString()));
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jo.toString()));
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyInfo_Returns_200() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/info")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsersInfo_Returns_200() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/info/all_users")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getFullUserInfo_Returns_200() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/info/{userId}", testService.getUserId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void changeMyInfo_Returns_200_WhenFileIsSetInsteadOfNull() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("phoneNumber", testService.phoneNumber);
            parameters.add("firstName", testService.firstName);
            parameters.add("lastName", testService.lastName);
            parameters.add("jobTitle", testService.jobTitle);
            parameters.add("infoAbout", testService.infoAbout);
            mvc.perform(MockMvcRequestBuilders.multipart("/info/change")
                            .file(file)
                            .params(parameters)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + testService.getUserJWT()))
                    .andExpect(status().isOk());
            testService.nullifyUserInfo();
        }
    }

    @Test
    void changeMyInfo_Returns_200_WhenFileIsSetInsteadOfExistingFile() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("phoneNumber", testService.phoneNumber);
            parameters.add("firstName", testService.firstName);
            parameters.add("lastName", testService.lastName);
            parameters.add("jobTitle", testService.jobTitle);
            parameters.add("infoAbout", testService.infoAbout);
            mvc.perform(MockMvcRequestBuilders.multipart("/info/change")
                    .file(file)
                    .params(parameters)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + testService.getUserJWT()));
            mvc.perform(MockMvcRequestBuilders.multipart("/info/change")
                            .file(file)
                            .params(parameters)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + testService.getUserJWT()))
                    .andExpect(status().isOk());
            testService.nullifyUserInfo();
        }
    }

    @Test
    void changeMyInfo_Returns_200_WhenNullIsSetInsteadOfAllNonNullAttributes() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("phoneNumber", testService.phoneNumber);
            parameters.add("firstName", testService.firstName);
            parameters.add("lastName", testService.lastName);
            parameters.add("jobTitle", testService.jobTitle);
            parameters.add("infoAbout", testService.infoAbout);
            mvc.perform(MockMvcRequestBuilders.multipart("/info/change")
                    .file(file)
                    .params(parameters)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + testService.getUserJWT()));
            MultiValueMap<String, String> nullParameters = new LinkedMultiValueMap<>();
            nullParameters.add("phoneNumber", null);
            nullParameters.add("firstName", null);
            nullParameters.add("lastName", null);
            nullParameters.add("jobTitle", null);
            nullParameters.add("infoAbout", null);
            mvc.perform(MockMvcRequestBuilders.multipart("/info/change")
                            .params(nullParameters)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + testService.getUserJWT()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void deleteUser_Returns_200() throws Exception {
        testService.createTestAccount();
        UserEntity user = userService.getUserByEmail(testService.testUserEmail);
        mvc.perform(MockMvcRequestBuilders
                        .delete("/admin/{userId}", user.getId().toString())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_Returns_304_When_UserIsNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/admin/{userId}", "c4f44950-2b80-4cf0-a060-ad99d19cc636")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isNotModified());
    }

    @Test
    void changeUserBalance_Returns_200() throws Exception {
        UserEntity user = userService.getUserByEmail(testService.userEmail);
        JSONObject jo = new JSONObject();
        jo.put("userId", user.getId().toString());
        jo.put("userBalance", 200);
        jo.put("cause", "Тест");
        mvc.perform(MockMvcRequestBuilders.post("/admin/user_balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testService.getAdminJWT())
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void changeUserBalance_Returns_403_When_UserIsNotFound() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("userId", "c4f44950-2b80-4cf0-a060-ad99d19cc636");
        jo.put("userBalance", 200);
        jo.put("cause", "Тест");
        mvc.perform(MockMvcRequestBuilders.post("/admin/user_balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testService.getAdminJWT())
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void transferCoins_Returns_200() throws Exception {
        UserEntity user = userService.getUserByEmail(testService.userEmail);
        user.setUserBalance(200);

        testService.createTestAccount();

        JSONObject jo = new JSONObject();
        jo.put("userId", testService.getTestUserId());
        jo.put("userBalance", 200);
        jo.put("cause", "Тест");
        mvc.perform(MockMvcRequestBuilders.post("/user/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testService.getUserJWT())
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void transferCoins_Returns_403_When_NotEnoughCoins() throws Exception {
        UserEntity user = userService.getUserByEmail(testService.userEmail);
        user.setUserBalance(200);

        testService.createTestAccount();

        JSONObject jo = new JSONObject();
        jo.put("userId", testService.getTestUserId());
        jo.put("userBalance", 201);
        jo.put("cause", "Тест");
        mvc.perform(MockMvcRequestBuilders.post("/user/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testService.getUserJWT())
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }
}
