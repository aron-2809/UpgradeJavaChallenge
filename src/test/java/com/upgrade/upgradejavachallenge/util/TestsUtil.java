package com.upgrade.upgradejavachallenge.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class TestsUtil {
    public static final LocalDateTime dateTime1 = LocalDateTime.of(LocalDate.of(2019, 12, 01), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime2 = LocalDateTime.of(LocalDate.of(2019, 12, 02), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime3 = LocalDateTime.of(LocalDate.of(2019, 12, 03), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime4 = LocalDateTime.of(LocalDate.of(2019, 12, 04), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime5 = LocalDateTime.of(LocalDate.of(2019, 12, 05), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime6 = LocalDateTime.of(LocalDate.of(2019, 12, 06), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime7 = LocalDateTime.of(LocalDate.of(2019, 12, 07), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime8 = LocalDateTime.of(LocalDate.of(2019, 12, 8), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime9 = LocalDateTime.of(LocalDate.of(2019, 12, 9), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime10 = LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime11 = LocalDateTime.of(LocalDate.of(2019, 12, 11), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime12 = LocalDateTime.of(LocalDate.of(2019, 12, 12), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime13 = LocalDateTime.of(LocalDate.of(2019, 12, 13), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime14 = LocalDateTime.of(LocalDate.of(2019, 12, 14), LocalTime.of(12, 00));
    public static final LocalDateTime dateTime15 = LocalDateTime.of(LocalDate.of(2019, 12, 15), LocalTime.of(12, 00));


    public static final LocalDateTime newDateTime1 = LocalDateTime.of(LocalDate.of(2020, 01, 01), LocalTime.of(12, 00));
    public static final LocalDateTime newDateTime2 = LocalDateTime.of(LocalDate.of(2020, 01, 30), LocalTime.of(12, 00));



    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
