package com.example.vse_back.controller;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.exceptions.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          AuthCodeService authCodeService,
                          LocalUtil localUtil,
                          JwtProvider jwtProvider,
                          ApplicationEventPublisher eventPublisher,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authCodeService = authCodeService;
        this.localUtil = localUtil;
        this.jwtProvider = jwtProvider;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Send a one-time auth code to the email address")
    @PostMapping("/auth/email")
    public ResponseEntity<Object> sendAuthCodeToEmailAddress(@RequestBody @Valid AuthEmailRequest authEmailRequest) {
        UserEntity user = userService.getUserByEmail(authEmailRequest.getEmail());
        if (user == null) {
            user = userService.createUser(authEmailRequest.getEmail());
        }
        if (!user.isEnabled()) {
            throw new UserIsDisabledException();
        }
        authCodeService.deleteByUserId(user.getId());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get a JWT")
    @PostMapping("/auth/code")
    public ResponseEntity<AuthResponse> authCode(@RequestBody @Valid AuthCodeRequest authCodeRequest) {
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
        if (!passwordEncoder.matches(inputCode, authCode.getCode())) {
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
    @GetMapping("/common/info")
    public ResponseEntity<UserEntity> getMyInfo(@RequestHeader(name = "Authorization") String token) {
        UserEntity user = localUtil.getUserFromToken(token);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Get basic info of all users")
    @GetMapping("/common/info/all_users")
    public ResponseEntity<BasicAllUsersInfoResponse> getBasicUserInfo() {
        List<UserEntity> users = userService.getAllUsers();
        List<BasicUserInfoResponse> basicInfoResponseList = users.stream().map(user -> new BasicUserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getUserBalance(),
                user.getFirstName(),
                user.getLastName()
        )).toList();
        BasicAllUsersInfoResponse response = new BasicAllUsersInfoResponse(basicInfoResponseList);
        return response.getInfoList() != null && !response.getInfoList().isEmpty()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get full info of the user")
    @GetMapping("/common/info/{userId}")
    public ResponseEntity<UserEntity> getFullUserInfo(@PathVariable(name = "userId") UUID userId) {
        UserEntity user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Change my profile info (you need to pass all arguments, even if they haven't changed, else they would be set to null)")
    @PostMapping("/common/info/change")
    public ResponseEntity<Object> changeMyInfo(@RequestHeader(name = "Authorization") String token,
                                               @ModelAttribute @Valid UserInfoChangeRequest userInfoChangeRequest) {
        UserEntity user = localUtil.getUserFromToken(token);
        userService.changeUserInfo(user, userInfoChangeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete the user")
    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "userId") UUID userId) {
        final boolean isDeleted = userService.deleteUserById(userId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @Operation(summary = "Change the isEnabled field")
    @PostMapping("/admin/is_enabled")
    public ResponseEntity<Object> changeIsEnabledField(@RequestBody @Valid ChangeIsEnabledFieldRequest request) {
        userService.changeIsEnabledField(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Change the user's balance")
    @PostMapping("/admin/user_balance")
    public ResponseEntity<Object> changeUserBalance(@RequestHeader(name = "Authorization") String token,
                                                    @RequestBody @Valid UserBalanceChangeRequest userBalanceChangeRequest) {
        UserEntity subjectUser = localUtil.getUserFromToken(token);
        UUID userId = userBalanceChangeRequest.getUserId();
        UserEntity objectUser = userService.getUserById(userId);
        userService.changeUserBalance(objectUser, subjectUser, userBalanceChangeRequest.getUserBalance(), userBalanceChangeRequest.getCause());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Transfer coins to other user")
    @PostMapping("/user/transfer")
    public ResponseEntity<Object> transferCoins(@RequestHeader(name = "Authorization") String token,
                                                @RequestBody @Valid TransferCoinsRequest transferCoinsRequest) {
        UserEntity subjectUser = localUtil.getUserFromToken(token);
        UUID userId = transferCoinsRequest.getUserId();
        UserEntity objectUser = userService.getUserById(userId);
        userService.transferCoins(objectUser, subjectUser, transferCoinsRequest.getUserBalance(), transferCoinsRequest.getCause());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}