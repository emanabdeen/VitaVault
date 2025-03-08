package com.example.insight.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.utility.RestrictedIngredientsCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class dietaryRestrictionIngredientViewModel extends ViewModel {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    //get the current user
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseUser currentUser = auth.getCurrentUser();
    public String uid = currentUser.getUid();
    final String  customCategory = "custom";

    //set the LiveData
    private final MutableLiveData<List<DietaryRestrictionIngredient>> customIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> customIngredientList = new ArrayList<>();
    private final MutableLiveData<List<DietaryRestrictionIngredient>> predefinedIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> predefinedIngredientList = new ArrayList<>();

    private final MutableLiveData<List<DietaryRestrictionIngredient>> diaryIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> diaryIngredientList = new ArrayList<>();

    private final MutableLiveData<List<DietaryRestrictionIngredient>> glutenIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> glutenIngredientList = new ArrayList<>();

    private final MutableLiveData<List<DietaryRestrictionIngredient>> porkIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> porkIngredientList = new ArrayList<>();

    private final MutableLiveData<List<DietaryRestrictionIngredient>> shellfishIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> shellfishIngredientList = new ArrayList<>();

    private final MutableLiveData<List<DietaryRestrictionIngredient>> meatsIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> meatsIngredientList = new ArrayList<>();

    private final MutableLiveData<List<DietaryRestrictionIngredient>> nutsIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> nutsIngredientList = new ArrayList<>();

    private final MutableLiveData<List<DietaryRestrictionIngredient>> otherIngredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> otherIngredientList = new ArrayList<>();



    //Constructor
    public dietaryRestrictionIngredientViewModel() {
        customIngredientLiveData.setValue(customIngredientList);
        predefinedIngredientLiveData.setValue(predefinedIngredientList);
        diaryIngredientLiveData.setValue(diaryIngredientList);
        glutenIngredientLiveData.setValue(glutenIngredientList);
        porkIngredientLiveData.setValue(porkIngredientList);
        shellfishIngredientLiveData.setValue(shellfishIngredientList);
        meatsIngredientLiveData.setValue(meatsIngredientList);
        nutsIngredientLiveData.setValue(nutsIngredientList);
        otherIngredientLiveData.setValue(otherIngredientList);

    }

    //Getter
    public LiveData<List<DietaryRestrictionIngredient>> getCustomIngredientsData() {
        Log.e("debug","liveData size:"+ customIngredientLiveData.getValue().size());
        return customIngredientLiveData;
    }

    public LiveData<List<DietaryRestrictionIngredient>> getPredefinedIngredientsData() {
        return predefinedIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getPorkIngredientLiveData() {
        return porkIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getGlutenIngredientLiveData() {
        return glutenIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getDiaryIngredientLiveData() {
        return diaryIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getShellfishIngredientLiveData() {
        return shellfishIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getMeatsIngredientLiveData() {
        return meatsIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getNutsIngredientLiveData() {
        return nutsIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getOtherIngredientLiveData() {
        return otherIngredientLiveData;
    }



    public void getCustomDietaryRestrictionIngredients() {

        customIngredientList = new ArrayList<>();

        CollectionReference dietaryRestrictions = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("dietaryRestrictions");

        Query query = dietaryRestrictions.whereEqualTo("ingredientCategory", customCategory);
                query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null && !querySnapshot.isEmpty()) {

                    // Retrieve the documents from the query result
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                            String category = document.getString("ingredientCategory");
                            String name = document.getString("ingredientName");

                        Log.d("AddCustomIngredientdebug", "Document ID: " + document.getId());
                        Log.d("AddCustomIngredientdebug", "Category: " + category);
                        Log.d("AddCustomIngredientdebug", "name: " + name);

                        // Create vital object with the retrieved data
                            DietaryRestrictionIngredient ingredient = new DietaryRestrictionIngredient(name,category);
                            ingredient.setIngredientId(document.getId());

                        Log.d("AddCustomIngredientdebug", ingredient.getIngredientName());


                        customIngredientList.add(ingredient);
                    }

                    customIngredientLiveData.postValue(customIngredientList);

                }else{
                    Log.e("debug","getting the list!! "+ String.valueOf(customIngredientList.size()));
                    customIngredientLiveData.postValue(customIngredientList);
                }
            } else {
                // Handle failure
                Log.e("Firestore", "Error retrieving documents: " + task.getException().getMessage());
                customIngredientLiveData.postValue(customIngredientList);
            }
        });


    }


    public void getPredefinedDietaryRestrictionIngredients() {

        CollectionReference dietaryRestrictions = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("dietaryRestrictions");

        Query query = dietaryRestrictions.whereNotEqualTo("ingredientCategory", customCategory);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                predefinedIngredientList = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null && !querySnapshot.isEmpty()) {

                    // Retrieve the documents from the query result
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String category = document.getString("ingredientCategory");
                        String name = document.getString("ingredientName");

                        Log.d("AddCustomIngredientdebug", "Document ID: " + document.getId());
                        Log.d("AddCustomIngredientdebug", "Category: " + category);
                        Log.d("AddCustomIngredientdebug", "name: " + name);

                        // Create vital object with the retrieved data
                        DietaryRestrictionIngredient ingredient = new DietaryRestrictionIngredient(name,category);
                        ingredient.setIngredientId(document.getId());

                        Log.d("AddCustomIngredientdebug", ingredient.getIngredientName());


                        predefinedIngredientList.add(ingredient);
                    }


                    predefinedIngredientLiveData.postValue(predefinedIngredientList);
                    updateSelectedItems();

                }else{
                    predefinedIngredientLiveData.postValue(null);
                }
            } else {
                // Handle failure
                Log.e("Firestore", "Error retrieving documents: " + task.getException().getMessage());
                predefinedIngredientLiveData.postValue(null);
            }
        });


    }


    public void addDietaryRestrictionIngredient(DietaryRestrictionIngredient ingredient) {

        try {
            DocumentReference addDocRef = db.collection("users")
                    .document(uid)
                    .collection("dietaryRestrictions")
                    .add(ingredient).getResult();

            String generatedId = addDocRef.getId(); //get the iD of the created document
            Log.d("debug", "Document added with ID: " + generatedId);

        } catch (Exception e) {
            Log.e("Firestore", "Error adding document: " + e.getMessage());
        }
    }

    public void updateDietaryRestrictionIngredient(DietaryRestrictionIngredient ingredient) {
        try {
            db.collection("users")
                    .document(uid)
                    .collection("dietaryRestrictions")
                    .document(ingredient.getIngredientId())
                    .update("ingredientName", ingredient.getIngredientName())
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error", "Error updating ingredient: " + e.getMessage());

                    });
        } catch (Exception e) {
            Log.e("Firestore", "Error updating document: " + e.getMessage());
        }
    }


    public void deleteDietaryRestrictionIngredient(String ingredientId){

        Log.e("AddCustomIngredient",ingredientId);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("dietaryRestrictions")
                .document(ingredientId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("debug", "Dietary Restriction  record is deleted successfully.");
                    //searchResultMessageData.postValue("vital record is deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error deleting : " + e.getMessage());
                });
    }

//loads all predefined ingredients into the viewModel
    public void loadCategoryLiveData(String category, List<DietaryRestrictionIngredient> ingredientList){

        if(category.equalsIgnoreCase(RestrictedIngredientsCategory.DAIRY.getCategoryDescription())){

            diaryIngredientList = ingredientList;


        }else if(category.equalsIgnoreCase(RestrictedIngredientsCategory.GLUTEN.getCategoryDescription())){

            glutenIngredientList = ingredientList;

        }else if(category.equalsIgnoreCase(RestrictedIngredientsCategory.PORK.getCategoryDescription())){

            porkIngredientList = ingredientList;

        }else if(category.equalsIgnoreCase(RestrictedIngredientsCategory.SHELLFISH.getCategoryDescription())){

            shellfishIngredientList = ingredientList;
        }else if(category.equalsIgnoreCase(RestrictedIngredientsCategory.MEATS.getCategoryDescription())){

            meatsIngredientList = ingredientList;

        }else if(category.equalsIgnoreCase(RestrictedIngredientsCategory.NUTS.getCategoryDescription())){

            nutsIngredientList = ingredientList;

        }else if(category.equalsIgnoreCase(RestrictedIngredientsCategory.OTHER.getCategoryDescription())){

            otherIngredientList = ingredientList;
        }else{
            Log.e("LoadIngredient","Unknown Category: "+category);
        }

    }

    private void updateSelectedItems(){

        for (DietaryRestrictionIngredient selectedIngredient: predefinedIngredientList ) {

           if(selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.DAIRY.getCategoryDescription())){

               diaryIngredientList.forEach( i -> {

                   if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())){
                       i.setSelected(true);
                   }else{
                       i.setSelected(false);
                   }

                   Log.e("DietLoad", i.getIngredientName() + " is selected: " +i.getIsSelected());
               });

               diaryIngredientLiveData.postValue(diaryIngredientList);

            }else if(selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.GLUTEN.getCategoryDescription())){

           }else if(selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.PORK.getCategoryDescription())){

           }else if(selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.SHELLFISH.getCategoryDescription())){

           }else if(selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.MEATS.getCategoryDescription())){

           }else if(selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.NUTS.getCategoryDescription())){

           }else if(selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.OTHER.getCategoryDescription())){

           }else{
               Log.e("MatchIngredient","MatchNotFound: "+selectedIngredient.getIngredientName()+", "+selectedIngredient.getIngredientCategory());
           }


        }

    }







}
