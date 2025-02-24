package com.example.insight.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.model.DietaryRestrictionIngredient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


    //set the LiveData
    private final MutableLiveData<List<DietaryRestrictionIngredient>> ingredientLiveData = new MutableLiveData<>();
    List<DietaryRestrictionIngredient> ingredientList = new ArrayList<>();


    //Constructor
    public dietaryRestrictionIngredientViewModel() {
        ingredientLiveData.setValue(ingredientList);

    }

    //Getter
    public LiveData<List<DietaryRestrictionIngredient>> getIngredientsData() {
        return ingredientLiveData;
    }


    public void getAllDietaryRestrictionIngredients() {

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("dietaryRestrictions")
                .orderBy("ingredientName", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ingredientList = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    Log.d("debug", "----------------------Get Vitals By type--------------------------------");

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


                        ingredientList.add(ingredient);
                    }

                    ingredientLiveData.postValue(ingredientList);
                    Log.d("AddCustomIngredientdebug","passou Aqui!");


                }else{
                    ingredientLiveData.postValue(null);
                }
            } else {
                // Handle failure
                Log.e("Firestore", "Error retrieving documents: " + task.getException().getMessage());
                ingredientLiveData.postValue(null);
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
                        Log.d("debug", "Vital updated successfully.");


                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error", "Error updating vital: " + e.getMessage());

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




}
