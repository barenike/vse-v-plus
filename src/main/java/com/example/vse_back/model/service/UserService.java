package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.UserIsNotFoundException;
import com.example.vse_back.infrastructure.user.UserInfoChangeRequest;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.entity.RoleEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.RoleRepository;
import com.example.vse_back.model.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ImageService imageService;
    private final BalanceChangeRecordsService balanceChangeRecordsService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       ImageService imageService, BalanceChangeRecordsService balanceChangeRecordsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.imageService = imageService;
        this.balanceChangeRecordsService = balanceChangeRecordsService;
    }

    public UserEntity createUser(String email) {
        UserEntity user = new UserEntity();
        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        user.setRole(userRole);
        user.setEnabled(true);
        user.setEmail(email);
        user.setUserBalance(0);
        userRepository.save(user);
        return user;
    }

    // isActivated (were there at least one successful login?) and isBlocked(is user allowed to access his/her account?)
    public void enableUser(UserEntity user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void disableUser(UserEntity user) {
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void changeUserBalance(UserEntity user, Integer balance, String cause) {
        balanceChangeRecordsService.createRecord(user, balance, cause);
        user.setUserBalance(balance);
        userRepository.save(user);
    }

    public void changeUserInfo(UserEntity user, UserInfoChangeRequest userInfoChangeRequest) {
        user.setPhoneNumber(userInfoChangeRequest.getPhoneNumber());
        user.setFirstName(userInfoChangeRequest.getFirstName());
        user.setLastName(userInfoChangeRequest.getLastName());
        user.setJobTitle(userInfoChangeRequest.getJobTitle());
        user.setInfoAbout(userInfoChangeRequest.getInfoAbout());
        user.setImage(setupImage(user, userInfoChangeRequest.getFile()));
        userRepository.save(user);
    }

    private ImageEntity setupImage(UserEntity user, MultipartFile file) {
        if (file == null && user.getImage() != null) {
            imageService.deleteImage(user.getImage().getId());
        } else if (file != null) {
            if (user.getImage() != null) {
                imageService.deleteImage(user.getImage().getId());
            }
            return imageService.createAndGetImage(file);
        }
        return null;
    }

    public UserEntity getUserById(String id) {
        Optional<UserEntity> user = userRepository.findById(UUID.fromString(id));
        if (user.isEmpty()) {
            throw new UserIsNotFoundException(id);
        }
        return user.get();
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUserById(UUID id) {
        if (userRepository.existsById(id)) {
            ImageEntity image = getUserById(String.valueOf(id)).getImage();
            userRepository.deleteById(id);
            if (image != null) {
                imageService.deleteImage(image.getId());
            }
            return true;
        }
        return false;
    }
}