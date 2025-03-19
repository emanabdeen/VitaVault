package com.example.insight;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.RestrictedIngredientsCategory;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RestrictedIngredientsTests {

    @Test
    public void getCategoryList_ReturnsListOfCategories_ExpectTrue(){
        // Arrange
        List<String> expected = new ArrayList<>();
        expected.add(RestrictedIngredientsCategory.DAIRY.getCategoryDescription());
        expected.add(RestrictedIngredientsCategory.GLUTEN.getCategoryDescription());
        expected.add(RestrictedIngredientsCategory.MEATS.getCategoryDescription());
        expected.add(RestrictedIngredientsCategory.NUTS.getCategoryDescription());
        expected.add(RestrictedIngredientsCategory.OTHER.getCategoryDescription());
        expected.add(RestrictedIngredientsCategory.PORK.getCategoryDescription());
        expected.add(RestrictedIngredientsCategory.SHELLFISH.getCategoryDescription());
        // Act
        List<String> actual = RestrictedIngredientsCategory.getCategoryList();
        // Assert
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void getIngredientsByCategory_ReturnsListOfIngredients_ExpectTrue() {
        // Arrange
        List<CommonRestrictedIngredients> expected = new ArrayList<>();
        expected.add(CommonRestrictedIngredients.CHEESE);
        expected.add(CommonRestrictedIngredients.BUTTER);
        expected.add(CommonRestrictedIngredients.YOGURT);
        expected.add(CommonRestrictedIngredients.OAT);
        expected.add(CommonRestrictedIngredients.BARLEY);
        expected.add(CommonRestrictedIngredients.RYE);
        expected.add(CommonRestrictedIngredients.FAT);
        expected.add(CommonRestrictedIngredients.BACON);
        expected.add(CommonRestrictedIngredients.PORK);
        expected.add(CommonRestrictedIngredients.HAM);
        expected.add(CommonRestrictedIngredients.SAUSAGE);
        expected.add(CommonRestrictedIngredients.SHRIMP);
        expected.add(CommonRestrictedIngredients.LOBSTER);
        expected.add(CommonRestrictedIngredients.CRAB);
        expected.add(CommonRestrictedIngredients.BEEF);
        expected.add(CommonRestrictedIngredients.CHICKEN);
        expected.add(CommonRestrictedIngredients.MILK);
        expected.add(CommonRestrictedIngredients.PEANUT);
        expected.add(CommonRestrictedIngredients.CASHEW);
        expected.add(CommonRestrictedIngredients.WALNUT);
        expected.add(CommonRestrictedIngredients.ALMOND);
        expected.add(CommonRestrictedIngredients.WHEAT);
        expected.add(CommonRestrictedIngredients.EGG);
        // Act
        List<CommonRestrictedIngredients> actual = CommonRestrictedIngredients.GetIngredientsByCategory();
        // Assert
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void getCategoryDescription_ReturnsCategoryDescription_ExpectTrue(){
        // Arrange
        String expected = "Dairy";
        // Act
        String actual = RestrictedIngredientsCategory.DAIRY.getCategoryDescription();
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getIngredientDescription_ReturnsIngredientDescription_ExpectTrue() {
        // Arrange
        String expected = "Cheese";
        // Act
        String actual = CommonRestrictedIngredients.CHEESE.getIngredientDescription();
        // Assert
        assertEquals(expected, actual);
    }

    @Test //TODO: Consider whether to actually bother testing this. The only way to test it is by relying on transforming things from other tests or hardcoding the expected result.
    public void getAllIngredientsWithCategory_ReturnsMapWithAllIngredientsAndCategories_ExpectTrue(){
//        // Arrange
//        List<String> categoryList = RestrictedIngredientsCategory.getCategoryList();
//        List<List<CommonRestrictedIngredients>> ingredientLists = new ArrayList<>();
//        for (String category : categoryList) {
//            ingredientLists.add(CommonRestrictedIngredients.GetIngredientsByCategory());
//        }
//        List<CommonRestrictedIngredients> ingredientList = CommonRestrictedIngredients.GetIngredientsByCategory();
//
//
//        // Act
//        Map<RestrictedIngredientsCategory, List<CommonRestrictedIngredients>> actualMap = CommonRestrictedIngredients.GetAllIngredientsWithCategory();
//
//        // Assert
//        actualMap.forEach((category, ingredients) -> {
//           //Boolean categoryExists = RestrictedIngredientsCategory.getCategoryList();
//        });
    }
}
