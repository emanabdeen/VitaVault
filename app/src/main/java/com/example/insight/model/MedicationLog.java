package com.example.insight.model;

public class MedicationLog {
    private String dosage;
    private String status;
    private String timestamp;
    private String logId;

    public MedicationLog() {}

    public MedicationLog(String dosage, String status, String timestamp) {
        this.dosage = dosage;
        this.status = status;
        this.timestamp = timestamp;
    }

    public MedicationLog(String logId, String dosage, String status, String timestamp) {
        this.logId = logId;
        this.dosage = dosage;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getLogId(){ return logId; }
    public void setLogId(String logId) { this.logId = logId; }
}