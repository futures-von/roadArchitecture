package com.von.base.java8.example12;

import javax.swing.text.DateFormatter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

public class DateTest {

    public static void main(String[] args) {
        String dateStr = "2025-05-08 21:48:55";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
        System.out.println(Instant.now().getEpochSecond());
        System.out.println(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        LocalDateTime localDateTime1 = localDateTime.plusSeconds(20);
        System.out.println(localDateTime1.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
//        localDateTime1.toInstant(ZoneOffset.systemDefault().)
        System.out.println(localDateTime1.format(dateTimeFormatter));
        System.out.println(System.currentTimeMillis());
        System.out.println(localDateTime.withYear(2026).format(dateTimeFormatter));
        System.out.println(Instant.from(ZonedDateTime.now()));
        Instant now = Instant.now();
        System.out.println(now.toEpochMilli());
        System.out.println(now.getEpochSecond());
        System.out.println(now.getLong(ChronoField.MILLI_OF_SECOND));
        System.out.println(now.getLong(ChronoField.MICRO_OF_SECOND));
        System.out.println(now.getLong(ChronoField.NANO_OF_SECOND));
        System.out.println(now.getNano());
    }
}
