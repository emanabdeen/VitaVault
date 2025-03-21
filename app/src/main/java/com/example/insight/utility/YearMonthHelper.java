package com.example.insight.utility;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public final class YearMonthHelper {

//    public static class YearMonthData {
//        public int monthNumber;
//        public int year;
//        public String monthName;
//    }


    public static YearMonth getNextYearMonthData(YearMonth yearMonthDate) {
        if (yearMonthDate == null) {
            return null;
        }
        YearMonth nextYearMonth = yearMonthDate.plusMonths(1);

//        YearMonthData result = new YearMonthData();
//        result.monthNumber = nextYearMonth.getMonthValue();
//        result.year = nextYearMonth.getYear();
//        result.monthName = nextYearMonth.getMonth().toString();
        return nextYearMonth;
    }

    public static YearMonth getPreviousYearMonthData(YearMonth yearMonthDate) {
        if (yearMonthDate == null) {
            return null;
        }
        YearMonth previousYearMonth = yearMonthDate.minusMonths(1);

//        YearMonthData result = new YearMonthData();
//        result.monthNumber = previousYearMonth.getMonthValue();
//        result.year = previousYearMonth.getYear();
//        result.monthName = previousYearMonth.getMonth().toString();
        return previousYearMonth;
    }

    public static String getLocalizedMonthName(YearMonth yearMonthDate) {
        if (yearMonthDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        return yearMonthDate.atDay(1).format(formatter);
    }

}
