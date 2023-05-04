package com.example.vse_back;

import org.json.JSONObject;
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
class UserBadgeControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestService testService;

    @AfterEach
    void cleanup() {
        testService.deleteBadge();
    }

    @Test
    void getMyUserBadges_Returns_200() throws Exception {
        testService.createBadge();
        mvc.perform(MockMvcRequestBuilders.get("/user/user_badges")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBadges_Returns_200() throws Exception {
        testService.createBadge();
        mvc.perform(MockMvcRequestBuilders.get("/user_badges/{userId}", testService.getUserId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void UserBadgeActivation_Returns_200() throws Exception {
        testService.createBadge();
        JSONObject jo = new JSONObject();
        jo.put("userBadgeId", testService.getUserBadgeId());
        jo.put("isActivated", true);
        mvc.perform(MockMvcRequestBuilders.post("/admin/user_badge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jo.toString())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }
}
