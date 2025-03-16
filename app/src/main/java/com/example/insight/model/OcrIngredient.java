package com.example.insight.model;

import java.io.Serializable;

import javax.annotation.Nullable;

public class OcrIngredient implements Serializable, Comparable<OcrIngredient> {
    private String ingredientName;

    private Boolean dietaryRestrictionFlagged = false;
    @Nullable
    private String matchedCategory;
    private Boolean addedAsCustom = false;
    private Boolean sameNameAsCommonRestrictedIngredient = false;


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
    public int compareTo(OcrIngredient ocrIngredient) {
        int matchedFlagCompare = 0;
        if (dietaryRestrictionFlagged && ocrIngredient.isDietaryRestrictionFlagged()) {
            matchedFlagCompare = ingredientName.compareTo(ocrIngredient.getIngredientName());
        } else {
            matchedFlagCompare =  dietaryRestrictionFlagged.compareTo(ocrIngredient.isDietaryRestrictionFlagged());
        }
        return matchedFlagCompare;
    }

    @Override
    public String toString() {
        return "OcrIngredient{" +
                "ingredientName= " + ingredientName +
                ", dietaryRestrictionFlagged= " + dietaryRestrictionFlagged +
                ", matchedCategory= " + matchedCategory +
                ", addedAsCustom= " + addedAsCustom +
                ", sameNameAsCommonRestrictedIngredient= " + sameNameAsCommonRestrictedIngredient +
                '}';
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

    public Boolean isDietaryRestrictionFlagged() {
        return dietaryRestrictionFlagged;
    }
    public void setDietaryRestrictionFlagged(Boolean matchedDietaryRestriction) {
        this.dietaryRestrictionFlagged = matchedDietaryRestriction;
    }

    public Boolean isAddedAsCustom() {
        return addedAsCustom;
    }

    public void setAddedAsCustom(Boolean addedAsCustom) {
        this.addedAsCustom = addedAsCustom;
    }

    public Boolean isSameNameAsCommonRestrictedIngredient() {
        return sameNameAsCommonRestrictedIngredient;
    }

    public void setSameNameAsCommonRestrictedIngredient(Boolean sameNameAsCommonRestrictedIngredient) {
        this.sameNameAsCommonRestrictedIngredient = sameNameAsCommonRestrictedIngredient;
    }
}
