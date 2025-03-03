package com.example.insight.utility;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IngredientUtils {
    private static final String TAG = "IngredientUtils";

    // List of ingredients
    private ArrayList<String> ingredientList;

    // Dictionary of ingredients matched
    private HashMap<String, String> matchedIngredients;


    public IngredientUtils() {
        this.ingredientList = new ArrayList<String>();
        this.matchedIngredients = new HashMap<String, String>();
    }

    public void addIngredient(String ingredient) {
        this.ingredientList.add(ingredient);
    }

    public ArrayList<String> getIngredientList() {
        return this.ingredientList;
    }

    public ArrayList<String> splitIngredientList(String inputText) {
        if (inputText == null || inputText.trim().isEmpty()) {
            Log.d(TAG,"InputText is null or empty: " + inputText);
            Log.e(TAG, "splitIngredientList: input text is null or empty");
            return new ArrayList<String>(); // Return an empty list if the input is null or empty
        }
        String trimmedLowerInputText = inputText.trim().toLowerCase();
        String ingredientsInputText = "";
        String mayContainInputText = "";
        String containsInputText = "";
        String frenchIngredients = "";

        if (trimmedLowerInputText.contains("ingredients")) {
            ingredientsInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf("ingredients"));
        }
        if (trimmedLowerInputText.contains("may contain")) {
            mayContainInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf("may contain"));
            ingredientsInputText = ingredientsInputText.replace(mayContainInputText, "");
        }
        if (ingredientsInputText.contains("contains")) {
            containsInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf("contains"));
            ingredientsInputText = ingredientsInputText.replace(containsInputText, "");
        }
        if (ingredientsInputText.contains("ingrédients")) { // sanitize french ingredients from string
            frenchIngredients = ingredientsInputText.substring(ingredientsInputText.indexOf("ingrédients"));
            ingredientsInputText = ingredientsInputText.replace(frenchIngredients, "");
        }
        if (!frenchIngredients.isEmpty()) {
            if (mayContainInputText.contains(frenchIngredients)) {
                mayContainInputText = mayContainInputText.replace(frenchIngredients, "");
            }
            if (containsInputText.contains(frenchIngredients)) {
                containsInputText = containsInputText.replace(frenchIngredients, "");
            }
        }

        // Sanitize colons and semicolons
        ingredientsInputText = ingredientsInputText.replace(":", "");
        ingredientsInputText = ingredientsInputText.replace(";", "");

        mayContainInputText = mayContainInputText.replace(":", "");
        mayContainInputText = mayContainInputText.replace(";", "");

        containsInputText = containsInputText.replace(":", "");
        containsInputText = containsInputText.replace(";", "");

        Log.d(TAG, "Ingredients Input Text: " + ingredientsInputText);
        Log.d(TAG, "May Contain Input Text: " + mayContainInputText);
        Log.d(TAG, "Contains Input Text: " + containsInputText);

        String[] splitIngredientsInputText;
        splitIngredientsInputText = ingredientsInputText.split(",");

        String[] splitMayContainInputText;
        splitMayContainInputText = mayContainInputText.split(",");
        Log.d(TAG, "splitMayContainInputText: " + splitMayContainInputText.toString());

        String[] splitContainsInputText;
        splitContainsInputText = containsInputText.split(",");
        Log.d(TAG, "splitContainsInputText: " + splitContainsInputText.toString());


        for (String ingredient : splitIngredientsInputText) {
            Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
            this.addIngredient(ingredient);
        }

        return this.ingredientList;
    }
}
