package com.example.insight.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;


public enum CommonRestrictedIngredients {


    CHEESE("Cheese",RestrictedIngredientsCategory.DAIRY),
    BUTTER("Butter",RestrictedIngredientsCategory.DAIRY),
    YOGURT("Yogurt",RestrictedIngredientsCategory.DAIRY),
    OAT("Oat",RestrictedIngredientsCategory.GLUTEN),
    BARLEY("Barley",RestrictedIngredientsCategory.GLUTEN),
    RYE("Rye",RestrictedIngredientsCategory.GLUTEN),
    FAT("Fat",RestrictedIngredientsCategory.PORK),
    BACON("Bacon",RestrictedIngredientsCategory.PORK),
    PORK("Pork",RestrictedIngredientsCategory.PORK),
    HAM("Ham",RestrictedIngredientsCategory.PORK),
    SAUSAGE("Sausage",RestrictedIngredientsCategory.PORK),
    SHRIMP("shrimp",RestrictedIngredientsCategory.SHELLFISH),
    LOBSTER("Lobster",RestrictedIngredientsCategory.SHELLFISH),
    CRAB("Crab",RestrictedIngredientsCategory.SHELLFISH),
    BEEF("Beef",RestrictedIngredientsCategory.MEATS),
    CHICKEN("Chicken",RestrictedIngredientsCategory.MEATS),
    MILK("Milk",RestrictedIngredientsCategory.DAIRY),
    PEANUT("Peanut",RestrictedIngredientsCategory.NUTS),
    CASHEW("Cashew",RestrictedIngredientsCategory.NUTS),
    WALNUT("Walnut",RestrictedIngredientsCategory.NUTS),
    ALMOND("Almond",RestrictedIngredientsCategory.NUTS),
    WHEAT("Wheat",RestrictedIngredientsCategory.GLUTEN),
    EGG("Egg",RestrictedIngredientsCategory.OTHER);

    private final String description;
    private final RestrictedIngredientsCategory category;


    //constructor
    CommonRestrictedIngredients(String description, RestrictedIngredientsCategory category){

        this.description = description;
        this.category = category;
    }


    public String getIngredientDescription(){
        return  description;
    }

    public RestrictedIngredientsCategory getCategory() {
        return category;
    }


    public static  Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> GetAllIngredientsWithCategory(){


        Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> ingredientCategoryMap = new LinkedHashMap<>();

        for (CommonRestrictedIngredients ingredient : CommonRestrictedIngredients.values()) {
            // Get the category of the ingredient

            if (!ingredientCategoryMap.containsKey(ingredient.getCategory())) {
                ingredientCategoryMap.put(ingredient.getCategory(), new ArrayList<>());
            }
            // Add the ingredient to the list for its category
            ingredientCategoryMap.get(ingredient.getCategory()).add(ingredient);


           //Sorting Categories
            List<RestrictedIngredientsCategory> categories = new ArrayList<>(ingredientCategoryMap.keySet());
           Collections.sort(categories);

            //Sorting map in alphabetical order
            for (RestrictedIngredientsCategory category : categories) {
                List<CommonRestrictedIngredients> items = ingredientCategoryMap.get(category);
                items.sort(new Comparator<CommonRestrictedIngredients>() {
                    @Override
                    public int compare(CommonRestrictedIngredients item1, CommonRestrictedIngredients item2) {
                        return item1.name().compareTo(item2.name()); // Compare item names alphabetically
                    }
                });
            }
        }
        return ingredientCategoryMap;
    }


    public static  List<CommonRestrictedIngredients> GetIngredientsByCategory(){

      List<CommonRestrictedIngredients> ingredientList = new ArrayList<>();

        for (CommonRestrictedIngredients ingredient : CommonRestrictedIngredients.values()) {


            if (!ingredientList.contains(ingredient)){
                ingredientList.add(ingredient);
            }

        }
        return ingredientList;
    }




    public static  Map<String, List<String>> GetAllIngredientsWithCategoryStrings(){

        Map<String, List<String>> ingredientCategoryMap = new HashMap<>();

        for (CommonRestrictedIngredients ingredient : CommonRestrictedIngredients.values()) {
            // Get the category of the ingredient

            if (!ingredientCategoryMap.containsKey(ingredient.getCategory().getCategoryDescription())) {
                ingredientCategoryMap.put(ingredient.getCategory().getCategoryDescription(), new ArrayList<>());
            }
            // Add the ingredient to the list for its category
            ingredientCategoryMap.get(ingredient.getCategory().getCategoryDescription()).add(ingredient.description);
        }
        return ingredientCategoryMap;
    }


    public static List<Object> getFlatennedList() {
        List<Object> flattenedList = new ArrayList<>();

        Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> ingredientMap = GetAllIngredientsWithCategory();

        for (Map.Entry<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> entry : ingredientMap.entrySet()) {
            RestrictedIngredientsCategory category = entry.getKey();
            flattenedList.add(category);  // Add the category first
            flattenedList.addAll(entry.getValue());  // Add all items under the category
        }

        return flattenedList;
    }

}
