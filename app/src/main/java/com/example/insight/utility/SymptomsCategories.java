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

    // Method to get the enum constant name by symptom category string
    public static String getEnumNameBySymptomCategory(String symptomCategory) {
        for (SymptomsCategories category : SymptomsCategories.values()) {
            if (category.symptomCategory.equalsIgnoreCase(symptomCategory)) {
                return category.name(); // Return the enum constant name
            }
        }
        return null; // Return null if no match is found
    }


}
