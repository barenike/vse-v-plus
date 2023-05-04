package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.EntityIsNotFoundException;
import com.example.vse_back.infrastructure.post.PostCreationRequest;
import com.example.vse_back.infrastructure.post.PostEditRequest;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.entity.PostEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.PostRepository;
import com.example.vse_back.model.service.utils.LocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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
        ImageEntity image = imageService.createAndGetImage(postCreationRequest.getFile());
        post.setImage(image);
        postRepository.save(post);
    }

    public void editPost(PostEditRequest postEditRequest) {
        PostEntity post = getPostById(UUID.fromString(postEditRequest.getPostId()));
        post.setTitle(postEditRequest.getTitle());
        post.setText(postEditRequest.getText());
        post.setImage(setupImage(post, postEditRequest.getFile()));
        postRepository.save(post);
    }

    // Create interface with this method and make it implemented by PostService and UserService?
    private ImageEntity setupImage(PostEntity post, MultipartFile file) {
        if (file == null && post.getImage() != null) {
            imageService.deleteImage(post.getImage().getId());
        } else if (file != null) {
            if (post.getImage() != null) {
                imageService.deleteImage(post.getImage().getId());
            }
            return imageService.createAndGetImage(file);
        }
        return null;
    }

    public boolean deletePostById(UUID id) {
        if (postRepository.existsById(id)) {
            ImageEntity image = getPostById(id).getImage();
            postRepository.deleteById(id);
            if (image != null) {
                imageService.deleteImage(image.getId());
            }
            return true;
        }
        return false;
    }

    public PostEntity getPostById(UUID id) {
        PostEntity post = postRepository.findByPostId(id);
        if (post == null) {
            throw new EntityIsNotFoundException("post", id.toString());
        }
        return post;
    }

    public List<PostEntity> getPostByUserId(UUID userId) {
        return postRepository.findByUserId(userId);
    }

    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }
}
