package com.example.insight.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.insight.R;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class OcrApiClient {

    private static final String TAG = "OcrApiClient";

    public static String detectText(Context context, Bitmap bitmap) {
        try {
            // Load API credentials from JSON file in 'res/raw'
            InputStream stream = context.getResources().openRawResource(R.raw.vision_api_key);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
            ImageAnnotatorClient visionClient = ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build());

            // Convert Bitmap to ByteString
            ByteString imgBytes = ByteString.copyFrom(ImageUtils.bitmapToByteArray(bitmap));

            // Prepare Image for Vision API
            Image image = Image.newBuilder().setContent(imgBytes).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(image)
                    .build();

            // Perform OCR
            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(Collections.singletonList(request));
            TextAnnotation annotation = response.getResponses(0).getFullTextAnnotation();

            return annotation.getText(); // Extracted text from image
        } catch (IOException e) {
            Log.e(TAG, "Error initializing Google Vision API", e);
        }
        return null;
    }
}
