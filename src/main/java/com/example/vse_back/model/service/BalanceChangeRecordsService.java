package com.example.vse_back.model.service;

import com.example.vse_back.model.entity.BalanceChangeRecordsEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.BalanceChangeRecordsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.vse_back.model.service.utils.LocalUtil.getCurrentMoscowDate;

@Service
public class BalanceChangeRecordsService {
    private final BalanceChangeRecordsRepository balanceChangeRecordsRepository;

    public BalanceChangeRecordsService(BalanceChangeRecordsRepository balanceChangeRecordsRepository) {
        this.balanceChangeRecordsRepository = balanceChangeRecordsRepository;
    }

    public List<BalanceChangeRecordsEntity> getUserBalanceChangeRecordsByUserId(String userId) {
        return balanceChangeRecordsRepository.findByUserId(UUID.fromString(userId));
    }

    public void createRecord(UserEntity user, Integer newBalance, String cause) {
        BalanceChangeRecordsEntity changeRecord = new BalanceChangeRecordsEntity();
        changeRecord.setUser(user);
        changeRecord.setChangeAmount(newBalance - user.getUserBalance());
        changeRecord.setCause(cause);
        changeRecord.setDate(getCurrentMoscowDate());
        balanceChangeRecordsRepository.save(changeRecord);
    }
}
