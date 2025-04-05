package com.example.insight.viewmodel;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.insight.BuildConfig;
import com.example.insight.model.DietaryRestrictionIngredient;
import com.example.insight.model.OcrIngredient;
import com.example.insight.model.Symptom;
import com.example.insight.utility.CommonRestrictedIngredients;
import com.example.insight.utility.GeminiHelper;
import com.example.insight.utility.IngredientUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IngredientScanViewModel extends ViewModel {
    private final static String TAG = "IngredientScanViewModel";
    final String  customCategory = "custom";

    private final MutableLiveData<HashMap<String, ArrayList<OcrIngredient>>> ocrIngredientsData = new MutableLiveData<>();
    private HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsList = new HashMap<>();


    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<DietaryRestrictionIngredient>> dietaryRestrictionIngredientsData = new MutableLiveData<>();
    private List<DietaryRestrictionIngredient> dietaryRestrictionIngredientsList = new ArrayList<>();

    private final MutableLiveData<List<OcrIngredient>> matchedIngredientsData = new MutableLiveData<>();
    private List<OcrIngredient> matchedIngredientsList = new ArrayList<>();

    // Constructors
    public IngredientScanViewModel() {
        ocrIngredientsData.postValue(ocrIngredientsList);
        dietaryRestrictionIngredientsData.postValue(dietaryRestrictionIngredientsList);
        matchedIngredientsData.postValue(matchedIngredientsList);
    }
    public IngredientScanViewModel(HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsList, List<DietaryRestrictionIngredient> dietaryRestrictionIngredientsList, HashMap<String, OcrIngredient> matchedIngredientsMap) {
        ocrIngredientsData.postValue(ocrIngredientsList);
        dietaryRestrictionIngredientsData.postValue(dietaryRestrictionIngredientsList);
        matchedIngredientsData.postValue(matchedIngredientsList);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Setters
    public void setOcrIngredientsData(HashMap<String, ArrayList<OcrIngredient>> ocrIngredientsList) {
        this.ocrIngredientsData.postValue(ocrIngredientsList);
    }
    public void setDietaryRestrictionIngredientsData(List<DietaryRestrictionIngredient> dietaryRestrictionIngredientsList) {
        this.dietaryRestrictionIngredientsData.postValue(dietaryRestrictionIngredientsList);
    }
    public void setMatchedIngredientsData(HashMap<String, OcrIngredient> matchedIngredientsMap) {
        this.matchedIngredientsData.postValue(matchedIngredientsList);
    }

    // Getters
    public MutableLiveData<HashMap<String, ArrayList<OcrIngredient>>> getOcrIngredientsData() {
        return ocrIngredientsData;
    }
    public MutableLiveData<List<DietaryRestrictionIngredient>> getDietaryRestrictionIngredientsData() {
        return dietaryRestrictionIngredientsData;
    }
    public MutableLiveData<List<OcrIngredient>> getMatchedIngredientsData() {
        return matchedIngredientsData;
    }

    public CompletableFuture<Boolean> scanIngredientsForMatches(Intent ocrScanIntent, FirebaseFirestore db, String uid) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        CompletableFuture<Boolean> ocrIngredientListFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> restrictedIngredientListFuture = new CompletableFuture<>();

        boolean[] ocrIngredientsRetrieved = new boolean[1];
        boolean[] dietaryRestrictionsRetrieved = new boolean[1];
        getOcrIngredientsMapFromIntent(ocrScanIntent).whenComplete((result, error) -> {
            if (error != null) {
                Log.e(TAG, "scanIngredientsForMatches: " + error);
            }
            if (result) {
                ocrIngredientsRetrieved[0] = true;
                Log.d(TAG, "scanIngredientsForMatches: OCR ingredients retrieved: " + ocrIngredientsList.toString());
                ocrIngredientListFuture.complete(true);
            }
        });
        getDietaryRestrictionCustomIngredientsFromFirestore(db, uid).whenComplete((result, error) -> {
            if (error != null) {
                Log.e(TAG, "scanIngredientsForMatches: " + error);
            }
            if (result) {
                dietaryRestrictionsRetrieved[0] = true;
                Log.d(TAG, "scanIngredientsForMatches: dietary restrictions retrieved: " + dietaryRestrictionIngredientsList.toString());
                restrictedIngredientListFuture.complete(true);
            }
        });
        CompletableFuture.allOf(ocrIngredientListFuture, restrictedIngredientListFuture).whenComplete((result, error) -> {
            if (error != null) {
                isLoading.postValue(false);
                future.complete(false);
            }
            else {
                Log.d(TAG, "Both lists retrieved, running comparison logic");
                matchedIngredientsList = IngredientUtils.getMatchedIngredients(ocrIngredientsList, dietaryRestrictionIngredientsList);
                matchedIngredientsList.forEach(ingredient -> Log.d(TAG, "Matched ingredient: " + ingredient.toString()));
                matchedIngredientsData.postValue(matchedIngredientsList);
                //isLoading.postValue(false);
                future.complete(true);
            }
        });
        return future;
    }


    // Get ingredients map from intent
    public CompletableFuture<Boolean> getOcrIngredientsMapFromIntent(Intent ocrScanIntent) {
        isLoading.postValue(true);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (ocrScanIntent != null) {
            HashMap<String, ArrayList<String>> ingredientsListMap = (HashMap<String, ArrayList<String>>) ocrScanIntent.getSerializableExtra("ocrIngredients");
            if (ingredientsListMap != null) {
                ocrIngredientsList = IngredientUtils.parseIngredientsMapToOcrIngredients(ingredientsListMap);
                Log.d(TAG, "ingredients retrievevd from intent: " + ocrIngredientsList);
                setOcrIngredientsData(ocrIngredientsList);
                future.complete(true);
            }
        }
        else {
            Log.e(TAG, "getOcrIngredients: ocrScanIntent is null");
            future.complete(false);
        }
        return future;
    }

    // Get dietary restriction ingredients list from user db
    public CompletableFuture<Boolean> getDietaryRestrictionCustomIngredientsFromFirestore(FirebaseFirestore db, String uid) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        dietaryRestrictionIngredientsList = new ArrayList<>();

        CollectionReference dietaryRestrictions = db
                .collection("users")
                .document(uid)
                .collection("dietaryRestrictions");

        Query query = dietaryRestrictions;
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null && !querySnapshot.isEmpty()) {

                    // Retrieve the documents from the query result
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String category = document.getString("ingredientCategory");
                        String name = document.getString("ingredientName");

                        Log.d(TAG, "Document ID: " + document.getId());
                        Log.d(TAG, "Category: " + category);
                        Log.d(TAG, "name: " + name);

                        // Create vital object with the retrieved data
                        DietaryRestrictionIngredient ingredient = new DietaryRestrictionIngredient(name,category);
                        ingredient.setIngredientId(document.getId());

                        Log.d(TAG, ingredient.getIngredientName());


                        dietaryRestrictionIngredientsList.add(ingredient);
                    }
                    Log.d(TAG, "custom dietary restrictions retrieved");
                    dietaryRestrictionIngredientsData.postValue(dietaryRestrictionIngredientsList);
                    future.complete(true);

                }else{
                    Log.e(TAG,"getting the list!! "+ String.valueOf(dietaryRestrictionIngredientsList.size()));
                    dietaryRestrictionIngredientsData.postValue(dietaryRestrictionIngredientsList);
                    future.complete(false);
                }
            } else {
                // Handle failure
                Log.e("Firestore", "Error retrieving documents: " + task.getException().getMessage());
                dietaryRestrictionIngredientsData.postValue(dietaryRestrictionIngredientsList);
                future.complete(false);
            }
        });
        return future;
    }



    //TESTING WITH GEMINI
    private final MutableLiveData<String> geminiRawResponse = new MutableLiveData<>();
    public LiveData<String> getGeminiRawResponse() {
        return geminiRawResponse;
    }

    public void analyzeWithGemini() {
        List<String> ingredientNames = new ArrayList<>();
        for (OcrIngredient ocr : matchedIngredientsList) {
            ingredientNames.add(ocr.getIngredientName());
        }

        List<String> userRestrictions = new ArrayList<>();
        for (DietaryRestrictionIngredient restriction : dietaryRestrictionIngredientsList) {
            userRestrictions.add(restriction.getIngredientName().toLowerCase()); // or getCategory() if that's what you want
        }

        GeminiHelper.analyzeIngredients(ingredientNames, userRestrictions, BuildConfig.GEMINI_API_KEY)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Gemini analysis failed: " + error.getMessage());
                        geminiRawResponse.postValue("Gemini analysis failed: " + error.getMessage());
                    } else {
                        Log.d(TAG, "Gemini analysis result:\n" + result);
                        geminiRawResponse.postValue(result); // optionally parse later
                    }
                });
    }

    private final MutableLiveData<Set<String>> geminiFlaggedIngredients = new MutableLiveData<>(new HashSet<>());

    public void setGeminiFlaggedIngredients(Set<String> flagged) {
        geminiFlaggedIngredients.postValue(flagged);
    }

    public LiveData<Set<String>> getGeminiFlaggedIngredients() {
        return geminiFlaggedIngredients;
    }

    private final MediatorLiveData<Boolean> dataReady = new MediatorLiveData<>();

    public LiveData<Boolean> isDataReady() {
        return dataReady;
    }

    public void initReadyObservers(LiveData<List<OcrIngredient>> matchedIngredients, LiveData<String> geminiRawResponse) {
        dataReady.addSource(matchedIngredients, ingredients -> checkReadyState());
        dataReady.addSource(geminiRawResponse, response -> checkReadyState());
    }

    private void checkReadyState() {
        if (matchedIngredientsData.getValue() != null && !matchedIngredientsData.getValue().isEmpty()
                && geminiRawResponse.getValue() != null && !geminiRawResponse.getValue().isEmpty()) {
            dataReady.setValue(true);
        }
    }

    public void setIsLoading(boolean loading) {
        isLoading.setValue(loading);
    }




}
