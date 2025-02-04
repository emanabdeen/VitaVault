package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class OtherSymptom extends Symptom {
    private String description;

    public OtherSymptom(String description) {
        super();
        this.description = description;
    }

    public OtherSymptom(LocalDate recordDate, LocalTime startTime, LocalTime endTime, String description) {
        super(recordDate, startTime, endTime);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


