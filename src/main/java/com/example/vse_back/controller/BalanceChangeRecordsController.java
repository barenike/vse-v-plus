package com.example.vse_back.controller;

import com.example.vse_back.model.entity.BalanceChangeRecordsEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.BalanceChangeRecordsService;
import com.example.vse_back.model.service.utils.LocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class BalanceChangeRecordsController {
    private final BalanceChangeRecordsService balanceChangeRecordsService;
    private final LocalUtil localUtil;

    public BalanceChangeRecordsController(BalanceChangeRecordsService balanceChangeRecordsService, LocalUtil localUtil) {
        this.balanceChangeRecordsService = balanceChangeRecordsService;
        this.localUtil = localUtil;
    }

    @Operation(summary = "Get the list of user balance's change records (user)")
    @GetMapping("/user/balance_change_records")
    public ResponseEntity<List<BalanceChangeRecordsEntity>> getUserBalanceChangeRecords(@RequestHeader(name = "Authorization") String token) {
        UserEntity user = localUtil.getUserFromToken(token);
        final List<BalanceChangeRecordsEntity> userBalanceRecords
                = balanceChangeRecordsService.getUserBalanceChangeRecordsByUserId(String.valueOf(user.getId()));
        return userBalanceRecords != null && !userBalanceRecords.isEmpty()
                ? new ResponseEntity<>(userBalanceRecords, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get the list of user balance's change records (admin)")
    @GetMapping("/admin/balance_change_records/{userId}")
    public ResponseEntity<List<BalanceChangeRecordsEntity>> getUserBalanceChangeRecords(@PathVariable(name = "userId") UUID userId) {
        final List<BalanceChangeRecordsEntity> userBalanceRecords = balanceChangeRecordsService.getUserBalanceChangeRecordsByUserId(String.valueOf(userId));
        return userBalanceRecords != null && !userBalanceRecords.isEmpty()
                ? new ResponseEntity<>(userBalanceRecords, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }
}