package com.example.fiap.archburgers.domain.utils;

import com.example.fiap.archburgers.domain.testUtils.TestLocale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {
    @BeforeAll
    static void beforeAll() {
        TestLocale.setDefault();
    }

    @Test
    public void testToTimestamp_ShouldReturnCorrectTimestamp_WhenLocalDateTimeIsProvided() {
        LocalDateTime dateTime = LocalDateTime.of(2022, 3, 14, 12, 34, 56);

        assertThat(DateUtils.toTimestamp(dateTime)).isEqualTo(1647272096000L);
    }
}