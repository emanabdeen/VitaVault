package com.example.insight.model;

import java.time.LocalDate;

public class VitalRecord {

    private int dayOfMonth; // Represents the day of the month (1-31)
    private Float measure1; // Can be null if empty
    private Float measure2; // Can be null if empty

    // Constructor
    public VitalRecord(int dayOfMonth, Float measure1, Float measure2) {
        this.dayOfMonth = dayOfMonth;
        this.measure1 = measure1;
        this.measure2 = measure2;
    }

    public VitalRecord(int dayOfMonth, Float measure1) {
        this.dayOfMonth = dayOfMonth;
        this.measure1 = measure1;
    }

    // Getters and Setters
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Float getMeasure1() {
        return measure1;
    }

    public void setMeasure1(Float measure1) {
        this.measure1 = measure1;
    }

    public Float getMeasure2() {
        return measure2;
    }

    public void setMeasure2(Float measure2) {
        this.measure2 = measure2;
    }
}
