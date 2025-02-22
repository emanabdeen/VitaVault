package com.example.insight.utility;

public enum Unites {

    BPM("BPM"),
    mmHg("mmHg"),
    Celsius("Celsius"),
    Fahrenheit ("Fahrenheit"),
    Kilograms("Kilograms"),
    Pounds ("Pounds"),;

    private final String unit;

    // Constructor to set the string value for each enum constant
    Unites(String unit) {
        this.unit = unit;
    }

    // Getter to access the string value
    public String getUnit() {
        return unit;
    }

    // Custom method to convert a string to the corresponding enum constant
    public static Unites fromString(String text) {
        if (text != null) {
            for (Unites u : Unites.values()) {
                if (u.getUnit().equalsIgnoreCase(text.trim())) {
                    return u;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant with text: " + text);
    }
}
