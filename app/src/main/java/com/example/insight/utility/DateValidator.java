package com.example.insight.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class DateValidator {

    public static boolean isValidDate(String date) {
        // Regex to match the format 00-00-0000
        String regex = "^\\d{2}-\\d{2}-\\d{4}$";
        if (date == null || !date.matches(regex)) {
            return false;
        }

        // Validate the actual date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false); // Disable lenient parsing (strict validation)
        try {
            sdf.parse(date); // Try to parse the date
            return true; // Date is valid
        } catch (ParseException e) {
            return false; // Date is invalid
        }
    }

    public static void main(String[] args) {
        // Test cases
//        String date1 = "12-05-2023"; // Valid
//        String date2 = "31-02-2023"; // Invalid (February doesn't have 31 days)
//        String date3 = "00-00-0000"; // Invalid (invalid date)
//        String date4 = "12/05/2023"; // Invalid (wrong format)
//        String date5 = "123-05-2023"; // Invalid (extra digit)
//
//        System.out.println(date1 + " is valid: " + isValidDate(date1)); // true
//        System.out.println(date2 + " is valid: " + isValidDate(date2)); // false
//        System.out.println(date3 + " is valid: " + isValidDate(date3)); // false
//        System.out.println(date4 + " is valid: " + isValidDate(date4)); // false
//        System.out.println(date5 + " is valid: " + isValidDate(date5)); // false
    }

}