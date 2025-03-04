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
            Log.d(TAG, "Ingredients string detected, carving out");
        }
        if (trimmedLowerInputText.contains("may contain")) {
            mayContainInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf("may contain"));
            ingredientsInputText = ingredientsInputText.replace(mayContainInputText, "");
            Log.d(TAG, "May contain string detected, carving out then cleaning it from ingredients string");
        }
        if (trimmedLowerInputText.contains("contains")) {
            containsInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf("contains"));
            mayContainInputText = mayContainInputText.replace(containsInputText, "");
            ingredientsInputText = ingredientsInputText.replace(containsInputText, "");
            Log.d(TAG, "Contains string detected, carving out then cleaning it from ingredients string");
        }
        if (trimmedLowerInputText.contains("ingrédients") ){ //|| ingredientsInputText.contains("ingrêdients") || ingredientsInputText.contains("ingrëdients") || ingredientsInputText.contains("ingrèdients")) { // sanitize french ingredients from string
            frenchIngredients = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf("ingrédients"));
            ingredientsInputText = ingredientsInputText.replace(frenchIngredients, "");
            Log.d(TAG,"French ingredients string detected, cleaning it from strings");
        }
        if (!frenchIngredients.isEmpty()) {
            Log.d(TAG, "French ingredients string not empty, cleaning it from other strings");
            if (mayContainInputText.contains(frenchIngredients)) {
                mayContainInputText = mayContainInputText.replace(frenchIngredients, "");
                Log.d(TAG, "Removed french ingredients string from may contain string");
            }
            if (containsInputText.contains(frenchIngredients)) {
                containsInputText = containsInputText.replace(frenchIngredients, "");
                Log.d(TAG, "Removed french ingredients string from contains string");
            }
        }

        // Sanitize colons and semicolons
        ingredientsInputText = ingredientsInputText.replace("ingredients", "");
        ingredientsInputText = ingredientsInputText.replace(":", "");
        ingredientsInputText = ingredientsInputText.replace(";", "");
        ingredientsInputText = ingredientsInputText.replace(".", ",");
        ingredientsInputText = ingredientsInputText.replace("\n", " ");

        mayContainInputText = mayContainInputText.replace("may contain", "");
        mayContainInputText = mayContainInputText.replace(":", "");
        mayContainInputText = mayContainInputText.replace(";", "");
        mayContainInputText = mayContainInputText.replace(".", ",");
        mayContainInputText = mayContainInputText.replace("\n", " ");


        containsInputText = containsInputText.replace("contains", "");
        containsInputText = containsInputText.replace(":", "");
        containsInputText = containsInputText.replace(";", "");
        containsInputText = containsInputText.replace(".", ",");
        containsInputText = containsInputText.replace("\n", "");

        Log.d(TAG, "Ingredients Input Text: " + ingredientsInputText);
        Log.d(TAG, "May Contain Input Text: " + mayContainInputText);
        Log.d(TAG, "Contains Input Text: " + containsInputText);

        String[] splitIngredientsInputText;
        splitIngredientsInputText = ingredientsInputText.split(",");

        String[] splitMayContainInputText;
        splitMayContainInputText = mayContainInputText.split(",");
        Log.d(TAG, "splitMayContainInputText length: " + splitMayContainInputText.length);

        String[] splitContainsInputText;
        splitContainsInputText = containsInputText.split(",");
        Log.d(TAG, "splitContainsInputText length: " + splitContainsInputText.length);


        for (String ingredient : splitIngredientsInputText) {
            Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
            if (!ingredient.trim().isEmpty()) {
                this.addIngredient(ingredient);
            } else {
                Log.d(TAG, "splitIngredientList: ingredient is empty, skipping");
            }
        }

        return this.ingredientList;
    }
}
