package com.example.insight;

import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Intent;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.insight.utility.LoginRegisterHelper;
import com.example.insight.view.Login;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void validPassword_IsValidPassword(){
        // Arrange
        String testPassword = "AAbbcc123!";
        Boolean expected = true;

        // Act
        Boolean actual = LoginRegisterHelper.validPassword(testPassword);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void invalidPassword_IsInvalidPassword(){
        // Arrange
        String testPassword = "abc123";
        Boolean expected = false;

        // Act
        Boolean actual = LoginRegisterHelper.validPassword(testPassword);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void checkRegistrationMessage_SuccessfulMessage_TitleGoneMessageVisible(){
        Intent registerIntent = new Intent();
        registerIntent.putExtra("registerSuccess",true);
        //TextView title = new TextView();

        //LoginRegisterHelper.checkRegistrationMessage(registerIntent, title, successMessage);
    }
}