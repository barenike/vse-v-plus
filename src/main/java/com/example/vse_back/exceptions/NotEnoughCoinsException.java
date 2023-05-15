package com.example.vse_back.exceptions;

public class NotEnoughCoinsException extends RuntimeException {
    public NotEnoughCoinsException(Integer userBalance) {
        super(String.format("You do not have enough coins to do this. Current balance is %s", userBalance));
    }
}
