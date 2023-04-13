package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.EmailAlreadyRegisteredException;
import com.example.vse_back.exceptions.IncorrectEmailException;
import com.example.vse_back.exceptions.IncorrectPasswordException;
import com.example.vse_back.exceptions.UserIsNotFoundException;
import com.example.vse_back.infrastructure.user.RegistrationRequest;
import com.example.vse_back.infrastructure.user.ResetPasswordRequest;
import com.example.vse_back.infrastructure.user.UserInfoChangeRequest;
import com.example.vse_back.model.entity.RoleEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.RoleRepository;
import com.example.vse_back.model.repository.UserRepository;
import com.example.vse_back.model.service.email_verification.VerificationTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserBalanceChangeRecordsService userBalanceChangeRecordsService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       VerificationTokenService verificationTokenService,
                       PasswordResetTokenService passwordResetTokenService,
                       UserBalanceChangeRecordsService userBalanceChangeRecordsService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenService = verificationTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.userBalanceChangeRecordsService = userBalanceChangeRecordsService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity createUser(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        if (getUserByEmailNullUnsafe(email) != null) {
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

    public void changeUserPassword(UserEntity user, ResetPasswordRequest resetPasswordRequest) {
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
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

    private UserEntity getUserByEmailNullUnsafe(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IncorrectEmailException(email);
        }
        return user;
    }

    public UserEntity getUserByEmailAndPassword(String email, String password) {
        UserEntity user = getUserByEmail(email);
        boolean isPasswordCorrect = passwordEncoder.matches(password, user.getPassword());
        if (!isPasswordCorrect) {
            throw new IncorrectPasswordException();
        }
        return user;
    }

    public List<UserEntity> getAllUsers() {
        List<UserEntity> users = readAll();
        users.removeIf(user -> user.getRoleEntity().getName().equals("ROLE_ADMIN") || !user.isEnabled());
        return users;
    }

    public boolean deleteUserById(UUID userId) {
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