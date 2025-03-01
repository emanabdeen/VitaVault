package com.example.insight.utility;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            Log.d("ImageUtils", "bitmap is null");
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
