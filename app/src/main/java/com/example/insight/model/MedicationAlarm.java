package com.example.insight.model;

public class MedicationAlarm {
    private String alarmId;
    private String medicationId;
    private String day;
    private String time;       // e.g., "08:00 AM" or use a Date/Time type if preferred
    private String repeatInfo; // e.g., "Mon, Wed, Fri" or a more structured format
    private boolean enabled;

    // Default constructor (needed for some serialization/deserialization libraries)
    public MedicationAlarm() {
    }

    // Parameterized constructor
    public MedicationAlarm(String alarmId, String medicationId, String day, String time, String repeatInfo, boolean enabled) {
        this.alarmId = alarmId;
        this.medicationId = medicationId;
        this.day = day;
        this.time = time;
        this.repeatInfo = repeatInfo;
        this.enabled = enabled;
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

    public String getRepeatInfo() {
        return repeatInfo;
    }

    public void setRepeatInfo(String repeatInfo) {
        this.repeatInfo = repeatInfo;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "MedicationAlarm{" +
                "alarmId='" + alarmId + '\'' +
                ", medicationId='" + medicationId + '\'' +
                ", time='" + time + '\'' +
                ", repeatInfo='" + repeatInfo + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
