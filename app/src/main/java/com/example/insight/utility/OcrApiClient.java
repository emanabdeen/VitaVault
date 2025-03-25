package com.example.insight.utility;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
public class OcrApiClient {

    private static final String TAG = "OcrApiClient";

    public static Task<Text> detectText(Bitmap inputImageBitmap) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage inputImage = InputImage.fromBitmap(inputImageBitmap, 0);
        Log.d(TAG, "DetectText(): text recognition request being created");
        return recognizer.process(inputImage);
    }

    public static boolean detectLanguage(String inputText) {
        boolean[] languageEnglish = new boolean[1];
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        Log.d(TAG, "detectLanguage(): language identification request being created");
        languageIdentifier.identifyLanguage(inputText).addOnCompleteListener(
                new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String languageCode = task.getResult();
                            Log.d(TAG, "detectLanguage(): language: " + languageCode);
                            if (languageCode.equals("en")) {
                                Log.d(TAG, "detectLanguage(): language is english");
                                languageEnglish[0] = true;
                            }
                            else {
                                Log.d(TAG, "detectLanguage(): language is not english");
                                languageEnglish[0] = false;
                            }
                        }
                        else {
                            Log.e(TAG, "detectLanguage(): language detection not successful");
                            languageEnglish[0] = false;
                        }
                    }
                }
        );
        return languageEnglish[0];
    }
}
