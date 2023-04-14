package com.example.vse_back.exceptions;

public class NotEnoughCoinsException extends RuntimeException {
    public NotEnoughCoinsException(Integer userBalance) {
        super(String.format("The user does not have enough coins to pay for this order. Current balance is %s", userBalance));
    }
}
