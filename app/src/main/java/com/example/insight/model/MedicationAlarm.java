package com.example.insight.model;

public class MedicationAlarm {
    private String alarmId;
    private String medicationId;
    private String day;
    private String time;       // e.g., "08:00 AM" or use a Date/Time type if preferred
    private String medicationName; // New field
    private String dosage;

    // Default constructor (needed for some serialization/deserialization libraries)
    public MedicationAlarm() {
    }

    // Parameterized constructor
    public MedicationAlarm(String alarmId, String medicationId, String day, String time) {
        this.alarmId = alarmId;
        this.medicationId = medicationId;
        this.day = day;
        this.time = time;
    }


    //for alarm rescheduling, has name and dosage
    public MedicationAlarm(String alarmId, String medicationId, String day, String time, String medicationName, String dosage) {
        this.alarmId = alarmId;
        this.medicationId = medicationId;
        this.day = day;
        this.time = time;
        this.medicationName = medicationName;
        this.dosage = dosage;
    }

    // Getters and Setters
    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    @Override
    public String toString() {
        return "MedicationAlarm{" +
                "alarmId='" + alarmId + '\'' +
                ", medicationId='" + medicationId + '\'' +
                ", time='" + time + '\''  +
                '}';
    }

}
