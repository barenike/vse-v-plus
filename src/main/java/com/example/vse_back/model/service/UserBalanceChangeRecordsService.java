package com.example.vse_back.model.service;

import com.example.vse_back.model.entity.UserBalanceChangeRecordsEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.UserBalanceChangeRecordsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.vse_back.utils.Util.getCurrentMoscowDate;

@Service
public class UserBalanceChangeRecordsService {
    private final UserBalanceChangeRecordsRepository userBalanceChangeRecordsRepository;

    public UserBalanceChangeRecordsService(UserBalanceChangeRecordsRepository userBalanceChangeRecordsRepository) {
        this.userBalanceChangeRecordsRepository = userBalanceChangeRecordsRepository;
    }

    public List<UserBalanceChangeRecordsEntity> getUserBalanceChangeRecordsByUserId(String userId) {
        return userBalanceChangeRecordsRepository.findByUserId(UUID.fromString(userId));
    }

    public void createRecord(UserEntity user, Integer newBalance, String cause) {
        UserBalanceChangeRecordsEntity changeRecord = new UserBalanceChangeRecordsEntity();
        changeRecord.setUserId(user.getId());
        changeRecord.setChangeAmount(newBalance - user.getUserBalance());
        changeRecord.setCause(cause);
        changeRecord.setDate(getCurrentMoscowDate());
        userBalanceChangeRecordsRepository.save(changeRecord);
    }
}
