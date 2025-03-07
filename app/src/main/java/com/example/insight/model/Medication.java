package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Medication {

    private String name;
    private String dosage;
    private String unit;
    private LocalDate date;

    public Medication() {
    }

    public Medication(String name, String dosage, String unit, LocalDate date) {
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
        this.date = date;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Convert Date to String for UI
    public String getDateStr() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

}
