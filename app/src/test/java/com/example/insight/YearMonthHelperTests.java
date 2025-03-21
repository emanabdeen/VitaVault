package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.insight.utility.YearMonthHelper;

import org.junit.Test;

import java.time.Month;
import java.time.YearMonth;

public class YearMonthHelperTests {

    @Test
    public void getNextYearMonthData_returnsNextYearMonth() {
        // Arrange
        YearMonth expected = YearMonth.of(2023, Month.FEBRUARY);

        // Act
        YearMonth actual = YearMonthHelper.getNextYearMonthData(YearMonth.of(2023, Month.JANUARY));

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getNextYearMonthData_useDecember_returnsJanuary() {
        // Arrange
        YearMonth expected = YearMonth.of(2023, Month.JANUARY);

        // Act
        YearMonth actual = YearMonthHelper.getNextYearMonthData(YearMonth.of(2022, Month.DECEMBER));

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getPreviousYearMonthData_returnsPreviousYearMonth() {
        // Arrange
        YearMonth expected = YearMonth.of(2023, Month.JANUARY);

        // Act
        YearMonth actual = YearMonthHelper.getPreviousYearMonthData(YearMonth.of(2023, Month.FEBRUARY));

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getPreviousYearMonthData_useJanuary_returnsDecember() {
        // Arrange
        YearMonth expected = YearMonth.of(2022, Month.DECEMBER);
        // Act
        YearMonth actual = YearMonthHelper.getPreviousYearMonthData(YearMonth.of(2023, Month.JANUARY));
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getLocalizedMonthName_returnsLocalizedMonthName() {
        // Arrange
        String expected = "February";
        YearMonth yearMonth = YearMonth.of(2023, Month.FEBRUARY);

        // Act
        String actual = YearMonthHelper.getLocalizedMonthName(yearMonth);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getPreviousYearMonthData_useNull_returnsNull() {
        // Arrange
        YearMonth yearMonth = null;
        // Act
        YearMonth actual = YearMonthHelper.getPreviousYearMonthData(yearMonth);
        // Assert
        assertNull(actual);
    }

    @Test
    public void getNextYearMonthData_useNull_returnsNull() {
        // Arrange
        YearMonth yearMonth = null;
        // Act
        YearMonth actual = YearMonthHelper.getNextYearMonthData(yearMonth);
        // Assert
        assertNull(actual);
    }

    @Test
    public void getLocalizedMonthName_useNull_returnsNull() {
        // Arrange
        YearMonth yearMonth = null;
        // Act
        String actual = YearMonthHelper.getLocalizedMonthName(yearMonth);
        // Assert
        assertNull(actual);
    }
}
