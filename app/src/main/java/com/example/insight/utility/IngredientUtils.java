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

    private static final String INGREDIENTS = "ingredients";
    private static final String MAY_CONTAIN = "may contain";
    private static final String CONTAINS = "contains";

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

        if (trimmedLowerInputText.contains(INGREDIENTS) || trimmedLowerInputText.contains("ingedients") || trimmedLowerInputText.contains("imgredients")
        || trimmedLowerInputText.contains("inqredients") || trimmedLowerInputText.contains("ngredients")) {
            if (trimmedLowerInputText.contains("ingedients") || trimmedLowerInputText.contains("imgredients") || trimmedLowerInputText.contains("inqredients")
            || trimmedLowerInputText.contains("ngredients")) {
                trimmedLowerInputText = trimmedLowerInputText.replace("ingedients", "ingredients");
                trimmedLowerInputText = trimmedLowerInputText.replace("imgredients", "ingredients");
                trimmedLowerInputText = trimmedLowerInputText.replace("inqredients", "ingredients");
                trimmedLowerInputText = trimmedLowerInputText.replace("ngredients", "ingredients");
            }
            ingredientsInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf(INGREDIENTS));
            Log.d(TAG, "Ingredients string detected, carving out");
        } else {
            ingredientsInputText = trimmedLowerInputText;
        }
        if (trimmedLowerInputText.contains(MAY_CONTAIN)) {
            mayContainInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf(MAY_CONTAIN));
            ingredientsInputText = ingredientsInputText.replace(mayContainInputText, "");
            Log.d(TAG, "May contain string detected, carving out then cleaning it from ingredients string");
        }
        if (trimmedLowerInputText.contains(CONTAINS)) {
            containsInputText = trimmedLowerInputText.substring(trimmedLowerInputText.indexOf(CONTAINS));
            mayContainInputText = mayContainInputText.replace(containsInputText, "");
            ingredientsInputText = ingredientsInputText.replace(containsInputText, "");
            Log.d(TAG, "Contains string detected, carving out then cleaning it from ingredients string");
        }
        if (trimmedLowerInputText.contains("ingrédients") ){
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


        HashMap<String, String> ocrCommonTypoReplaceMap = new HashMap<>();
        // A few common typos and misspellings from the OCR
        ocrCommonTypoReplaceMap.put("0il", "oil");
        ocrCommonTypoReplaceMap.put("eniched", "enriched");

        // Sanitize colons and semicolons
        for (String key : sanitizationReplaceMap.keySet()) {
            ingredientsInputText = ingredientsInputText.replace(key, sanitizationReplaceMap.get(key));
            mayContainInputText = mayContainInputText.replace(key, sanitizationReplaceMap.get(key));
            containsInputText = containsInputText.replace(key, sanitizationReplaceMap.get(key));
        }

        for (String key : ocrCommonTypoReplaceMap.keySet()) {
            ingredientsInputText = ingredientsInputText.replace(key, ocrCommonTypoReplaceMap.get(key));
            mayContainInputText = mayContainInputText.replace(key, ocrCommonTypoReplaceMap.get(key));
            containsInputText = containsInputText.replace(key, ocrCommonTypoReplaceMap.get(key));
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
                if (ingredient.startsWith(" and")) {
                    ingredient = ingredient.replace(" and", emptyString);
                }
                if (ingredient.contains(" and ")) {
                    Log.d(TAG, "ingredient found with AND, splitting ingredient into two ingredients: " + ingredient);
                    String[] splitIngredient = ingredient.split(" and ");
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + splitIngredient[0]);
                    ingredientsList.add(splitIngredient[0].trim());
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + splitIngredient[1]);
                    ingredientsList.add(splitIngredient[1].trim());
                } else {
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
                    ingredientsList.add(ingredient.trim());
                }
            } else {
                Log.d(TAG, "splitIngredientList: ingredient is empty, skipping");
            }
        }
        for (String ingredient : splitMayContainInputText) {
            if (!ingredient.trim().isEmpty()) {
                if (ingredient.startsWith(" and")) {
                    ingredient = ingredient.replace(" and", emptyString);
                }
                if (ingredient.contains(" and ")) {
                    Log.d(TAG, "ingredient found with AND, splitting ingredient into two ingredients: " + ingredient);
                    String[] splitIngredient = ingredient.split(" and ");
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + splitIngredient[0]);
                    mayContainList.add(splitIngredient[0].trim());
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + splitIngredient[1]);
                    mayContainList.add(splitIngredient[1].trim());
                } else {
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
                    mayContainList.add(ingredient.trim());
                }
            } else {
                Log.d(TAG, "splitIngredientList: ingredient is empty, skipping");
            }
        }
        for (String ingredient : splitContainsInputText) {
            if (!ingredient.trim().isEmpty()) {
                if (ingredient.startsWith(" and")) {
                    ingredient = ingredient.replace(" and", emptyString);
                }
                if (ingredient.contains(" and ")) {
                    Log.d(TAG, "ingredient found with AND, splitting ingredient into two ingredients: " + ingredient);
                    String[] splitIngredient = ingredient.split(" and ");
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + splitIngredient[0]);
                    containsList.add(splitIngredient[0]);
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + splitIngredient[1]);
                    containsList.add(splitIngredient[1]);
                } else {
                    Log.d(TAG, "splitIngredientList: adding ingredient - " + ingredient);
                    containsList.add(ingredient.trim());
                }
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

        // Label common restrictions even if they don't match
        matchedIngredientsList = findCommonRestrictions(matchedIngredientsList);

        return matchedIngredientsList;
    }

    public static ArrayList<OcrIngredient> findCommonRestrictions(ArrayList<OcrIngredient> ingredientsList) {
        ArrayList<OcrIngredient> commonIngredientsLabeledList = new ArrayList<>();
        for(OcrIngredient ingredient : ingredientsList) {
            for(CommonRestrictedIngredients restrictedIngredient : CommonRestrictedIngredients.values()) {
                if (restrictedIngredient.getIngredientDescription().toLowerCase().equals(ingredient.getIngredientName())) {
                    ingredient.setIngredientMatchedCategory(restrictedIngredient.getCategory().getCategoryDescription());
                    Log.d(TAG, "Item is a common ingredient, marking and hiding button");
                    ingredient.setSameNameAsCommonRestrictedIngredient(true);
                    break;
                } else {
                    ingredient.setSameNameAsCommonRestrictedIngredient(false);
                }
            }
            commonIngredientsLabeledList.add(ingredient);
        }
        return commonIngredientsLabeledList;
    }
}
