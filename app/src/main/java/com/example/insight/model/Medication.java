package com.example.insight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Medication {

    private String medicationId;
    private String name;
    private String dosage;
    private String unit;
    private boolean reminderEnabled;
    private HashMap<String, List<String>> reminderMap;

    public Medication() {
        this.medicationId = generateId();
    }

    public Medication(String name, String dosage, String unit, boolean reminderEnabled) {
        this.medicationId = generateId();
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
        this.reminderEnabled = reminderEnabled;
        this.reminderMap = new HashMap<>();
    }

    // Generate Unique ID
    private String generateId() {
        return "MED_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isReminderEnabled() { return reminderEnabled; }

    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }

    // Method to add a reminder
    public void addReminder(String day, String time) {
        reminderMap.computeIfAbsent(day, k -> new ArrayList<>()).add(time);
    }

    // Method to remove a reminder
    public void removeReminder(String day, String time) {
        List<String> times = reminderMap.get(day);
        if (times != null) {
            times.remove(time);
            if (times.isEmpty()) {
                reminderMap.remove(day); // Remove the day if no times remain
            }
        }
    }

    // Method to get reminders for a specific day
    public List<String> getReminders(String day) {
        return reminderMap.getOrDefault(day, new ArrayList<>());
    }

    // Method to display all reminders
    public void printReminders() {
        for (HashMap.Entry<String, List<String>> entry : reminderMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public HashMap<String, List<String>> getReminderMap() {
        return reminderMap;
    }

    public void setReminderMap(HashMap<String, List<String>> reminderMap) {
        this.reminderMap = reminderMap;
    }


}
