package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Symptom {
    private String symptomId;
    private LocalDate recordDate;
    private LocalTime startTime; // Stores time in HH:mm format
    private Optional<LocalTime> endTime; // Optional end time
    private String symptomName;
    private String symptomLevel;
    private String symptomDescription;

    // Default constructor: sets recordDate to today, startTime to now, and endTime to empty
    public Symptom() {
        this.recordDate = LocalDate.now(); // Sets to current date
        this.startTime = LocalTime.now(); // Sets to current time
        this.endTime = Optional.empty(); // No end time by default
        this.symptomName = ""; // Will be updated by subclass
        this.symptomLevel = "";
        this.symptomDescription = "";
    }

    // Constructor with symptomName and symptomLevel
    public Symptom(String symptomName, String symptomLevel) {
        this.recordDate = LocalDate.now(); // Sets to current date
        this.startTime = LocalTime.now(); // Sets to current time
        this.endTime = Optional.empty(); // No end time by default
        this.symptomName = symptomName;
        this.symptomLevel = symptomLevel;
        this.symptomDescription = "";
    }

    // Constructor with symptomName, symptomLevel, and symptomDescription
    public Symptom(String symptomName, String symptomLevel, String symptomDescription) {
        this.recordDate = LocalDate.now(); // Sets to current date
        this.startTime = LocalTime.now(); // Sets to current time
        this.endTime = Optional.empty(); // No end time by default
        this.symptomName = symptomName;
        this.symptomLevel = symptomLevel;
        this.symptomDescription = symptomDescription;
    }

    // Constructor with custom values (no description)
    public Symptom(LocalDate recordDate, LocalTime startTime, Optional<LocalTime> endTime, String symptomName, String symptomLevel) {
        this.recordDate = recordDate;
        this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
        this.endTime = endTime.map(time -> time.truncatedTo(ChronoUnit.MINUTES)); // Truncate if present
        this.symptomName = symptomName;
        this.symptomLevel = symptomLevel;
        this.symptomDescription = "";
    }

    // Constructor with custom values
    public Symptom(LocalDate recordDate, LocalTime startTime, Optional<LocalTime> endTime, String symptomName, String symptomLevel, String symptomDescription) {
        this.recordDate = recordDate;
        this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
        this.endTime = endTime.map(time -> time.truncatedTo(ChronoUnit.MINUTES)); // Truncate if present
        this.symptomName = symptomName;
        this.symptomLevel = symptomLevel;
        this.symptomDescription = symptomDescription;
    }

    // Getters and Setters
    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public LocalTime getStartTime() {
        return startTime.truncatedTo(ChronoUnit.MINUTES);
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Optional<LocalTime> getEndTime() {
        return endTime.map(time -> time.truncatedTo(ChronoUnit.MINUTES)); // Truncate if present
    }

    public void setEndTime(Optional<LocalTime> endTime) {
        this.endTime = endTime.map(time -> time.truncatedTo(ChronoUnit.MINUTES)); // Truncate if present
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public String getSymptomLevel() {
        return symptomLevel;
    }

    public void setSymptomLevel(String symptomLevel) {
        this.symptomLevel = symptomLevel;
    }

    public String getSymptomDescription() {
        return symptomDescription;
    }

    public void setSymptomDescription(String symptomDescription) {
        this.symptomDescription = symptomDescription;
    }

    public String getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(String symptomId) {
        this.symptomId = symptomId;
    }
}

