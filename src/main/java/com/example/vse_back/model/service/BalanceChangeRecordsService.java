package com.example.vse_back.model.service;

import com.example.vse_back.model.entity.BalanceChangeRecordEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.BalanceChangeRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.vse_back.model.service.utils.LocalUtil.getCurrentMoscowDate;

@Service
public class BalanceChangeRecordsService {
    private final BalanceChangeRecordRepository balanceChangeRecordRepository;

    public BalanceChangeRecordsService(BalanceChangeRecordRepository balanceChangeRecordRepository) {
        this.balanceChangeRecordRepository = balanceChangeRecordRepository;
    }

    public List<BalanceChangeRecordEntity> getBalanceChangeRecordsByUserId(UUID userId) {
        return balanceChangeRecordRepository.findByUserId(userId);
    }

    public void createChangeBalanceRecord(UserEntity objectUser, UserEntity subjectUser, Integer newBalance, String cause) {
        BalanceChangeRecordEntity userRecord = new BalanceChangeRecordEntity();
        userRecord.setObjectUser(objectUser);
        userRecord.setSubjectUser(subjectUser);
        userRecord.setChangeAmount(newBalance - objectUser.getUserBalance());
        userRecord.setCause(cause);
        userRecord.setDate(getCurrentMoscowDate());
        balanceChangeRecordRepository.save(userRecord);
    }

    public void transferCoins(UserEntity objectUser, UserEntity subjectUser, Integer transferSum, String cause) {
        BalanceChangeRecordEntity userRecord = new BalanceChangeRecordEntity();
        userRecord.setObjectUser(objectUser);
        userRecord.setSubjectUser(subjectUser);
        userRecord.setChangeAmount(transferSum);
        userRecord.setCause(cause);
        userRecord.setDate(getCurrentMoscowDate());
        balanceChangeRecordRepository.save(userRecord);
    }
}
