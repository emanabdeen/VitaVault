package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Medication {

    private String medicationId;
    private String name;
    private String dosage;
    private String unit;

    public Medication() {
        this.medicationId = generateId();
    }

    public Medication(String name, String dosage, String unit, boolean reminderEnabled, boolean repeatWeekly) {
        this.medicationId = generateId();
        this.name = name;
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
        this.name = name;
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
