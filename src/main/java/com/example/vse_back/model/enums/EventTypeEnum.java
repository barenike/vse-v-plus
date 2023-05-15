package com.example.vse_back.model.enums;

public enum EventTypeEnum {
    STANDARD(10),
    EXTENDED(20),
    PREMIUM(30);

    public final Integer pricePerParticipant;

    EventTypeEnum(Integer pricePerParticipant) {
        this.pricePerParticipant = pricePerParticipant;
    }
}
