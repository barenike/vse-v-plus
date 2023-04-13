package com.example.vse_back.controller;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.exceptions.NotEnabledUserException;
import com.example.vse_back.exceptions.TokenIsNotFoundException;
import com.example.vse_back.infrastructure.user.*;
import com.example.vse_back.model.entity.PasswordResetTokenEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.entity.VerificationTokenEntity;
import com.example.vse_back.model.service.PasswordResetTokenService;
import com.example.vse_back.model.service.UserService;
import com.example.vse_back.model.service.email_verification.OnRegistrationCompleteEvent;
import com.example.vse_back.model.service.email_verification.VerificationTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final VerificationTokenService verificationTokenService;
    private final JwtProvider jwtProvider;
    private final ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService,
                          PasswordResetTokenService passwordResetTokenService,
                          VerificationTokenService verificationTokenService,
                          JwtProvider jwtProvider,
                          ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.verificationTokenService = verificationTokenService;
        this.jwtProvider = jwtProvider;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest,
                                          HttpServletRequest request) {
        UserEntity user = userService.createUser(registrationRequest);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Test is needed.
    @GetMapping("/register/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        VerificationTokenEntity verificationToken = verificationTokenService.getToken(token);
        if (verificationToken == null) {
            throw new TokenIsNotFoundException("Verification token", token);
        }
        UserEntity user = verificationToken.getUser();
        user.setEnabled(true);
        userService.enableUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestBody @Valid AuthRequest authRequest) {
        UserEntity user = userService.getUserByEmailAndPassword(authRequest.getEmail(), authRequest.getPassword());
        if (!user.isEnabled()) {
            throw new NotEnabledUserException();
        }
        String token = jwtProvider.generateToken(String.valueOf(user.getId()));
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getInfo(@RequestHeader(name = "Authorization") String token) {
        String userId = jwtProvider.getUserIdFromRawToken(token);
        UserEntity user = userService.getUserById(userId);
        return new ResponseEntity<>(new InfoResponse(
                user.getId().toString(),
                user.getRoleEntity().getRoleId(),
                user.getEmail(),
                user.getUserBalance(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getJobTitle(),
                user.getInfoAbout()),
                HttpStatus.OK);
    }

    @PostMapping("/info/change")
    public ResponseEntity<?> changeInfo(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody @Valid UserInfoChangeRequest userInfoChangeRequest) {
        String userId = jwtProvider.getUserIdFromRawToken(token);
        UserEntity user = userService.getUserById(userId);
        userService.changeUserInfo(user, userInfoChangeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Test is needed.
    @PostMapping("/reset/password/{email}")
    public ResponseEntity<?> sendResetPasswordMail(@PathVariable(name = "email") String email,
                                                   HttpServletRequest request) {
        UserEntity user = userService.getUserByEmail(email);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        passwordResetTokenService.resetPassword(user, appUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Test is needed.
    @GetMapping("/reset/password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable("token") String token,
                                           @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        PasswordResetTokenEntity passwordResetToken = passwordResetTokenService.validatePasswordResetToken(token);
        if (passwordResetToken == null) {
            throw new TokenIsNotFoundException("Reset token", token);
        }
        UserEntity user = passwordResetToken.getUser();
        userService.changeUserPassword(user, resetPasswordRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "userId") UUID userId) {
        final boolean isDeleted = userService.deleteUserById(userId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @PostMapping("/admin/user_balance")
    public ResponseEntity<?> changeUserBalance(@RequestBody @Valid UserBalanceRequest userBalanceRequest) {
        String userId = userBalanceRequest.getUserId();
        UserEntity user = userService.getUserById(userId);
        userService.changeUserBalance(user, userBalanceRequest.getUserBalance(), userBalanceRequest.getCause());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin/info")
    public ResponseEntity<?> getAllUsersInfo() {
        List<UserEntity> users = userService.getAllUsers();
        List<InfoResponse> result = users.stream().map(user -> new InfoResponse(
                user.getId().toString(),
                user.getRoleEntity().getRoleId(),
                user.getEmail(),
                user.getUserBalance(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getJobTitle(),
                user.getInfoAbout()
        )).toList();
        AdminInfoResponse adminInfoResponse = new AdminInfoResponse(result);
        return adminInfoResponse.getInfoList() != null && !adminInfoResponse.getInfoList().isEmpty()
                ? new ResponseEntity<>(adminInfoResponse, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }
}