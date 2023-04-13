package com.example.vse_back.controller;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.model.entity.UserBalanceChangeRecordsEntity;
import com.example.vse_back.model.service.UserBalanceChangeRecordsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserBalanceChangeRecordsController {
    private final UserBalanceChangeRecordsService userBalanceChangeRecordsService;
    private final JwtProvider jwtProvider;

    public UserBalanceChangeRecordsController(UserBalanceChangeRecordsService userBalanceChangeRecordsService, JwtProvider jwtProvider) {
        this.userBalanceChangeRecordsService = userBalanceChangeRecordsService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/user/balance_change_records")
    public ResponseEntity<List<UserBalanceChangeRecordsEntity>> getMyOrderRecordsByOrderId(@RequestHeader(name = "Authorization") String token) {
        String userId = jwtProvider.getUserIdFromRawToken(token);
        final List<UserBalanceChangeRecordsEntity> userBalanceRecords = userBalanceChangeRecordsService.getUserBalanceChangeRecordsByUserId(userId);
        return userBalanceRecords != null && !userBalanceRecords.isEmpty()
                ? new ResponseEntity<>(userBalanceRecords, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
