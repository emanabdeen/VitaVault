package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class OtherSymptom extends Symptom {
    private String description;

    public OtherSymptom(String symptomLevel) {
        super();
        this.setSymptomName("Other");
        this.setSymptomLevel(symptomLevel);
    }

    public OtherSymptom(String symptomLevel, String symptomDescription) {
        super();
        this.setSymptomName("Other");
        this.setSymptomLevel(symptomLevel);
        this.setSymptomDescription(symptomDescription);
    }
    public OtherSymptom(LocalDate recordDate, LocalTime startTime,LocalTime endTime,String symptomLevel) {
        super(recordDate, startTime, endTime, "Other", symptomLevel);
    }

    public OtherSymptom(LocalDate recordDate, LocalTime startTime,LocalTime endTime,String symptomLevel, String symptomDescription) {
        super(recordDate, startTime, endTime, "Other", symptomLevel, symptomDescription);
    }
}


