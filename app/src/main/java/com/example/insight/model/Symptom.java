package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Symptom {
    private LocalDate recordDate;
    private LocalTime startTime; // Stores time in HH:mm format
    private LocalTime endTime;   // Stores time in HH:mm format
    private String symptomName;
    private String symptomLevel;
    private String symptomDescription;

    // Default constructor: sets recordDate to today, startTime & endTime to now
    public Symptom() {
        this.recordDate = LocalDate .now(); // Sets to current date
        this.startTime = LocalTime.now(); // Sets to current time
        this.endTime = LocalTime.now(); // Default to now
        this.symptomName = ""; //will be updated by subclass
        this.symptomLevel = "";
        this.symptomDescription = "";
    }

    // Constructor with custom values no description is required
    public Symptom(LocalDate recordDate, LocalTime startTime, LocalTime endTime, String symptomName, String symptomLevel) {
        this.recordDate = recordDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.symptomName = symptomName;
        this.symptomLevel = symptomLevel;
        this.symptomDescription = "";
    }

    // Constructor with custom values
    public Symptom(LocalDate recordDate, LocalTime startTime, LocalTime endTime, String symptomName, String symptomLevel, String symptomDescription) {
        this.recordDate = recordDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.symptomName = symptomName;
        this.symptomLevel = symptomLevel;
        this.symptomDescription = symptomDescription;
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
}

