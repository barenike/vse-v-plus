package com.example.vse_back.controller;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.exceptions.AuthTokenHasExpiredException;
import com.example.vse_back.exceptions.AuthTokenIsNotValidException;
import com.example.vse_back.infrastructure.user.*;
import com.example.vse_back.model.entity.AuthTokenEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserService;
import com.example.vse_back.model.service.email_verification.AuthTokenService;
import com.example.vse_back.model.service.email_verification.OnRegistrationCompleteEvent;
import com.example.vse_back.utils.Util;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final JwtProvider jwtProvider;
    private final ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService,
                          AuthTokenService authTokenService,
                          JwtProvider jwtProvider,
                          ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.authTokenService = authTokenService;
        this.jwtProvider = jwtProvider;
        this.eventPublisher = eventPublisher;
    }

    @Operation(summary = "Send a one-time auth token to the email address")
    @PostMapping("/auth/email")
    public ResponseEntity<?> sendAuthTokenToEmailAddress(@RequestBody @Valid AuthEmailRequest authEmailRequest) {
        UserEntity user = userService.getUserByEmail(authEmailRequest.getEmail());
        if (user == null) {
            user = userService.createUser(authEmailRequest.getEmail());
        }
        authTokenService.deleteByUserId(user.getId());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get JWT")
    @PostMapping("/auth/token")
    public ResponseEntity<?> authToken(@RequestBody @Valid AuthTokenRequest authEmailRequest) {
        String email = authEmailRequest.getEmail();
        AuthTokenEntity authToken = authTokenService.getToken(authEmailRequest.getToken());
        String token = authToken.getToken();
        try {
            if (!authToken.getUser().getEmail().equals(email)) {
                throw new AuthTokenIsNotValidException(token);
            } else if (ChronoUnit.MINUTES.between(authToken.getCreationDate(), Util.getCurrentMoscowDate()) > 60) {
                throw new AuthTokenHasExpiredException(token);
            } else {
                UserEntity user = userService.getUserByEmail(email);
                String jwt = jwtProvider.generateJwtToken(String.valueOf(user.getId()));
                return new ResponseEntity<>(new AuthResponse(jwt), HttpStatus.OK);
            }
        } finally {
            authTokenService.deleteByToken(token);
        }
    }

    @Operation(summary = "Get my profile info")
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

    @Operation(summary = "Change my profile info")
    @PostMapping("/info/change")
    public ResponseEntity<?> changeInfo(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody @Valid UserInfoChangeRequest userInfoChangeRequest) {
        String userId = jwtProvider.getUserIdFromRawToken(token);
        UserEntity user = userService.getUserById(userId);
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

    @Operation(summary = "Get profile info of all users")
    @GetMapping("/info/all_users")
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