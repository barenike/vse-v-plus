package com.example.vse_back.model.service;

import com.example.vse_back.model.entity.BalanceChangeRecordEntity;
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

    public List<BalanceChangeRecordEntity> getBalanceChangeRecordsByUserId(String userId) {
        return balanceChangeRecordsRepository.findByObjectUserId(UUID.fromString(userId));
    }

    public void createChangeBalanceRecord(UserEntity objectUser, UserEntity subjectUser, Integer newBalance, String cause) {
        BalanceChangeRecordEntity objectUserRecord = new BalanceChangeRecordEntity();
        objectUserRecord.setObjectUser(objectUser);
        objectUserRecord.setSubjectUser(subjectUser);
        objectUserRecord.setChangeAmount(newBalance - objectUser.getUserBalance());
        objectUserRecord.setCause(cause);
        objectUserRecord.setDate(getCurrentMoscowDate());
        balanceChangeRecordsRepository.save(objectUserRecord);

        BalanceChangeRecordEntity subjectUserRecord = new BalanceChangeRecordEntity();
        subjectUserRecord.setObjectUser(subjectUser);
        subjectUserRecord.setSubjectUser(subjectUser);
        subjectUserRecord.setChangeAmount(newBalance - objectUser.getUserBalance());
        subjectUserRecord.setCause("Changed the balance of a user with an email " + objectUser.getEmail() + " with the cause: " + cause);
        subjectUserRecord.setDate(getCurrentMoscowDate());
        balanceChangeRecordsRepository.save(subjectUserRecord);
    }

    public void transferCoins(UserEntity objectUser, UserEntity subjectUser, Integer transferSum, String cause) {
        BalanceChangeRecordEntity objectUserRecord = new BalanceChangeRecordEntity();
        objectUserRecord.setObjectUser(objectUser);
        objectUserRecord.setSubjectUser(subjectUser);
        objectUserRecord.setChangeAmount(transferSum);
        objectUserRecord.setCause(cause);
        objectUserRecord.setDate(getCurrentMoscowDate());
        balanceChangeRecordsRepository.save(objectUserRecord);

        BalanceChangeRecordEntity subjectUserRecord = new BalanceChangeRecordEntity();
        subjectUserRecord.setObjectUser(subjectUser);
        subjectUserRecord.setSubjectUser(subjectUser);
        subjectUserRecord.setChangeAmount(-transferSum);
        subjectUserRecord.setCause("Transferred coins to " + objectUser.getEmail() + " with the cause: " + cause);
        subjectUserRecord.setDate(getCurrentMoscowDate());
        balanceChangeRecordsRepository.save(subjectUserRecord);
    }
}
