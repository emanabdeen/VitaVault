package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.insight.utility.TimeValidator;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.time.LocalTime;

@RunWith(Theories.class)
public class TimeValidatorTests {

    @DataPoints
    public static String[] invalidTimes() {
        return new String[] {"12:60", "9:30", "99:99" };
    }

    @Test
    public void isValidTime_useValidTime_returnsTrue() {
        // Arrange
        String time = "23:45";

        // Act
        boolean result = TimeValidator.isValidTime(time);

        //Assert
        assertTrue(result);
    }

    @Theory
    public void isValidTime_useInvalidTime_returnsFalse(String invalidTime) {
        System.out.println("Testing invalid time: " + invalidTime);
        // Act
        boolean result = TimeValidator.isValidTime(invalidTime);

        //Assert
        assertFalse(result);
    }

    @Test
    public void stringToLocalTime_UseValidTime_ReturnsLocalTime() {
        // Arrange
        String time = "23:45";
        LocalTime expected = LocalTime.of(23, 45);

        // Act
        LocalTime result = TimeValidator.StringToLocalTime(time);

        //Assert
        assertEquals(expected, result);
    }

    @Theory
    public void stringToLocalTime_UseInvalidTime_ReturnsNull(String invalidTime) {
        System.out.println("Testing invalid time: " + invalidTime);
        // Act
        LocalTime result = TimeValidator.StringToLocalTime(invalidTime);

        //Assert
        assertNull(result);
    }

    @Test
    public void stringToLocalTime_UseNullTime_ReturnsNull() {
        // Arrange
        String time = null;
        LocalTime expected = null;
        // Act
        LocalTime result = TimeValidator.StringToLocalTime(time);
        //Assert
        assertEquals(expected, result);
    }


    @Test
    public void localTimeToString_UseValidTime_ReturnsString() {
        // Arrange
        LocalTime time = LocalTime.of(23, 45);
        String expected = "23:45";

        // Act
        String result = TimeValidator.LocalTimeToString(time);

        //Assert
        assertEquals(expected, result);
    }

    @Test
    public void localTimeToString_UseNullTime_ReturnsNull() {
        // Arrange
        LocalTime time = null;
        String expected = null;
        // Act
        String result = TimeValidator.LocalTimeToString(time);
        //Assert
        assertEquals(expected, result);
    }

}
