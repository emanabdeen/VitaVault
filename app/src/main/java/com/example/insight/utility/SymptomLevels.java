package com.example.insight.utility;

public enum SymptomLevels {
    NotPresented("NotPresented"),
    Mild("Mild"),
    Moderate("Moderate"),
    Severe ("Severe");

    private final String symptomLevel;

    // Constructor to set the string value for each enum constant
    SymptomLevels(String symptomLevel) {
        this.symptomLevel = symptomLevel;
    }

    // Getter to access the string value
    public String getSymptomLevel() {
        return symptomLevel;
    }

    // Custom method to convert a string to the corresponding enum constant
    public static SymptomLevels fromString(String text) {
        if (text != null) {
            for (SymptomLevels sl : SymptomLevels.values()) {
                if (sl.getSymptomLevel().equalsIgnoreCase(text.trim())) {
                    return sl;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant with text: " + text);
    }
}


