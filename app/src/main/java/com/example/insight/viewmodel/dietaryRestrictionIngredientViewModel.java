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
    List<DietaryRestrictionIngredient> savedIngredientList = new ArrayList<>();

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
        predefinedIngredientLiveData.setValue(savedIngredientList);
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
        Log.e("debug","Predefined liveData size:"+ predefinedIngredientLiveData.getValue().size());
        return predefinedIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getPorkIngredientLiveData() {
        return porkIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getGlutenIngredientLiveData() {
        return glutenIngredientLiveData;
    }

    public MutableLiveData<List<DietaryRestrictionIngredient>> getDiaryIngredientLiveData() {
        Log.e("debug","Diary liveData size:"+ diaryIngredientLiveData.getValue().size());
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

    //setters
    public void setDiaryIngredientList(List<DietaryRestrictionIngredient> diaryIngredientList) {
        this.diaryIngredientList = diaryIngredientList;
    }

    public void setGlutenIngredientList(List<DietaryRestrictionIngredient> glutenIngredientList) {
        this.glutenIngredientList = glutenIngredientList;
    }

    public void setPorkIngredientList(List<DietaryRestrictionIngredient> porkIngredientList) {
        this.porkIngredientList = porkIngredientList;
    }

    public void setShellfishIngredientList(List<DietaryRestrictionIngredient> shellfishIngredientList) {
        this.shellfishIngredientList = shellfishIngredientList;
    }

    public void setMeatsIngredientList(List<DietaryRestrictionIngredient> meatsIngredientList) {
        this.meatsIngredientList = meatsIngredientList;
    }

    public void setNutsIngredientList(List<DietaryRestrictionIngredient> nutsIngredientList) {
        this.nutsIngredientList = nutsIngredientList;
    }

    public void setOtherIngredientList(List<DietaryRestrictionIngredient> otherIngredientList) {
        this.otherIngredientList = otherIngredientList;
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
                savedIngredientList = new ArrayList<>();
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


                        savedIngredientList.add(ingredient);
                    }


                    predefinedIngredientLiveData.postValue(savedIngredientList);
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

    private void updateSelectedItems() {

        resetLists();

        for (DietaryRestrictionIngredient selectedIngredient : savedIngredientList) {

            Log.i("debug-ViewModel", "Selected: " + selectedIngredient.getIngredientName());

            if (selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.DAIRY.getCategoryDescription())) {

                diaryIngredientList.forEach(i -> {

                    if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())) {
                        i.setSelected(true);
                        i.setIngredientId(selectedIngredient.getIngredientId());
                        Log.i("debug-ViewModel", "MATCH: " + i.getIngredientName());
                    }

                    Log.e("debug-ViewModel", i.getIngredientName() + " is selected: " + i.getIsSelected());
                });

                diaryIngredientLiveData.postValue(diaryIngredientList);

            } else if (selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.GLUTEN.getCategoryDescription())) {


                glutenIngredientList.forEach(i -> {

                    if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())) {
                        i.setSelected(true);
                        i.setIngredientId(selectedIngredient.getIngredientId());
                        Log.i("debug-ViewModel", "MATCH: " + i.getIngredientName());
                    }

                    Log.e("debug-ViewModel", i.getIngredientName() + " is selected: " + i.getIsSelected());
                });

                glutenIngredientLiveData.postValue(glutenIngredientList);

            } else if (selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.PORK.getCategoryDescription())) {

                porkIngredientList.forEach(i -> {

                    if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())) {
                        i.setSelected(true);
                        i.setIngredientId(selectedIngredient.getIngredientId());
                        Log.i("debug-ViewModel", "MATCH: " + i.getIngredientName());
                    }

                    Log.e("debug-ViewModel", i.getIngredientName() + " is selected: " + i.getIsSelected());
                });

                porkIngredientLiveData.postValue(porkIngredientList);


            } else if (selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.SHELLFISH.getCategoryDescription())) {

                shellfishIngredientList.forEach(i -> {

                    if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())) {
                        i.setSelected(true);
                        i.setIngredientId(selectedIngredient.getIngredientId());
                        Log.i("debug-ViewModel", "MATCH: " + i.getIngredientName());
                    }

                    Log.e("debug-ViewModel", i.getIngredientName() + " is selected: " + i.getIsSelected());
                });

                shellfishIngredientLiveData.postValue(shellfishIngredientList);


            } else if (selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.MEATS.getCategoryDescription())) {

                meatsIngredientList.forEach(i -> {

                    if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())) {
                        i.setSelected(true);
                        i.setIngredientId(selectedIngredient.getIngredientId());
                        Log.i("debug-ViewModel", "MATCH: " + i.getIngredientName());
                    }

                    Log.e("debug-ViewModel", i.getIngredientName() + " is selected: " + i.getIsSelected());
                });

                meatsIngredientLiveData.postValue(meatsIngredientList);


            } else if (selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.NUTS.getCategoryDescription())) {

                nutsIngredientList.forEach(i -> {

                    if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())) {
                        i.setSelected(true);
                        i.setIngredientId(selectedIngredient.getIngredientId());
                        Log.i("debug-ViewModel", "MATCH: " + i.getIngredientName());
                    }

                    Log.e("debug-ViewModel", i.getIngredientName() + " is selected: " + i.getIsSelected());
                });

                nutsIngredientLiveData.postValue(nutsIngredientList);


            } else if (selectedIngredient.getIngredientCategory().equalsIgnoreCase(RestrictedIngredientsCategory.OTHER.getCategoryDescription())) {

                otherIngredientList.forEach(i -> {

                    if (i.getIngredientName().equalsIgnoreCase(selectedIngredient.getIngredientName())) {
                        i.setSelected(true);
                        i.setIngredientId(selectedIngredient.getIngredientId());
                        Log.i("debug-ViewModel", "MATCH: " + i.getIngredientName());
                    }

                    Log.e("debug-ViewModel", i.getIngredientName() + " is selected: " + i.getIsSelected());
                });

                otherIngredientLiveData.postValue(otherIngredientList);


            } else {
                Log.e("MatchIngredient", "MatchNotFound: " + selectedIngredient.getIngredientName() + ", " + selectedIngredient.getIngredientCategory());
            }

        }
    }

    private void resetLists() {

        diaryIngredientList.forEach(dairy->{
            dairy.setSelected(false);
            dairy.setIngredientId(null);
        });

        glutenIngredientList.forEach(gluten->{
            gluten.setSelected(false);
            gluten.setIngredientId(null);
        });

        porkIngredientList.forEach(pork->{
            pork.setSelected(false);
            pork.setIngredientId(null);
        });

        shellfishIngredientList.forEach(shellfish->{
            shellfish.setSelected(false);
            shellfish.setIngredientId(null);
        });

        meatsIngredientList.forEach(meats->{
            meats.setSelected(false);
            meats.setIngredientId(null);
        });

        nutsIngredientList.forEach(nuts->{
            nuts.setSelected(false);
            nuts.setIngredientId(null);
        });

        otherIngredientList.forEach(other->{
            other.setSelected(false);
            other.setIngredientId(null);
        });

    }

}
