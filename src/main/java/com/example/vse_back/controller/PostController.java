package com.example.vse_back.controller;

import com.example.vse_back.infrastructure.post.PostCreationRequest;
import com.example.vse_back.infrastructure.post.PostEditRequest;
import com.example.vse_back.model.entity.PostEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.PostService;
import com.example.vse_back.model.service.utils.LocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class PostController {
    private final PostService postService;
    private final LocalUtil localUtil;

    public PostController(PostService postService, LocalUtil localUtil) {
        this.postService = postService;
        this.localUtil = localUtil;
    }

    @Operation(summary = "Create the post")
    @PostMapping("/admin/post")
    public ResponseEntity<Object> createPost(@RequestHeader(name = "Authorization") String token,
                                             @ModelAttribute @Valid PostCreationRequest postCreationRequest) {
        UserEntity user = localUtil.getUserFromToken(token);
        postService.createPost(postCreationRequest, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Tests are needed!
    @Operation(summary = "Edit the post")
    @PostMapping("/admin/post/edit")
    public ResponseEntity<Object> editPost(@ModelAttribute @Valid PostEditRequest postEditRequest) {
        postService.editPost(postEditRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete the post")
    @DeleteMapping("/admin/post/{postId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(name = "postId") UUID postId) {
        final boolean isDeleted = postService.deletePostById(postId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @Operation(summary = "Get all posts")
    @GetMapping("/common/posts")
    public ResponseEntity<List<PostEntity>> getAllProducts() {
        final List<PostEntity> posts = postService.getAllPosts();
        return posts != null && !posts.isEmpty()
                ? new ResponseEntity<>(posts, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }
}
