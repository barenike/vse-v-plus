package com.example.vse_back.controller;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.exceptions.AuthCodeHasExpiredException;
import com.example.vse_back.exceptions.AuthCodeIsInvalidException;
import com.example.vse_back.exceptions.AuthCodeIsNotFoundException;
import com.example.vse_back.exceptions.UserIsNotFoundException;
import com.example.vse_back.infrastructure.user.*;
import com.example.vse_back.model.entity.AuthCodeEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserService;
import com.example.vse_back.model.service.email_verification.AuthCodeService;
import com.example.vse_back.model.service.email_verification.OnRegistrationCompleteEvent;
import com.example.vse_back.model.service.utils.LocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthCodeService authCodeService;
    private final LocalUtil localUtil;
    private final JwtProvider jwtProvider;
    private final ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService,
                          AuthCodeService authCodeService,
                          LocalUtil localUtil, JwtProvider jwtProvider, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.authCodeService = authCodeService;
        this.localUtil = localUtil;
        this.jwtProvider = jwtProvider;
        this.eventPublisher = eventPublisher;
    }

    @Operation(summary = "Send a one-time auth code to the email address")
    @PostMapping("/auth/email")
    public ResponseEntity<?> sendAuthCodeToEmailAddress(@RequestBody @Valid AuthEmailRequest authEmailRequest) {
        UserEntity user = userService.getUserByEmail(authEmailRequest.getEmail());
        if (user == null) {
            user = userService.createUser(authEmailRequest.getEmail());
        }
        authCodeService.deleteByUserId(user.getId());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get JWT")
    @PostMapping("/auth/code")
    public ResponseEntity<?> authCode(@RequestBody @Valid AuthCodeRequest authCodeRequest) {
        String email = authCodeRequest.getEmail();
        String inputCode = authCodeRequest.getCode();
        UserEntity user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UserIsNotFoundException(email);
        }
        UUID userId = user.getId();
        AuthCodeEntity authCode = authCodeService.getAuthCodeByUserId(userId);
        if (authCode == null) {
            throw new AuthCodeIsNotFoundException(email);
        }
        String originalCode = authCode.getCode();
        if (!originalCode.equals(inputCode)) {
            authCodeService.incrementAttemptCount(authCode);
            throw new AuthCodeIsInvalidException(inputCode);
        } else if (ChronoUnit.MINUTES.between(authCode.getDate(), LocalUtil.getCurrentMoscowDate()) > 5) {
            authCodeService.deleteByUserId(userId);
            throw new AuthCodeHasExpiredException(inputCode);
        } else {
            authCodeService.deleteByUserId(userId);
            String jwt = jwtProvider.generateJwt(String.valueOf(user.getId()));
            return new ResponseEntity<>(new AuthResponse(jwt), HttpStatus.OK);
        }
    }

    @Operation(summary = "Get my info")
    @GetMapping("/info")
    public ResponseEntity<?> getMyInfo(@RequestHeader(name = "Authorization") String token) {
        UserEntity user = localUtil.getUserFromToken(token);
        return new ResponseEntity<>(new FullUserInfoResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getJobTitle(),
                user.getInfoAbout(),
                user.getImage()),
                HttpStatus.OK);
    }

    // Test is needed
    @Operation(summary = "Get basic info of all users")
    @GetMapping("/info/all_users")
    public ResponseEntity<?> getFullUserInfo() {
        List<UserEntity> users = userService.getAllUsers();
        List<BasicUserInfoResponse> basicInfoResponseList = users.stream().map(user -> new BasicUserInfoResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        )).toList();
        BasicAllUsersInfoResponse response = new BasicAllUsersInfoResponse(basicInfoResponseList);
        return response.getInfoList() != null && !response.getInfoList().isEmpty()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    // Test is needed
    @Operation(summary = "Get full info of a user")
    @GetMapping("/info/{userId}")
    public ResponseEntity<?> getFullUserInfo(@PathVariable(name = "userId") String userId) {
        UserEntity user = userService.getUserById(userId);
        FullUserInfoResponse response = new FullUserInfoResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getJobTitle(),
                user.getInfoAbout(),
                user.getImage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Test is needed
    @Operation(summary = "Change my profile info")
    @PostMapping("/info/change")
    public ResponseEntity<?> changeMyInfo(@RequestHeader(name = "Authorization") String token,
                                          @RequestBody @Valid UserInfoChangeRequest userInfoChangeRequest) {
        UserEntity user = localUtil.getUserFromToken(token);
        userService.changeUserInfo(user, userInfoChangeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete the user")
    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "userId") UUID userId) {
        final boolean isDeleted = userService.deleteUserById(userId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @Operation(summary = "Change the user's balance")
    @PostMapping("/admin/user_balance")
    public ResponseEntity<?> changeUserBalance(@RequestBody @Valid UserBalanceRequest userBalanceRequest) {
        String userId = userBalanceRequest.getUserId();
        UserEntity user = userService.getUserById(userId);
        userService.changeUserBalance(user, userBalanceRequest.getUserBalance(), userBalanceRequest.getCause());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}