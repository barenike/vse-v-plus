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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
class BalanceChangeRecordsIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Test
    void getMyBalanceChangeRecords_Returns_200() throws Exception {
        UserEntity user = userService.getUserByEmail(testService.userEmail);
        JSONObject jo = new JSONObject();
        jo.put("userId", user.getId().toString());
        jo.put("userBalance", 200);
        jo.put("cause", "Тест");
        mvc.perform(MockMvcRequestBuilders.post("/admin/user_balance")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + testService.getAdminJWT())
                .content(jo.toString()));

        mvc.perform(MockMvcRequestBuilders.get("/common/balance_change_records")
                        .header("Authorization", "Bearer " + testService.getUserJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getBalanceChangeRecords_Returns_200() throws Exception {
        UserEntity user = userService.getUserByEmail(testService.userEmail);
        JSONObject jo = new JSONObject();
        jo.put("userId", user.getId().toString());
        jo.put("userBalance", 200);
        jo.put("cause", "Тест");
        mvc.perform(MockMvcRequestBuilders.post("/admin/user_balance")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + testService.getAdminJWT())
                .content(jo.toString()));

        mvc.perform(MockMvcRequestBuilders.get("/admin/balance_change_records/" + user.getId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }
}
