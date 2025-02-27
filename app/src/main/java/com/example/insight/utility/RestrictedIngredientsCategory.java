package com.example.insight.utility;

import java.util.ArrayList;
import java.util.List;

public enum RestrictedIngredientsCategory {

    DAIRY("Diary"),
    GLUTEN("Gluten"),
    MEATS("Meats"),
    PORK("Pork"),
    SHELLFISH("Shellfish"),
    NUTS("Nuts"),
    OTHER("Other");

    private final String categoryDescription;

    // Constructor to associate a description with each category
    RestrictedIngredientsCategory(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }


    public String getCategoryDescription() {
        return categoryDescription;
    }

    public static List<String> getCategoryList() {
        List<String> categoryList = new ArrayList<>();

        for (RestrictedIngredientsCategory category : RestrictedIngredientsCategory.values()) {
            categoryList.add(category.getCategoryDescription());
        }

        return categoryList;
    }

}
