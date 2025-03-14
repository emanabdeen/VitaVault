package com.example.insight.utility;

import android.util.Log;

import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.model.OcrIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class IngredientUtils {
    private static final String TAG = "IngredientUtils";

    public static HashMap<String, ArrayList<String>> splitIngredientList(String inputText) {
        if (inputText == null || inputText.trim().isEmpty()) {
            Log.d(TAG,"InputText is null or empty: " + inputText);
            Log.e(TAG, "splitIngredientList: input text is null or empty");
            return new HashMap<String, ArrayList<String>>(); // Return an empty list if the input is null or empty
        }
        ArrayList<String> ingredientsList = new ArrayList<String>();
        ArrayList<String> mayContainList = new ArrayList<String>();
        ArrayList<String> containsList = new ArrayList<String>();
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
        HashMap<String, String> sanitizationReplaceMap = new HashMap<>();
        String emptyString = "";
        sanitizationReplaceMap.put("ingredients", emptyString);
        sanitizationReplaceMap.put("may contain", emptyString);
        sanitizationReplaceMap.put("contains", emptyString);
        sanitizationReplaceMap.put(":", emptyString);
        sanitizationReplaceMap.put("|", emptyString);
        sanitizationReplaceMap.put(";", emptyString);
        sanitizationReplaceMap.put("(", ", ");
        sanitizationReplaceMap.put(")", emptyString);
        sanitizationReplaceMap.put(".", ", ");
        sanitizationReplaceMap.put("\n", " ");

        // Sanitize colons and semicolons
        for (String key : sanitizationReplaceMap.keySet()) {
            ingredientsInputText = ingredientsInputText.replace(key, sanitizationReplaceMap.get(key));
            mayContainInputText = mayContainInputText.replace(key, sanitizationReplaceMap.get(key));
            containsInputText = containsInputText.replace(key, sanitizationReplaceMap.get(key));
        }

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
            if (!ingredient.trim().isEmpty()) {
                Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
                ingredientsList.add(ingredient);
            } else {
                Log.d(TAG, "splitIngredientList: ingredient is empty, skipping");
            }
        }
        for (String ingredient : splitMayContainInputText) {
            if (!ingredient.trim().isEmpty()) {
                Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
                mayContainList.add(ingredient);
            } else {
                Log.d(TAG, "splitIngredientList: ingredient is empty, skipping");
            }
        }
        for (String ingredient : splitContainsInputText) {
            if (!ingredient.trim().isEmpty()) {
                Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
                containsList.add(ingredient);
            } else {
                Log.d(TAG, "splitIngredientList: ingredient is empty, skipping");
            }
        }
        HashMap<String, ArrayList<String>> ingredientsListMap = new HashMap<String, ArrayList<String>>();
        ingredientsListMap.put("ingredients", ingredientsList);
        ingredientsListMap.put("may_contain", mayContainList);
        ingredientsListMap.put("contains", containsList);
        return ingredientsListMap;
    }

    public static HashMap<String, ArrayList<OcrIngredient>> parseIngredientsMapToOcrIngredients(HashMap<String, ArrayList<String>> ingredientsListMap) {
        HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsListMap = new HashMap<String, ArrayList<OcrIngredient>>();
        ArrayList<OcrIngredient> ingredientsList = new ArrayList<OcrIngredient>();
        ArrayList<OcrIngredient> mayContainList = new ArrayList<OcrIngredient>();
        ArrayList<OcrIngredient> containsList = new ArrayList<OcrIngredient>();

        for (String ingredient : ingredientsListMap.get("ingredients")) {
            ingredientsList.add(new OcrIngredient(ingredient));
        }
        for (String ingredient : ingredientsListMap.get("may_contain")) {
            mayContainList.add(new OcrIngredient(ingredient));
        }
        for (String ingredient : ingredientsListMap.get("contains")) {
            containsList.add(new OcrIngredient(ingredient));
        }

        ocrIngredientsListMap.put("ingredients", ingredientsList);
        ocrIngredientsListMap.put("may_contain", mayContainList);
        ocrIngredientsListMap.put("contains", containsList);
        return ocrIngredientsListMap;
    }

//    private void sortMatchedIngredients(ArrayList<OcrIngredient> matchedIngredientsList) {
//        Collections.sort(matchedIngredientsList, new Comparator<OcrIngredient>() {
//            @Override
//            public int compare(OcrIngredient o1, OcrIngredient o2) {
//
//            }
//        });
//    }

    public static ArrayList<OcrIngredient> getMatchedIngredients(HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsListMap, List<DietaryRestrictionIngredient> dietaryRestrictionIngredientsList) {
        ArrayList<OcrIngredient> matchedIngredientsList = new ArrayList<>();
        ArrayList<String> ingredientsListNames = new ArrayList<>();
        ArrayList<String> flaggedIngredientsList = new ArrayList<>();
        for(OcrIngredient ocrIngredient : ocrIngredientsListMap.get("ingredients")) {
            for(DietaryRestrictionIngredient dietaryRestrictionIngredient : dietaryRestrictionIngredientsList) {
                OcrIngredient scannedIngredient = ocrIngredient;
                if(ocrIngredient.getIngredientName().toLowerCase().contains(dietaryRestrictionIngredient.getIngredientName().toLowerCase())) {
                    scannedIngredient.setDietaryRestrictionFlagged(true);
                    scannedIngredient.setIngredientMatchedCategory(dietaryRestrictionIngredient.getIngredientCategory());
                }
                if (!ingredientsListNames.contains(scannedIngredient.getIngredientName())) { // Only add ingredient to list if it wasn't already added
                    matchedIngredientsList.add(scannedIngredient);
                    if (scannedIngredient.isDietaryRestrictionFlagged()) {
                        flaggedIngredientsList.add(scannedIngredient.getIngredientName());
                        ingredientsListNames.add(scannedIngredient.getIngredientName());
                    }
                    else {
                        ingredientsListNames.add(scannedIngredient.getIngredientName());
                    }
                }
            }
        }
        for(OcrIngredient ocrIngredient : ocrIngredientsListMap.get("may_contain")) {
            for(DietaryRestrictionIngredient dietaryRestrictionIngredient : dietaryRestrictionIngredientsList) {
                OcrIngredient scannedIngredient = ocrIngredient;
                if(ocrIngredient.getIngredientName().toLowerCase().contains(dietaryRestrictionIngredient.getIngredientName().toLowerCase())) {
                    scannedIngredient.setDietaryRestrictionFlagged(true);
                    scannedIngredient.setIngredientMatchedCategory(dietaryRestrictionIngredient.getIngredientCategory());
                }
                if (!ingredientsListNames.contains(scannedIngredient.getIngredientName())) {
                    matchedIngredientsList.add(scannedIngredient);
                    if (scannedIngredient.isDietaryRestrictionFlagged()) {
                        flaggedIngredientsList.add(scannedIngredient.getIngredientName());
                        ingredientsListNames.add(scannedIngredient.getIngredientName());
                    }
                    else {
                        ingredientsListNames.add(scannedIngredient.getIngredientName());
                    }
                }

            }
        }
        for(OcrIngredient ocrIngredient : ocrIngredientsListMap.get("contains")) {
            for(DietaryRestrictionIngredient dietaryRestrictionIngredient : dietaryRestrictionIngredientsList) {
                OcrIngredient scannedIngredient = ocrIngredient;
                if(ocrIngredient.getIngredientName().toLowerCase().contains(dietaryRestrictionIngredient.getIngredientName().toLowerCase())) {
                    scannedIngredient.setDietaryRestrictionFlagged(true);
                    scannedIngredient.setIngredientMatchedCategory(dietaryRestrictionIngredient.getIngredientCategory());
                }
                if (!ingredientsListNames.contains(scannedIngredient.getIngredientName())) {
                    matchedIngredientsList.add(scannedIngredient);
                    if (scannedIngredient.isDietaryRestrictionFlagged()) {
                        flaggedIngredientsList.add(scannedIngredient.getIngredientName());
                        ingredientsListNames.add(scannedIngredient.getIngredientName());
                    }
                    else {
                        ingredientsListNames.add(scannedIngredient.getIngredientName());
                    }
                }
            }
        }
        matchedIngredientsList.sort((o1, o2) -> Boolean.compare(o1.isDietaryRestrictionFlagged(), o2.isDietaryRestrictionFlagged()));
        Collections.reverse(matchedIngredientsList);

        return matchedIngredientsList;
    }
}
