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
    public void register_Returns_201() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("password", testService.password);
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isCreated());
    }

    @Test
    public void register_Returns_400_When_EmailPatternIsViolated() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", "lilo-games@gmail.ru");
        jo.put("password", testService.password);
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_Returns_400_When_PasswordPatternIsViolated() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("password", "12345Aa");
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_Returns_403_When_EmailAlreadyRegistered() throws Exception {
        testService.register();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("password", testService.password);
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void auth_Returns_200() throws Exception {
        testService.register();
        testService.enableUser();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("password", testService.password);
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void auth_Returns_403_When_UserIsNotEnabled() throws Exception {
        testService.register();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("password", testService.password);
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void auth_Returns_401_When_EmailIsIncorrect() throws Exception {
        testService.register();
        testService.enableUser();
        JSONObject jo = new JSONObject();
        jo.put("email", "ilo-games@mail.ru");
        jo.put("password", testService.password);
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void auth_Returns_401_When_PasswordIsIncorrect() throws Exception {
        testService.register();
        testService.enableUser();
        JSONObject jo = new JSONObject();
        jo.put("email", testService.email);
        jo.put("password", "123456A");
        jo.put("phoneNumber", testService.phoneNumber);
        jo.put("firstName", testService.firstName);
        jo.put("lastName", testService.lastName);
        mvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void info_Returns_200() throws Exception {
        testService.register();
        testService.enableUser();
        mvc.perform(MockMvcRequestBuilders.get("/info")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    public void infoChange_Returns_200() throws Exception {
        testService.register();
        testService.enableUser();
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
        testService.register();
        UserEntity user = userService.getUserByEmail("lilo-games@mail.ru");
        mvc.perform(MockMvcRequestBuilders
                        .delete("/admin/{userId}", user.getId().toString())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUser_Returns_304_When_UserIsNotFound() throws Exception {
        testService.register();
        mvc.perform(MockMvcRequestBuilders
                        .delete("/admin/{userId}", "c4f44950-2b80-4cf0-a060-ad99d19cc636")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isNotModified());
    }

    @Test
    public void changeUserBalance_Returns_200() throws Exception {
        testService.register();
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
        testService.register();
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
        testService.register();
        mvc.perform(MockMvcRequestBuilders.get("/admin/info")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }
}
