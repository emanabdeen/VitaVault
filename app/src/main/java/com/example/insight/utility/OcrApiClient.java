package com.example.insight.utility;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.languageid.IdentifiedLanguage;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OcrApiClient {

    private static final String TAG = "OcrApiClient";

    public static Task<Text> detectText(Bitmap inputImageBitmap) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage inputImage = InputImage.fromBitmap(inputImageBitmap, 0);
        Log.d(TAG, "DetectText(): text recognition request being created");
        return recognizer.process(inputImage);
    }

    public static CompletableFuture<Boolean> detectLanguage(String inputText) {
        CompletableFuture<Boolean> languageIsEnglish = new CompletableFuture<>();
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient(new LanguageIdentificationOptions.Builder().setConfidenceThreshold(0.2f).build());
        languageIdentifier.identifyPossibleLanguages(inputText).addOnCompleteListener( new OnCompleteListener<List<IdentifiedLanguage>>() {
            @Override
            public void onComplete(@NonNull Task<List<IdentifiedLanguage>> task) {
                if (task.isSuccessful()) {
                    List<IdentifiedLanguage> identifiedLanguages = task.getResult();
                    Log.d(TAG, "detectLanguage(): language identification successful");
                    for (IdentifiedLanguage identifiedLanguage : identifiedLanguages) {
                        String languageCode = identifiedLanguage.getLanguageTag();
                        Log.d(TAG, "detectLanguage(): language: " + languageCode + " confidence: " + identifiedLanguage.getConfidence());
                    }
                } else {
                    Log.e(TAG, "detectLanguage(): language identification not successful");
                }
            }
        });
        Log.d(TAG, "detectLanguage(): language identification request being created");
        languageIdentifier.identifyLanguage(inputText).addOnCompleteListener(
                new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String languageCode = task.getResult();
                            Log.d(TAG, "detectLanguage(): language: " + languageCode);
                            if (languageCode.equals("en")) {
                                Log.d(TAG, "detectLanguage(): language is english " + languageCode);
                                languageIsEnglish.complete(true);
                            }
                            else {
                                Log.d(TAG, "detectLanguage(): language is not english " + languageCode);
                                languageIsEnglish.complete(false);
                            }
                        }
                        else {
                            Log.e(TAG, "detectLanguage(): language detection not successful");
                            languageIsEnglish.complete(false);
                        }
                    }
                }
        );
        return languageIsEnglish;
    }
}
