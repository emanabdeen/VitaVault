package com.example.insight.model;

import com.example.insight.utility.RestrictedIngredientsCategory;

import java.io.Serializable;

import javax.annotation.Nullable;

public class OcrIngredient implements Serializable {
    private String ingredientName;

    private Boolean dietaryRestrictionFlagged = false;
    @Nullable
    private String matchedCategory;


    // For constructing a new OcrIngredient object for each ingredient parsed from OCR text
    public OcrIngredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public OcrIngredient(String ingredientName, Boolean dietaryRestrictionFlagged, String matchedCategory) {
        this.ingredientName = ingredientName;
        this.dietaryRestrictionFlagged = dietaryRestrictionFlagged;
        this.matchedCategory = matchedCategory;
    }

    @Override
    public String toString() {
        return "OcrIngredient{" +
                "ingredientName= " + ingredientName +
                ", dietaryRestrictionFlagged= " + dietaryRestrictionFlagged +
                ", matchedCategory= " + matchedCategory;
    }


    // Getters and setters
    public String getIngredientMatchedCategory() {
        if (matchedCategory != null) {
            return matchedCategory;
        } else {
            return null;
        }
    }
    public void setIngredientMatchedCategory(String matchedCategory) {
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
