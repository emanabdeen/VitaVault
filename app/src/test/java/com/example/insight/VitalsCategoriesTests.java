package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.example.insight.utility.VitalsCategories;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class VitalsCategoriesTests {

    @DataPoints
    public static String[] validVitalsCategoryStrings() {
        return new String[]{ "HeartRate", "BloodPressure", "BodyTemperature", "Weight" };
    }

    @Theory
    public void fromString_useValidVitalsCategoryString_ReturnsCorrespondingEnum(String validVitalsCategoryString) {
        System.out.println("Testing valid vitals category string: " + validVitalsCategoryString);
        VitalsCategories result = VitalsCategories.fromString(validVitalsCategoryString);
        System.out.println("Result: " + result);

        // Assert
        assert result != null;
        assertEquals(validVitalsCategoryString, result.getVital());
    }

    @Test
    public void fromString_useNullString_ThrowsIllegalArgumentException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> VitalsCategories.fromString(null) );
    }

}
