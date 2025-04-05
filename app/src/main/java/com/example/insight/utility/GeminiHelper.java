package com.example.insight.utility;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.*;

public class GeminiHelper {
    private static final String TAG = "GeminiHelper";

    // ✅ Your correct endpoint based on Google AI Studio
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public static CompletableFuture<String> analyzeIngredients(List<String> ingredientNames, List<String> userRestrictions, String apiKey) {
        CompletableFuture<String> future = new CompletableFuture<>();

//        String prompt = String.format("""
//Here is a list of food ingredients:
//%s
//
//Identify any ingredients that may conflict with dietary restrictions such as:
//- alcohol-free (e.g., wine, beer, vodka, rum, brandy, liquor, ethanol, wine vinegar, alcohol extract)
//- vegan (e.g., gelatin, whey, casein, honey, beeswax)
//- kosher (e.g., shellfish, pork, non-kosher meat)
//- halal (e.g., pork, alcohol, gelatin from non-halal animals, non-zabiha meat, wine vinegar, rum extract, vanilla extract, blood by-products)
//- gluten-free (e.g., wheat, barley, rye, malt)
//
//Return a JSON array like this:
//[
//  {
//    "ingredient": "natural flavors",
//    "reason": "May contain alcohol-based solvents",
//    "restriction": "alcohol"
//  }
//]
//""", String.join(", ", ingredientNames));

        // Build the prompt dynamically from ingredients and user restrictions
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Here is a list of food ingredients:\n");
        promptBuilder.append(String.join(", ", ingredientNames));
        promptBuilder.append("\n\n");

        promptBuilder.append("Identify any ingredients that may conflict with the following dietary restrictions:\n");
        for (String restriction : userRestrictions) {
            promptBuilder.append("- ").append(restriction).append("\n");
        }

        promptBuilder.append("""
        
Return a JSON array like this:
[
  {
    "ingredient": "natural flavors",
    "reason": "May contain alcohol-based solvents",
    "restriction": "alcohol"
  }
]
""");

        String prompt = promptBuilder.toString();
        Log.d(TAG, "Sending prompt to Gemini:\n" + prompt);

        try {
            // ✅ Build Gemini API request JSON
            JSONObject textPart = new JSONObject().put("text", prompt);
            JSONArray parts = new JSONArray().put(textPart);
            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);
            JSONObject requestBody = new JSONObject().put("contents", contents);

            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + apiKey)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Gemini API call failed: " + e.getMessage());
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Gemini analysis failed: Unexpected code " + response);
                        future.completeExceptionally(new IOException("Unexpected code " + response));
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Gemini raw response: " + responseBody);

                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String aiText = json
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        Log.d(TAG, "Gemini analysis success:\n" + aiText);
                        future.complete(aiText);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse Gemini response: " + e.getMessage());
                        future.completeExceptionally(e);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Request construction failed: " + e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }
}
