package com.example.insight.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageUtils {

    public static Bitmap loadBitmap(String filePath) {
        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        } else {
            Log.e("ImageUtils", "File not found: " + filePath);
            return null;
        }
    }
}
