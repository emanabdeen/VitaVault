package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class NauseaSymptom extends Symptom {
    //private String nauseaLevel; // mild, moderate, severe

    public NauseaSymptom(String symptomLevel) {
        super();
        this.setSymptomName("Nausea");
        this.setSymptomLevel(symptomLevel);
    }

    public NauseaSymptom(String symptomLevel, String symptomDescription) {
        super();
        this.setSymptomName("Nausea");
        this.setSymptomLevel(symptomLevel);
        this.setSymptomDescription(symptomDescription);
    }

    public NauseaSymptom(LocalDate recordDate, LocalTime startTime,LocalTime endTime,String symptomLevel) {
        super(recordDate, startTime, endTime, "Nausea", symptomLevel);
    }

    public NauseaSymptom(LocalDate recordDate, LocalTime startTime,LocalTime endTime,String symptomLevel, String symptomDescription) {
        super(recordDate, startTime, endTime, "Nausea", symptomLevel, symptomDescription);
    }

}

