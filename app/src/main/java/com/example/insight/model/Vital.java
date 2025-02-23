package com.example.insight.model;

import com.example.insight.utility.Unites;
import com.example.insight.utility.VitalsCategories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Vital {

    private String vitalId;
    private LocalDate recordDate;
    private LocalTime recordTime; // Stores time in HH:mm format
    private String vitalType;
    private String measurement1;
    private String measurement2;
    private String unit;

//    private String heartRate_BPM;
//    private String bloodPressure_Systolic;
//    private String bloodPressure_Diastolic;
//    private String bodyTemperature_C;
//    private String bodyTemperature_F;
//    private String weight_Kg;
//    private String weight_lb;

    public Vital() {
    }

    public Vital(LocalDate recordDate, LocalTime recordTime, String vitalType, String unit) {
        this.recordDate = recordDate;
        this.recordTime = recordTime;
        this.vitalType = vitalType;
        this.unit = unit;
    }



    public Vital(String vitalType, String unit) {
        this.recordDate = LocalDate .now(); // Sets to current date
        this.recordTime = LocalTime.now(); // Sets to current time
        this.vitalType = vitalType;
        this.unit = unit;
    }


    public String getVitalType() {
        return vitalType;
    }

    public void setVitalType(String vitalType) {
        this.vitalType = vitalType;
    }

    public LocalTime getRecordTime() {
        return recordTime;
    }

    public String getRecordTimeStr() {
        String recordTimeStr;
        if (recordDate != null) {
            recordTimeStr = recordDate.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            recordTimeStr="";
        }
        return recordTimeStr;
    }

    public void setRecordTime(LocalTime recordTime) {
        this.recordTime = recordTime;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public String getRecordDateStr() {
        String recordDateStr;
        if (recordDate != null) {
            recordDateStr = recordDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } else {
            recordDateStr="";
        }

        return recordDateStr;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public String getVitalId() {
        return vitalId;
    }

    public void setVitalId(String vitalId) {
        this.vitalId = vitalId;
    }

    public String getMeasurement1() {
        return measurement1;
    }

    public void setMeasurement1(String measurement1) {
        this.measurement1 = measurement1;
    }

    public String getMeasurement2() {
        return measurement2;
    }

    public void setMeasurement2(String measurement2) {
        this.measurement2 = measurement2;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

//    public String getWeight_lb() {
//        return weight_lb;
//    }
//
//    public void setWeight_lb(String weight_lb) {
//        this.weight_lb = weight_lb;
//    }
//
//    public String getWeight_Kg() {
//        return weight_Kg;
//    }
//
//    public void setWeight_Kg(String weight_Kg) {
//        this.weight_Kg = weight_Kg;
//    }
//
//    public String getBodyTemperature_F() {
//        return bodyTemperature_F;
//    }
//
//    public void setBodyTemperature_F(String bodyTemperature_F) {
//        this.bodyTemperature_F = bodyTemperature_F;
//    }
//
//    public String getBodyTemperature_C() {
//        return bodyTemperature_C;
//    }
//
//    public void setBodyTemperature_C(String bodyTemperature_C) {
//        this.bodyTemperature_C = bodyTemperature_C;
//    }
//
//    public String getBloodPressure_Diastolic() {
//        return bloodPressure_Diastolic;
//    }
//
//    public void setBloodPressure_Diastolic(String bloodPressure_Diastolic) {
//        this.bloodPressure_Diastolic = bloodPressure_Diastolic;
//    }
//
//    public String getBloodPressure_Systolic() {
//        return bloodPressure_Systolic;
//    }
//
//    public void setBloodPressure_Systolic(String bloodPressure_Systolic) {
//        this.bloodPressure_Systolic = bloodPressure_Systolic;
//    }
//
//    public String getHeartRate_BPM() {
//        return heartRate_BPM;
//    }
//
//    public void setHeartRate_BPM(String heartRate_BPM) {
//        this.heartRate_BPM = heartRate_BPM;
//    }


}
