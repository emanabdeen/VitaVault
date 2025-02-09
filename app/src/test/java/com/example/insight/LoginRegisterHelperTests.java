package com.example.insight;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.insight.utility.LoginRegisterHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class LoginRegisterHelperTests {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void validatePassword_ValidPassword_ExpectTrue(){
        // Arrange
        String testPassword = "AAbbcc123!";
        Boolean expected = true;
        // Act
        Boolean actual = LoginRegisterHelper.validPassword(testPassword);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void validatePassword_InvalidPassword_ExpectFalse(){
        // Arrange
        String testPassword = "abc123";
        Boolean expected = false;
        // Act
        Boolean actual = LoginRegisterHelper.validPassword(testPassword);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void validateNewAndConfirmPassword_MatchingInputs_ExpectTrue(){
        // Arrange
        String testPassword = "AAbbcc123!";
        String testConfirmPassword = "AAbbcc12!";
        Boolean expected = true;
        LoginRegisterHelper lrh = new LoginRegisterHelper();
        // Act
        Boolean actual = lrh.validateNewPasswordMatchesConfirmPassword(testPassword, testConfirmPassword);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void validateNewAndConfirmPassword_NonMatchingInputs_ExpectFalse() {
        // Arrange
        String testPassword = "AAbbcc123!";
        String testConfirmPassword = "notMatchingPassword";
        Boolean expected = false;
        LoginRegisterHelper lrh = new LoginRegisterHelper();
        // Act
        Boolean actual = lrh.validateNewPasswordMatchesConfirmPassword(testPassword, testConfirmPassword);
        // Assert
        assertEquals(expected, actual);
    }

}