package com.example.insight.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public String getFormattedDate() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // fallback
        }
    }

    public String getFormattedTime() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // fallback
        }
    }
}