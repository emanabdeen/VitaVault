package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class CoughingSymptom extends Symptom {
    private String coughLevel; // mild, moderate, severe

    public CoughingSymptom(String coughLevel) {
        super(); // Uses default date and time
        this.coughLevel = coughLevel;
    }

    public CoughingSymptom(LocalDate recordDate, LocalTime startTime, LocalTime endTime, String coughLevel) {
        super(recordDate, startTime, endTime);
        this.coughLevel = coughLevel;
    }

    public String getCoughLevel() {
        return coughLevel;
    }

    public void setCoughLevel(String coughLevel) {
        this.coughLevel = coughLevel;
    }
}


