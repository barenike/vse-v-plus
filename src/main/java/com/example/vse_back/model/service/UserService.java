package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.UserIsNotFoundException;
import com.example.vse_back.infrastructure.user.UserInfoChangeRequest;
import com.example.vse_back.model.entity.RoleEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.RoleRepository;
import com.example.vse_back.model.repository.UserRepository;
import com.example.vse_back.model.service.email_verification.AuthTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthTokenService authTokenService;
    private final UserBalanceChangeRecordsService userBalanceChangeRecordsService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       AuthTokenService authTokenService,
                       UserBalanceChangeRecordsService userBalanceChangeRecordsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authTokenService = authTokenService;
        this.userBalanceChangeRecordsService = userBalanceChangeRecordsService;
    }

    public UserEntity createUser(String email) {
        UserEntity user = new UserEntity();
        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        user.setRoleEntity(userRole);
        user.setEnabled(true);
        user.setEmail(email);
        user.setUserBalance(0);
        userRepository.save(user);
        return user;
    }

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
        userBalanceChangeRecordsService.createRecord(user, balance, cause);
        user.setUserBalance(balance);
        userRepository.save(user);
    }

    public void changeUserInfo(UserEntity user, UserInfoChangeRequest userInfoChangeRequest) {
        user.setPhoneNumber(userInfoChangeRequest.getPhoneNumber());
        user.setFirstName(userInfoChangeRequest.getFirstName());
        user.setLastName(userInfoChangeRequest.getLastName());
        user.setJobTitle(userInfoChangeRequest.getJobTitle());
        user.setInfoAbout(userInfoChangeRequest.getInfoAbout());
        userRepository.save(user);
    }

    private List<UserEntity> readAll() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(String userId) {
        UserEntity user = userRepository.findByUserId(UUID.fromString(userId));
        if (user == null) {
            throw new UserIsNotFoundException(userId);
        }
        return user;
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserEntity> getAllUsers() {
        List<UserEntity> users = readAll();
        users.removeIf(user -> user.getRoleEntity().getName().equals("ROLE_ADMIN") || !user.isEnabled());
        return users;
    }

    public boolean deleteUserById(UUID userId) {
        if (userRepository.existsById(userId)) {
            boolean isVerificationTokenDeleted = authTokenService.deleteByUserId(userId);
            if (!isVerificationTokenDeleted) {
                return false;
            }
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}