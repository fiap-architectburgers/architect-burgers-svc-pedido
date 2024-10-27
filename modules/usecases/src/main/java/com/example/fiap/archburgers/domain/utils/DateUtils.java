package com.example.fiap.archburgers.domain.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
