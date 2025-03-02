package com.github.walterfan.kata.reminder.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtil {
    public static final String ISO_8601_DATE_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static Date stringToDate(String dateString) {
        try {
            // Define the date format for parsing the input string
            SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATE_FMT);
            return formatter.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Error parsing date string: " + e.getMessage());
            return null;
        }
    }
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        // Define the date format for ISO 8601 format
        SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATE_FMT);
        return formatter.format(date);
    }
    
    public static String instantToIsoString(Instant instant) {
        if (instant == null) {
            throw new IllegalArgumentException("Instant cannot be null");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT; // 标准 ISO-8601 格式
        return formatter.format(instant);
    }
    
    public static Instant isoStringToInstant(String isoString) {
        if (isoString == null || isoString.isEmpty()) {
            throw new IllegalArgumentException("ISO string cannot be null or empty");
        }
        try {
            return Instant.parse(isoString); // ISO-8601 字符串直接解析为 Instant
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid ISO-8601 format: " + isoString, e);
        }
    }
}
