package com.example.insight.utility;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateValidator {

    // Define the date pattern as a constant
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TAG = "DateValidator"; // Tag for logging

    /**
     * Validates if the input string is a valid date in the format "yyyy-MM-dd".
     *
     * @param date The string to validate.
     * @return true if the date is valid, false otherwise.
     */
    public static boolean isValidDate(String date) {
        // Regex to match the format 00-00-0000
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (date == null || !date.matches(regex)) {
            return false;
        }

        // Validate the actual date
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setLenient(false); // Disable lenient parsing (strict validation)
        try {
            sdf.parse(date); // Try to parse the date
            return true; // Date is valid
        } catch (ParseException e) {
            return false; // Date is invalid
        }
    }

    /**
     * Converts a string to a LocalDate object using the pattern "yyyy-MM-dd".
     *
     * @param dateStr The string to convert.
     * @return The parsed LocalDate, or null if the input is invalid or null.
     */
    public static LocalDate StringToLocalDate(String dateStr) {
        // Use isValidDate to validate the input
        if (!isValidDate(dateStr)) {
            Log.e(TAG, "Error: Input string is invalid or null.");
            return null;
        }

        try {
            // Parse the string to LocalDate using the specified pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
            LocalDate date = LocalDate.parse(dateStr, formatter);
            return date;
        } catch (DateTimeParseException e) {
            // Handle parsing errors
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a LocalDate object to a formatted string using the pattern "yyyy-MM-dd".
     *
     * @param date The LocalDate object to convert.
     * @return The formatted date string, or null if the input date is null.
     */
    public static String LocalDateToString(LocalDate date) {
        // Handle null input
        if (date == null) {
            Log.e(TAG, "Error: Input date is null.");
            return null;
        }

        // Format the LocalDate to a string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return date.format(formatter);
    }

    public static void main(String[] args) {
        // Test cases
//        String date1 = "2023-05-12"; // Valid
//        String date2 = "2023-02-31"; // Invalid (February doesn't have 31 days)
//        String date3 = "0000-00-00"; // Invalid (invalid date)
//        String date4 = "2023/05/12"; // Invalid (wrong format)
//        String date5 = "2023-05-123"; // Invalid (extra digit)
//
//        System.out.println(date1 + " is valid: " + isValidDate(date1)); // true
//        System.out.println(date2 + " is valid: " + isValidDate(date2)); // false
//        System.out.println(date3 + " is valid: " + isValidDate(date3)); // false
//        System.out.println(date4 + " is valid: " + isValidDate(date4)); // false
//        System.out.println(date5 + " is valid: " + isValidDate(date5)); // false
    }

}