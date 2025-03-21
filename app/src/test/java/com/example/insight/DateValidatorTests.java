package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.insight.utility.DateValidator;
import com.example.insight.utility.TimeValidator;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.time.LocalDate;

@RunWith(Theories.class)
public class DateValidatorTests {

    @DataPoints
    public static String[] invalidDates() {
        return new String[] {"12-05-2023", "2023-02-31", "0000-00-00", "2023/05/12", "2023-05-123", "abcdefg"};
    }

    @Test
    public void isValidDate_useValidDate_returnsTrue() {
        // Arrange
        String date = "2023-05-12";

        // Act
        boolean result = DateValidator.isValidDate(date);

        //Assert
        assertTrue(result);
    }

    @Theory
    public void isValidDate_useInvalidDate_returnsFalse(String invalidDate) {
        System.out.println("Testing invalid date: " + invalidDate);
        // Act
        boolean result = DateValidator.isValidDate(invalidDate);

        //Assert
        assertFalse(result);
    }

    @Test
    public void isValidDate_useNullDate_returnsFalse() {
        // Arrange
        String date = null;
        // Act
        boolean result = DateValidator.isValidDate(date);
        //Assert
        assertFalse(result);
    }

    @Test
    public void stringToLocalDate_UseValidDate_ReturnsLocalDate() {
        // Arrange
        String date = "2023-05-12";
        LocalDate expected = LocalDate.of(2023, 5, 12);

        // Act
        LocalDate result = DateValidator.StringToLocalDate(date);

        //Assert
        assertEquals(expected, result);
    }

    @Theory
    public void stringToLocalDate_UseInvalidDate_ReturnsLocalDate(String invalidDate) {
        System.out.println("Testing invalid date: " + invalidDate);
        // Act
        LocalDate result = DateValidator.StringToLocalDate(invalidDate);

        //Assert
        assertNull(result);
    }

    @Test
    public void localDateToString_UseValidDate_ReturnsString() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 5, 12);
        String expected = "2023-05-12";

        // Act
        String result = DateValidator.LocalDateToString(date);

        //Assert
        assertEquals(expected, result);
    }

    @Test
    public void localDateToString_UseNullDate_ReturnsNull() {
        // Arrange
        LocalDate date = null;

        // Act
        String result = DateValidator.LocalDateToString(date);

        //Assert
        assertNull(result);
    }


}
