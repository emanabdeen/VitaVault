package com.example.insight.utility;

public final class StringHandler {

    /**
     * Returns the input string if it is not null, otherwise returns an empty string.
     *
     * @param input The string to validate.
     * @return The input string if it is not null, otherwise an empty string.
     */
    public static String defaultIfNull(String input) {
        return input != null ? input : "";
    }

    /**
     * Returns an empty string if the input object is null, otherwise returns the object's string representation.
     *
     * @param input The object to check.
     * @return An empty string if the input is null, otherwise the object's string representation.
     */
    public static String defaultIfNull(Object input) {
        return input != null ? input.toString() : "";
    }

    /**
     * Trims whitespace from the input string. If the input is null, returns an empty string.
     *
     * @param input The string to trim.
     * @return The trimmed string, or an empty string if the input is null.
     */
    public static String trim(String input) {
        return input != null ? input.trim() : "";
    }

    /**
     * Checks if the input string is null or empty (after trimming).
     *
     * @param input The string to check.
     * @return true if the string is null or empty, otherwise false.
     */
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    /**
     * Capitalizes the first letter of the input string.
     *
     * @param input The string to capitalize.
     * @return The capitalized string, or an empty string if the input is null.
     */
    public static String capitalizeFirstLetter(String input) {
        if (isNullOrEmpty(input)) {
            return "";
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
