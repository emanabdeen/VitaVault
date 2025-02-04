package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Symptom {
    private LocalDate recordDate;   // Stores the date
    private LocalTime startTime; // Stores time in HH:mm format
    private LocalTime endTime;   // Stores time in HH:mm format

    // Default constructor: sets recordDate to today, startTime & endTime to now
    public Symptom() {
        this.recordDate = LocalDate .now(); // Sets to current date
        this.startTime = LocalTime.now(); // Sets to current time
        this.endTime = LocalTime.now(); // Default to now (can be updated)
    }

    // Constructor with custom values
    public Symptom(LocalDate  recordDate, LocalTime startTime, LocalTime endTime) {
        this.recordDate = recordDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public LocalDate  getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate  recordDate) {
        this.recordDate = recordDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}

