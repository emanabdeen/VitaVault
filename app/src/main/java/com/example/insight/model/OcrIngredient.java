package com.example.insight.model;

import com.example.insight.utility.RestrictedIngredientsCategory;

import javax.annotation.Nullable;

public class OcrIngredient {
    private String ingredientName;

    private Boolean dietaryRestrictionFlagged = false;
    @Nullable
    private RestrictedIngredientsCategory matchedCategory;


    // For constructing a new OcrIngredient object for each ingredient parsed from OCR text
    public OcrIngredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public RestrictedIngredientsCategory getIngredientMatchedCategory() {
        if (matchedCategory != null) {
            return matchedCategory;
        } else {
            return null;
        }
    }
    public void setIngredientMatchedCategory(RestrictedIngredientsCategory matchedCategory) {
            this.matchedCategory = matchedCategory;
    }

    public String getIngredientName() {
        return ingredientName;
    }
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Boolean getDietaryRestrictionFlagged() {
        return dietaryRestrictionFlagged;
    }
    public void setDietaryRestrictionFlagged(Boolean matchedDietaryRestriction) {
        this.dietaryRestrictionFlagged = matchedDietaryRestriction;
    }
}
