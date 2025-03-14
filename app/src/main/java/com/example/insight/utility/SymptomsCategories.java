package com.example.insight.utility;

public enum SymptomsCategories {
    HEADACHE("Headache"),
    COUGHING("Coughing"),
    NAUSEA("Nausea"),
    RASH("Rash"),
    CHEST_PAIN("Chest Pain"),
    SORE_THROAT("Sore Throat"),
    JOIN_PAIN("Join Pain"),
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
