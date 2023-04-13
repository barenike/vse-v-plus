package com.example.vse_back.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Util {
    public static LocalDateTime getCurrentMoscowDate() {
        ZoneId zone = ZoneId.of("Europe/Moscow");
        ZonedDateTime date = ZonedDateTime.now(zone);
        return date.toLocalDateTime();
    }
}
