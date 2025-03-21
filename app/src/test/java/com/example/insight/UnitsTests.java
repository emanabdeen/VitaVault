package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.example.insight.utility.Units;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class UnitsTests {

    @DataPoints
    public static String[] validUnitStrings() {
        return new String[]{ "BPM", "mmHg", "Celsius", "Fahrenheit", "Kilograms", "Pounds" };
    }

    @Theory
    public void fromString_useValidUnitString_ReturnsCorrespondingEnum(String validUnitString) {
        System.out.println("Testing valid unit string: " + validUnitString);
        Units result = Units.fromString(validUnitString);
        System.out.println("Result: " + result);
        // Assert
        assert result != null;
        assertEquals(validUnitString, result.getUnit());
    }

    @Test
    public void fromString_useNullString_ThrowsIllegalArgumentException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> Units.fromString(null) );
    }
}
