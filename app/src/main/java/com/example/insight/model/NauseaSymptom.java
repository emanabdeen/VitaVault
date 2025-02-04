package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class NauseaSymptom extends Symptom {
    private String nauseaLevel; // mild, moderate, severe

    public NauseaSymptom(String nauseaLevel) {
        super();
        this.nauseaLevel = nauseaLevel;
    }

    public NauseaSymptom(LocalDate recordDate, LocalTime startTime, LocalTime endTime, String nauseaLevel) {
        super(recordDate, startTime, endTime);
        this.nauseaLevel = nauseaLevel;
    }

    public String getNauseaLevel() {
        return nauseaLevel;
    }

    public void setNauseaLevel(String nauseaLevel) {
        this.nauseaLevel = nauseaLevel;
    }
}

