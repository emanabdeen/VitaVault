package com.example.insight.utility;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TimeValidator {

    public static boolean isValidTime(String time) {
        // Regex to match the format HH:mm
        String regex = "^\\d{2}:\\d{2}$";
        if (time == null || !time.matches(regex)) {
            return false;
        }

        // Validate the actual time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime.parse(time, formatter); // Try to parse the time
            return true; // Time is valid
        } catch (DateTimeParseException e) {
            return false; // Time is invalid
        }
    }

    public static void main(String[] args) {
        // Test cases
//        String time1 = "23:45"; // Valid
//        String time2 = "12:60"; // Invalid (Minutes cannot be 60)
//        String time3 = "24:00"; // Invalid (Hours cannot be 24)
//        String time4 = "9:30";  // Invalid (Should be 09:30)
//        String time5 = "99:99"; // Invalid (Out of range)
//
//        System.out.println(time1 + " is valid: " + isValidTime(time1)); // true
//        System.out.println(time2 + " is valid: " + isValidTime(time2)); // false
//        System.out.println(time3 + " is valid: " + isValidTime(time3)); // false
//        System.out.println(time4 + " is valid: " + isValidTime(time4)); // false
//        System.out.println(time5 + " is valid: " + isValidTime(time5)); // false
    }
}
