package com.example.insight.utility;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TimeValidator {

    // Define the time pattern as a constant
    private static final String TIME_PATTERN = "HH:mm";
    private static final String TAG = "TimeValidator"; // Tag for logging

    /**
     * Validates if the input string is a valid time in the format "HH:mm".
     *
     * @param time The string to validate.
     * @return true if the time is valid, false otherwise.
     */
    public static boolean isValidTime(String time) {
        // Regex to match the format HH:mm
        String regex = "^\\d{2}:\\d{2}$";
        if (time == null || !time.matches(regex)) {
            return false;
        }

        // Validate the actual time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        try {
            LocalTime.parse(time, formatter); // Try to parse the time
            return true; // Time is valid
        } catch (DateTimeParseException e) {
            return false; // Time is invalid
        }
    }

    /**
     * Converts a string to a LocalTime object using the pattern "HH:mm".
     *
     * @param timeStr The string to convert.
     * @return The parsed LocalTime, or null if the input is invalid or null.
     */
    public static LocalTime StringToLocalTime(String timeStr) {
        // Use isValidTime to validate the input
        if (!isValidTime(timeStr)) {
            Log.e(TAG, "Error: Input string is invalid or null.");
            return null;
        }

        try {
            // Parse the string to LocalTime using the specified pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
            LocalTime time = LocalTime.parse(timeStr, formatter);
            return time;
        } catch (DateTimeParseException e) {
            // Handle parsing errors
            Log.e(TAG, "Error parsing time: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a LocalTime object to a formatted string using the pattern "HH:mm".
     *
     * @param time The LocalTime object to convert.
     * @return The formatted time string, or null if the input time is null.
     */
    public static String LocalTimeToString(LocalTime time) {
        // Handle null input
        if (time == null) {
            Log.e(TAG, "Error: Input time is null.");
            return null;
        }

        // Format the LocalTime to a string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        return time.format(formatter);
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
