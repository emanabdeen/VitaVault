package com.example.insight.model;


public class Medication {

    private String medicationId;
    private String name;
    private String dosage;
    private String unit;

    public Medication() {
        this.medicationId = generateId();
    }

    public Medication(String name, String dosage, String unit) {
        this.medicationId = generateId();
        setName(name);
        this.dosage = dosage;
        this.unit = unit;
    }

    // Generate Unique ID
    private String generateId() {
        return "MED_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            name = name.trim(); // Remove leading/trailing spaces
            // Capitalize first letter, lowercase the rest
            this.name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        } else {
            this.name = name;
        }
    }


    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }




}
