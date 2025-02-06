package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class CoughingSymptom extends Symptom {


    public CoughingSymptom(String symptomLevel) {
        super(); // Uses default date and time
        this.setSymptomName("Coughing");
        this.setSymptomLevel(symptomLevel);
    }

    public CoughingSymptom(String symptomLevel, String symptomDescription) {
        super();
        this.setSymptomName("Coughing");
        this.setSymptomLevel(symptomLevel);
        this.setSymptomDescription(symptomDescription);
    }

    public CoughingSymptom(LocalDate recordDate, LocalTime startTime,LocalTime endTime,String symptomLevel) {
        super(recordDate, startTime, endTime, "Coughing", symptomLevel);
    }

    public CoughingSymptom(LocalDate recordDate, LocalTime startTime,LocalTime endTime,String symptomLevel, String symptomDescription) {
        super(recordDate, startTime, endTime, "Coughing", symptomLevel, symptomDescription);
    }


}


