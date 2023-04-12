package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.EmailAlreadyRegisteredException;
import com.example.vse_back.exceptions.IncorrectEmailException;
import com.example.vse_back.exceptions.IncorrectPasswordException;
import com.example.vse_back.infrastructure.user.RegistrationRequest;
import com.example.vse_back.infrastructure.user.ResetPasswordRequest;
import com.example.vse_back.model.entity.RoleEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.RoleRepository;
import com.example.vse_back.model.repository.UserRepository;
import com.example.vse_back.model.service.email_verification.VerificationTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       VerificationTokenService verificationTokenService,
                       PasswordResetTokenService passwordResetTokenService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenService = verificationTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity create(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        if (findByEmail(email) != null) {
            throw new EmailAlreadyRegisteredException(email);
        }
        UserEntity user = new UserEntity();
        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        user.setRoleEntity(userRole);
        user.setEnabled(false);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setPhoneNumber(registrationRequest.getPhoneNumber());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setUserBalance(0);
        userRepository.save(user);
        return user;
    }

    public void enableUser(UserEntity user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void changeUserBalance(UserEntity user, Integer userBalance) {
        user.setUserBalance(userBalance);
        userRepository.save(user);
    }

    public void changePassword(UserEntity user, ResetPasswordRequest resetPasswordRequest) {
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userRepository.save(user);
    }

    public List<UserEntity> readAll() {
        return userRepository.findAll();
    }

    public UserEntity read(UUID userId) {
        return userRepository.getById(userId);
    }

    public UserEntity findByUserId(String userId) {
        return userRepository.findByUserId(UUID.fromString(userId));
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity findByEmailAndPassword(String email, String password) {
        UserEntity user = findByEmail(email);
        if (user == null) {
            throw new IncorrectEmailException(email);
        }
        boolean isPasswordCorrect = passwordEncoder.matches(password, user.getPassword());
        if (!isPasswordCorrect) {
            throw new IncorrectPasswordException();
        }
        return user;
    }

    public List<UserEntity> findAllUsers() {
        List<UserEntity> users = readAll();
        users.removeIf(user -> user.getRoleEntity().getName().equals("ROLE_ADMIN") || !user.isEnabled());
        return users;
    }

    public boolean delete(UUID userId) {
        if (userRepository.existsById(userId)) {
            boolean isVerificationTokenDeleted = verificationTokenService.deleteByUserId(userId);
            passwordResetTokenService.deleteByUserId(userId);
            if (!isVerificationTokenDeleted) {
                return false;
            }
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}