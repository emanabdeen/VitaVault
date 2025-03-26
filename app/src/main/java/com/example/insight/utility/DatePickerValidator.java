package com.example.insight.utility;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

public class DatePickerValidator implements CalendarConstraints.DateValidator {

    public DatePickerValidator() {
        // Default constructor
    }

    @Override
    public boolean isValid(long date) {
        // Only allow dates up to today
        return date <= System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // No data to write
    }

    // ✅ Required CREATOR field to fix warning
    public static final Parcelable.Creator<DatePickerValidator> CREATOR =
            new Parcelable.Creator<DatePickerValidator>() {
                @Override
                public DatePickerValidator createFromParcel(Parcel source) {
                    return new DatePickerValidator();
                }

                @Override
                public DatePickerValidator[] newArray(int size) {
                    return new DatePickerValidator[size];
                }
            };
}
