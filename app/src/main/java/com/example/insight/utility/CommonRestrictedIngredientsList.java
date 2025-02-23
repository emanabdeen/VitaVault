package com.example.insight.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CommonRestrictedIngredientsList {


    EGG("Egg",RestrictedIngredientsCategory.OTHER),
    BEEF("Beef",RestrictedIngredientsCategory.MEATS),
    CHICKEN("Chicken",RestrictedIngredientsCategory.MEATS),
    FAT("Fat",RestrictedIngredientsCategory.PORK),
    BACON("Bacon",RestrictedIngredientsCategory.PORK),
    PORK("Pork",RestrictedIngredientsCategory.PORK),
    HAM("Ham",RestrictedIngredientsCategory.PORK),
    SAUSAGE("Sausage",RestrictedIngredientsCategory.PORK),
    MILK("Milk",RestrictedIngredientsCategory.DAIRY),
    CHEESE("Cheese",RestrictedIngredientsCategory.DAIRY),
    BUTTER("Butter",RestrictedIngredientsCategory.DAIRY),
    YOGURT("Yogurt",RestrictedIngredientsCategory.DAIRY),
    PEANUT("Peanut",RestrictedIngredientsCategory.NUTS),
    CASHEW("Cashew",RestrictedIngredientsCategory.NUTS),
    WALNUT("WALNUT",RestrictedIngredientsCategory.NUTS),
    ALMOND("ALMOND",RestrictedIngredientsCategory.NUTS),
    WHEAT("Wheat",RestrictedIngredientsCategory.GLUTEN),
    OAT("Oat",RestrictedIngredientsCategory.GLUTEN),
    BARLEY("Barley",RestrictedIngredientsCategory.GLUTEN),
    RYE("Rye",RestrictedIngredientsCategory.GLUTEN),
    SHRIMP("shrimp",RestrictedIngredientsCategory.SHELLFISH),
    LOBSTER("Lobster",RestrictedIngredientsCategory.SHELLFISH),
    CRAB("Crab",RestrictedIngredientsCategory.SHELLFISH);

    private final String description;
    private final RestrictedIngredientsCategory category;


    //constructor
    CommonRestrictedIngredientsList(String description, RestrictedIngredientsCategory category){

        this.description = description;
        this.category = category;
    }


    public String GetIngredientDescription(){
        return  description;
    }

    public RestrictedIngredientsCategory getCategory() {
        return category;
    }

    public static  Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredientsList>> GetAllIngredientsWithCategory(){

        Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredientsList>> ingredientCategoryMap = new HashMap<>();

        for (CommonRestrictedIngredientsList ingredient : CommonRestrictedIngredientsList.values()) {
            // Get the category of the ingredient

            if (!ingredientCategoryMap.containsKey(ingredient.getCategory())) {
                ingredientCategoryMap.put(ingredient.getCategory(), new ArrayList<>());
            }
            // Add the ingredient to the list for its category
            ingredientCategoryMap.get(ingredient.getCategory()).add(ingredient);
        }
        return ingredientCategoryMap;
    }


    public static  List<CommonRestrictedIngredientsList> GetIngredientsByCategory(){

      List<CommonRestrictedIngredientsList> ingredientList = new ArrayList<>();

        for (CommonRestrictedIngredientsList ingredient : CommonRestrictedIngredientsList.values()) {


            if (!ingredientList.contains(ingredient)){
                ingredientList.add(ingredient);
            }

        }
        return ingredientList;
    }


}
