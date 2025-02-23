package com.example.insight.model;

public class DietaryResrtictionIngredient {

    private String ingredientId;
    private String ingredientName;
    private String ingredientCategory;

    public String getIngredientCategory() {
        return ingredientCategory;
    }

    public void setIngredientCategory(String ingredientCategory) {
        this.ingredientCategory = ingredientCategory;
    }

    public DietaryResrtictionIngredient(){

    }

    public DietaryResrtictionIngredient(String ingredientName, String category){
        this.ingredientName = ingredientName;
        this.ingredientCategory = category == null ? "Custom" : category;
    }


    //getters & Setters


    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
