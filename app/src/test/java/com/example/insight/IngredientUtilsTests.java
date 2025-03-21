package com.example.insight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.example.insight.model.OcrIngredient;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.IngredientUtils;
import com.example.insight.utility.RestrictedIngredientsCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientUtilsTests {
    @Test
    public void splitIngredientsList_UseValidData_ReturnsCorrectSplitList(){

        // Arrange
        String ingredientsString = "Ingredients: Milk, Eggs, Flour, Onions, Tomatoes, Peanutbutter, Wheat, Corn. May contain: Wheat. Contains: Corn.";

        // Act
        HashMap<String, ArrayList<String>> ingredientsListMap = IngredientUtils.splitIngredientList(ingredientsString);
        ArrayList<String> ingredientsList = ingredientsListMap.get("ingredients");
        ArrayList<String> mayContainList = ingredientsListMap.get("may_contain");
        ArrayList<String> containsList = ingredientsListMap.get("contains");

        // Assert
        assertEquals(8, ingredientsList.size());
        assertTrue(ingredientsList.contains("milk"));
        assertTrue(ingredientsList.contains("eggs"));
        assertTrue(ingredientsList.contains("flour"));
        assertTrue(ingredientsList.contains("onions"));
        assertTrue(ingredientsList.contains("tomatoes"));
        assertTrue(ingredientsList.contains("peanutbutter"));
        assertTrue(ingredientsList.contains("wheat"));
        assertTrue(ingredientsList.contains("corn"));
        assertEquals(1, mayContainList.size());
        assertTrue(mayContainList.contains("wheat"));
        assertEquals(1, containsList.size());
        assertTrue(containsList.contains("corn"));
    }

    @Test
    public void splitIngredientsList_UseEmptyData_ReturnsEmptyList(){
        // Arrange
        String ingredientsString = "";
        // Act
        HashMap<String, ArrayList<String>> ingredientsListMap = IngredientUtils.splitIngredientList(ingredientsString);

        // Assert
        assertEquals(0, ingredientsListMap.size());
        assertEquals(new HashMap<String, ArrayList<String>>() , ingredientsListMap);
    }

    @Test
    public void parseSplitIngredientsIntoOcrIngredients_UseValidData_ReturnsCorrectOcrIngredientsList(){
        // Arrange
        HashMap<String, ArrayList<String>> ingredientsListMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> ingredientsList = new ArrayList<String>();
        ArrayList<String> mayContainList = new ArrayList<String>();
        ArrayList<String> containsList = new ArrayList<String>();
        ingredientsList.add("milk");
        ingredientsList.add("eggs");
        ingredientsList.add("flour");
        ingredientsList.add("onions");
        ingredientsList.add("tomatoes");
        ingredientsList.add("peanutbutter");
        ingredientsList.add("wheat");
        ingredientsList.add("corn");
        mayContainList.add("wheat");
        containsList.add("corn");

        ingredientsListMap.put("ingredients", ingredientsList);
        ingredientsListMap.put("may_contain", mayContainList);
        ingredientsListMap.put("contains", containsList);


        // Act
        HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsListMap = IngredientUtils.parseIngredientsMapToOcrIngredients(ingredientsListMap);
        ArrayList<OcrIngredient> ingredients = ocrIngredientsListMap.get("ingredients");
        ArrayList<OcrIngredient> mayContain = ocrIngredientsListMap.get("may_contain");
        ArrayList<OcrIngredient> contains = ocrIngredientsListMap.get("contains");


        // Assert
        assertEquals(8, ingredients.size());
        assertEquals("milk", ingredients.get(0).getIngredientName());
        assertEquals("eggs", ingredients.get(1).getIngredientName());
        assertEquals("flour", ingredients.get(2).getIngredientName());
        assertEquals("onions", ingredients.get(3).getIngredientName());
        assertEquals("tomatoes", ingredients.get(4).getIngredientName());
        assertEquals("peanutbutter", ingredients.get(5).getIngredientName());
        assertEquals("wheat", ingredients.get(6).getIngredientName());
        assertEquals("corn", ingredients.get(7).getIngredientName());

        assertEquals(1, mayContain.size());
        assertEquals("wheat", mayContain.get(0).getIngredientName());

        assertEquals(1, contains.size());
        assertEquals("corn", contains.get(0).getIngredientName());
    }

    @Test
    public void parseSplitIngredientsIntoOcrIngredients_UseEmptyData_ReturnsEmptyList(){
        // Arrange
        HashMap<String, ArrayList<String>> ingredientsListMap = new HashMap<String, ArrayList<String>>();

        // Act
        HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsListMap = IngredientUtils.parseIngredientsMapToOcrIngredients(ingredientsListMap);

        // Assert
        assertEquals(0, ocrIngredientsListMap.size());
        assertEquals(new HashMap<String, ArrayList<OcrIngredient>>() , ocrIngredientsListMap);
    }

    @Test
    public void findCommonRestrictions_UseValidData_ReturnsCorrectMatchedIngredientsList(){
        // Arrange (construct ingredients hashmap from common restricted)
        HashMap<String, ArrayList<String>> ingredientsListMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> ingredientsList = new ArrayList<String>();
        Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> commonRestrictedIngredientsMap = CommonRestrictedIngredients.GetAllIngredientsWithCategory();
        commonRestrictedIngredientsMap.forEach((category, ingredients) -> {
            ingredients.forEach(ingredient -> {
                ingredientsList.add(ingredient.getIngredientDescription());
            });
        });
        ingredientsListMap.put("ingredients", ingredientsList);


        // Act (parse map into OCRIngredients list and find common restrictions)
        HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsListMap = IngredientUtils.parseIngredientsMapToOcrIngredients(ingredientsListMap);
        ArrayList<OcrIngredient> ingredients = ocrIngredientsListMap.get("ingredients");
        ArrayList<OcrIngredient> commonRestrictionsLabeledIngredientsList = IngredientUtils.findCommonRestrictions(ingredients);

        // Assert (make sure each ingredient is marked with its category)
        for(OcrIngredient ingredient : commonRestrictionsLabeledIngredientsList) {
            String ingredientName = ingredient.getIngredientName();
            String ingredientCategory = ingredient.getIngredientMatchedCategory();
            String categoryName = CommonRestrictedIngredients.valueOf(ingredientName.toUpperCase()).getCategory().getCategoryDescription();
            assertEquals(ingredientCategory, categoryName);
        }
    }
}
