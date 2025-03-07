package com.example.insight.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.Task;
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
}
