package com.example.vse_back;

import org.junit.jupiter.api.AfterEach;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Negative tests?
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = VseBackApplication.class)
@AutoConfigureMockMvc
@Transactional
class PostControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestService testService;

    @AfterEach
    void cleanup() {
        testService.deletePost();
    }

    @Test
    void createPost_Returns_201() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("title", "Test title");
            parameters.add("text", "Test text");
            mvc.perform(MockMvcRequestBuilders.multipart("/admin/post")
                            .file(file)
                            .params(parameters)
                            .header("Authorization", "Bearer " + testService.getAdminJWT()))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void editPost_Returns_200() throws Exception {
        testService.createPost();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("postId", String.valueOf(testService.getPostId()));
        parameters.add("title", "New test title");
        parameters.add("text", "New test text");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test_image.jpg")) {
            MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "application/json", inputStream);
            mvc.perform(MockMvcRequestBuilders.multipart("/admin/post/edit")
                            .file(file)
                            .params(parameters)
                            .header("Authorization", "Bearer " + testService.getAdminJWT()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void deletePost_Returns_200() throws Exception {
        testService.createPost();
        mvc.perform(MockMvcRequestBuilders.delete("/admin/post/{postId}", testService.getPostId())
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPosts_Returns_200() throws Exception {
        testService.createPost();
        mvc.perform(MockMvcRequestBuilders.get("/common/posts")
                        .header("Authorization", "Bearer " + testService.getAdminJWT()))
                .andExpect(status().isOk());
    }
}
