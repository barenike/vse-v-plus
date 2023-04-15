package com.example.vse_back;

import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserService;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Test
    public void authEmail_Returns_200() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        mvc.perform(MockMvcRequestBuilders.post("/auth/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void authCode_Returns_200() throws Exception {
        testService.createAccount();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("code", testService.getCode());
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void authCode_Returns_403_When_ThereIsNoUserWithThisEmail() throws Exception {
        testService.createAccount();
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
    public void authCode_Returns_403_When_AuthCodeIsInvalid() throws Exception {
        testService.createAccount();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("code", "000000");
        mvc.perform(MockMvcRequestBuilders.post("/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }

    // Can falsely fail with probability 3 / 1 000 000
    @Test
    public void authCode_Returns_403_When_TooManyAuthAttempts() throws Exception {
        testService.createAccount();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
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
    public void getMyInfo_Returns_200() throws Exception {
        testService.createAccount();
        mvc.perform(MockMvcRequestBuilders.get("/info")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    public void changeMyInfo_Returns_200() throws Exception {
        testService.createAccount();
        JSONObject jo = new JSONObject();
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        jo.put("jobTitle", testService.jobTitle);
        jo.put("infoAbout", testService.infoAbout);
        mvc.perform(MockMvcRequestBuilders.post("/info/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testService.getUserJWT())
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUser_Returns_200() throws Exception {
        testService.createAccount();
        UserEntity user = userService.getUserByEmail("lilo-games@mail.ru");
        mvc.perform(MockMvcRequestBuilders
                        .delete("/admin/{userId}", user.getId().toString())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUser_Returns_304_When_UserIsNotFound() throws Exception {
        testService.createAccount();
        mvc.perform(MockMvcRequestBuilders
                        .delete("/admin/{userId}", "c4f44950-2b80-4cf0-a060-ad99d19cc636")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isNotModified());
    }

    @Test
    public void changeUserBalance_Returns_200() throws Exception {
        testService.createAccount();
        UserEntity user = userService.getUserByEmail("lilo-games@mail.ru");
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
    public void changeUserBalance_Returns_403_When_UserIsNotFound() throws Exception {
        testService.createAccount();
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
    public void getAllUsersInfo_Returns_200() throws Exception {
        testService.createAccount();
        mvc.perform(MockMvcRequestBuilders.get("/info/all_users")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }
}
