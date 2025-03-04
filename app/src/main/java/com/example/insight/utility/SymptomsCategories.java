package com.example.insight.utility;

public enum SymptomsCategories {
    HEADACHE("Headache"),
    COUGHING("Coughing"),
    NAUSEA("Nausea"),
    OTHER("Other");

    private final String symptomCategory;

    // Constructor to set the string value for each enum constant
    SymptomsCategories(String symptomCategory) {
        this.symptomCategory = symptomCategory;
    }

    // Getter to access the string value
    public String getSymptom() {
        return symptomCategory;
    }
}
