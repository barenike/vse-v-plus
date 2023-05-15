package com.example.vse_back;

import com.example.vse_back.model.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
class EventControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private EventService eventService;

    @Autowired
    private TestService testService;

    @Test
    void createEvent_Returns_201() throws Exception {
        testService.createTestAccount();
        testService.setUserBalance(100);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("type", "STANDARD");
            parameters.add("title", "Test title");
            parameters.add("description", "Test description");
            parameters.add("eventDate", String.valueOf(LocalDateTime.now()));
            parameters.add("participantIds",
                    testService.getUserId()
                            + ", " + testService.getTestUserId()
                            + ", " + testService.getAdminId());
            mvc.perform(MockMvcRequestBuilders.multipart("/user/event/create")
                            .file(file)
                            .params(parameters)
                            .header("Authorization", "Bearer " + testService.getUserJWT()))
                    .andExpect(status().isCreated());
        }
    }
}
