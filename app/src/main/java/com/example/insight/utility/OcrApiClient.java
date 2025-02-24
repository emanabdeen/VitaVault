package com.example.insight.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.concurrent.CompletableFuture;

public class OcrApiClient {

    private static String baseUrl = "https://vision.googleapis.com/v1/images:annotate";
    private static String apiKey = "YOUR_API_KEY";
    private static String endpoint = baseUrl + "?key=" + apiKey;

    public static CompletableFuture<String> SendOcrRequest(String imageUrl, Context context){
        CompletableFuture<String> ocrTextResult = new CompletableFuture<String>();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageUrl);
        Frame frameImage = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frameImage);

        String stringImageText = "";

        for (int i=0; i <textBlockSparseArray.size();i++) {
            TextBlock textBlock = textBlockSparseArray.valueAt(i);
            stringImageText += textBlock.getValue();
        }
        if (stringImageText.length() > 0){
            ocrTextResult.complete(stringImageText);
        } else {
            ocrTextResult.complete("No text found");
        }

        return ocrTextResult;
    }
}
