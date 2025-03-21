package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.insight.utility.StringHandler;

import org.junit.Test;

public class StringHandlerTests {

    @Test
    public void testDefaultIfNull_UseNullInput_ExpectEmptyString() {
        // Arrange
        String input = null;
        String expected = "";
        // Act
        String actual = StringHandler.defaultIfNull(input);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testDefaultIfNull_UseNonNullInput_ExpectSameString() {
        // Arrange
        String input = "Hello";
        String expected = "Hello";
        // Act
        String actual = StringHandler.defaultIfNull(input);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testDefaultIfNull_UseNullObject_ExpectEmptyString() {
        // Arrange
        Object input = null;
        String expected = "";
        // Act
        String actual = StringHandler.defaultIfNull(input);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testDefaultIfNull_UseNonNullObject_ExpectSameString() {
        // Arrange
        Object input = "Hello";
        String expected = "Hello";
        // Act
        String actual = StringHandler.defaultIfNull(input);
        // Assert
        assertEquals(expected, actual);
    }


    @Test
    public void testTrim_UseStringWithWhitespace_ExpectTrimmedString() {
        // Arrange
        String input = "  Hello  ";
        String expected = "Hello";
        // Act
        String actual = StringHandler.trim(input);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testTrim_UseNullInput_ExpectEmptyString() {
        // Arrange
        String input = null;
        String expected = "";
        // Act
        String actual = StringHandler.trim(input);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNullOrEmpty_UseNullInput_ExpectReturnTrue(){
        // Arrange
        String input = null;

        // Act
        boolean actual = StringHandler.isNullOrEmpty(input);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testIsNullOrEmpty_UseEmptyString_ExpectReturnTrue(){
        // Arrange
        String input = "";

        // Act
        boolean actual = StringHandler.isNullOrEmpty(input);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testIsNullOrEmpty_UseNonNullOrEmptyString_ExpectReturnFalse(){
        // Arrange
        String input = "Hello";

        // Act
        boolean actual = StringHandler.isNullOrEmpty(input);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testCapitalizeFirstLetter_UseNullInput_ExpectEmptyString(){
        // Arrange
        String input = null;
        String expected = "";
        // Act
        String actual = StringHandler.capitalizeFirstLetter(input);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testCapitalizeFirstLetter_UseNonNullInput_ExpectCapitalizedString(){
        // Arrange
        String input = "hello";
        String expected = "Hello";
        // Act
        String actual = StringHandler.capitalizeFirstLetter(input);
        // Assert
        assertEquals(expected, actual);
    }
}
