package com.example.insight.utility;

public enum VitalsCategories {

    HeartRate("HeartRate"),
    BloodPressure("BloodPressure"),
    BodyTemperature("BodyTemperature"),
    Weight ("Weight ");

    private final String vitalCategory;

    // Constructor to set the string value for each enum constant
    VitalsCategories(String vitalCategory) {
        this.vitalCategory = vitalCategory;
    }

    // Getter to access the string value
    public String getVital() {
        return vitalCategory;
    }

    // Custom method to convert a string to the corresponding enum constant
    public static VitalsCategories fromString(String text) {
        if (text != null) {
            for (VitalsCategories vc : VitalsCategories.values()) {
                if (vc.getVital().equalsIgnoreCase(text.trim())) {
                    return vc;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant with text: " + text);
    }

    // Method to check if a string is a valid enum value
    public static boolean isValidVitalCategory(String text) {
        if (text != null) {
            for (VitalsCategories vc : VitalsCategories.values()) {
                if (vc.getVital().equalsIgnoreCase(text.trim())) {
                    return true; // The string is a valid enum value
                }
            }
        }
        return false; // The string is not a valid enum value
    }


}
