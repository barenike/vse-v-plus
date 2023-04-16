package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.PostIsNotFoundException;
import com.example.vse_back.infrastructure.posts.PostCreationRequest;
import com.example.vse_back.infrastructure.posts.PostResponse;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.entity.PostEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.PostRepository;
import com.example.vse_back.model.service.utils.LocalUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ImageService imageService;

    public PostService(PostRepository postRepository, ImageService imageService) {
        this.postRepository = postRepository;
        this.imageService = imageService;
    }

    public void createPost(PostCreationRequest postCreationRequest, UserEntity user) {
        PostEntity post = new PostEntity();
        post.setTitle(postCreationRequest.getTitle());
        post.setText(postCreationRequest.getText());
        post.setDate(LocalUtil.getCurrentMoscowDate());
        post.setUser(user);
        ImageEntity image = imageService.createImage(postCreationRequest.getFile());
        post.setImage(image);
        postRepository.save(post);
    }

    public boolean deleteProductById(UUID id) {
        if (postRepository.existsById(id)) {
            imageService.deleteImage(getPostById(id).getImage().getId());
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public PostEntity getPostById(UUID id) {
        PostEntity post = postRepository.findByPostId(id);
        if (post == null) {
            throw new PostIsNotFoundException(id.toString());
        }
        return post;
    }

    public List<PostResponse> getAllPosts() {
        List<PostEntity> products = postRepository.findAll();
        return products.stream().map(post -> new PostResponse(
                post.getId().toString(),
                post.getTitle(),
                post.getText(),
                post.getDate(),
                post.getUser().getId().toString(),
                post.getImage()
        )).collect(Collectors.toList());
    }
}
