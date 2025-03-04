package com.example.insight.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.VideoCapture;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.insight.databinding.ActivityIngredientsScanBinding;
import com.example.insight.utility.ImageUtils;
import com.example.insight.utility.IngredientUtils;
import com.example.insight.utility.OcrApiClient;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;

public class IngredientsScan extends AppCompatActivity {
    private static final String TAG = "IngredientsScan";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private TextureView textureView;

    private ActivityIngredientsScanBinding binding;
    private IngredientUtils ingredientUtils;
    private Button cameraButton, ocrTestButton;
    private ImageCapture imageCapture;
    private ImageView cameraResultImageView;
    private TextView ocrResultText;
    private Uri imageUri;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> launcherCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientsScanBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());
        ingredientUtils = new IngredientUtils();
        previewView = binding.previewViewCameraX;
        cameraResultImageView = binding.imgViewCameraResult;
        ocrResultText = binding.ocrResultText;

        cameraResultImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraResultImageView.setVisibility(View.INVISIBLE);
                previewView.setVisibility(View.VISIBLE);
            }
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (Exception e) {
                Log.e(TAG, "onCreate: " + e.getMessage());
            }}, getExecutor());
        ocrResultText.setMovementMethod(new ScrollingMovementMethod());
        launcherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Log.d(TAG, "onActivityResult: called.");
                Log.d(TAG, "onActivityResult: requestCode: " + " == " + REQUEST_IMAGE_CAPTURE + " resultCode: " + o.getResultCode() + " == " + RESULT_OK);
                if (o.getResultCode() == RESULT_OK) {
                    Bitmap[] imageBitmap = new Bitmap[1];
                    try {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                        imageBitmap[0] = ImageDecoder.decodeBitmap(source);
                        new Thread(() -> {
                            runOnUiThread(() -> {
                                if (imageBitmap[0] != null) {
                                    Log.d(TAG, "onImageSaved: imageBitmap[0] is not null, setting image view");
                                    cameraResultImageView.setImageBitmap(imageBitmap[0]);
                                    cameraResultImageView.setVisibility(View.VISIBLE);
                                    previewView.setVisibility(View.INVISIBLE);
                                }
                            });
                            Log.d(TAG, "Running OcrApiClient.detectText");
                            String textResult = OcrApiClient.detectText(getApplicationContext(), imageBitmap[0]);
                            Log.d(TAG, "onClick: OCR result: " + textResult);
                            Log.d(TAG, "Test parsing OCR result text...");
                            runOnUiThread(() -> {
                                ocrResultText.setText(textResult);
                            });
                            ArrayList<String> ingredientsList = ingredientUtils.splitIngredientList(textResult);
                            Log.d(TAG, "Ingredients List: " + ingredientsList);
                        }).start();
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: " + e.getMessage());
                    }

                }
            }
        });

        //cameraResultImageView = binding.imgViewCameraResult;
        cameraButton = binding.cameraButton;
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    //dispatchTakePictureIntent();
                    Log.d(TAG, "OnClick: camera button clicked, trying to capture photo");
                    try {
                        File imageFile = createImageFile();
                        capturePhoto();
                    } catch (IOException ex) {
                        Log.e(TAG, "Error trying to create file for image capture: " + ex.getMessage());
                    }
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.CAMERA);
                }
            }
        });
        ocrTestButton = binding.ocrTestButton;
        ocrTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    Log.d(TAG, "onClick: OCR TEST - media picker launched");

                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
            }
        });
    }

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//                Log.d(TAG, "dispatchTakePictureIntent: " + photoFile.getAbsolutePath());
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Log.e(TAG, "dispatchTakePictureIntent: " + ex.getMessage());
//                return;
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                imageUri = photoURI;
//                currentPhotoPath = photoURI.toString();
//                Log.d(TAG, "current photo path before saving: " + currentPhotoPath);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//                launcherCamera.launch(takePictureIntent);
//            }
//            else {
//                Log.e(TAG, "dispatchTakePictureIntent: failed to generate file uri");
//            }
//        }
//    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (storageDir.mkdirs()){
                Log.d(TAG, "createImageFile: directory created successfully");
            } else {
                Log.d(TAG, "createImageFile: failed to create directory " + storageDir.getName());
            }
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void capturePhoto() {
        long timestamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        imageUri = outputFileResults.getSavedUri();
                        Log.d(TAG, "onImageSaved: " + imageUri);
                        Bitmap[] imageBitmap = new Bitmap[1];
                        try {
                            runOnUiThread(() -> applyCropTransformation(imageUri.toString()));
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                            imageBitmap[0] = ImageDecoder.decodeBitmap(source);

                            new Thread(() -> {
                                runOnUiThread(() -> {
                                    if (imageBitmap[0] != null) {
                                        Log.d(TAG, "onImageSaved: imageBitmap[0] is not null, setting image view");
                                        cameraResultImageView.setImageBitmap(imageBitmap[0]);
                                        cameraResultImageView.setVisibility(View.VISIBLE);
                                        previewView.setVisibility(View.INVISIBLE);
                                    }
                                });
                                Log.d(TAG, "Running OcrApiClient.detectText");
                                String textResult = OcrApiClient.detectText(getApplicationContext(), imageBitmap[0]);
                                Log.d(TAG, "onClick: OCR result: " + textResult);
                                Log.d(TAG, "Test parsing OCR result text...");
                                runOnUiThread(() -> {
                                    ocrResultText.setText(textResult);
                                });
                                ArrayList<String> ingredientsList = ingredientUtils.splitIngredientList(textResult);
                                Log.d(TAG, "Ingredients List: " + ingredientsList);

                            }).start();
                        } catch (Exception e) {
                            Log.e(TAG, "onClick: " + e.getMessage());
                        }
                        Log.d(TAG, "onImageSaved: image saved");
                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        Log.d(TAG, "onError: " + error.getMessage());
                        Toast.makeText(IngredientsScan.this, "Error taking photo", Toast.LENGTH_LONG).show();
                    }
                }
                );
    }

//    private void startCropActivity(Uri imageUri) {
//        Uri croppedImageUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
//
//        Intent intent = new Intent(this, CropActivity.class);
//        intent.putExtra("IMAGE_URI", imageUri.toString());
//        intent.putExtra("DESTINATION_URI", croppedImageUri.toString());
//
//    }
    private void applyCropTransformation(String filePath) {
        Bitmap fullImageBitmap = ImageUtils.loadBitmap(filePath);

        if (fullImageBitmap == null) {
            Log.e(TAG, "applyCropTransformation: fullImageBitmap is null");
            return;
        }
        // Get transformation rectangle crop area
        int width = fullImageBitmap.getWidth();
        int height = fullImageBitmap.getHeight();
        int cropWidth = width / 2;
        int cropHeight = height / 2;
        int startX = (width - cropWidth) / 2;
        int startY = (height - cropHeight) / 2;

         // Crop image
         Bitmap croppedImageBitmap = Bitmap.createBitmap(fullImageBitmap, startX, startY, cropWidth, cropHeight);
         croppedImageBitmap = applyRotation(croppedImageBitmap, 90); // rotate if needed (don't think so)

    }

    private Bitmap applyRotation(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    //dispatchTakePictureIntent();
                    Log.d(TAG, "requestPermissionLauncher: permission granted");
                    Toast.makeText(IngredientsScan.this, "Camera permission granted, try capturing photo again", Toast.LENGTH_LONG).show();
                    //capturePhoto();
                } else {
                    Log.d(TAG, "checkPermissions: permission denied");
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });


    // Registers a photo picker activity launcher in single-select mode.
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    try {
                        Log.d("PhotoPicker", "Selected URI: " + uri.toString());
                        Log.d("PhotoPicker", "Launching Detect Text call");

                        Bitmap[] imageBitmap = new Bitmap[1];
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                            imageBitmap[0] = ImageDecoder.decodeBitmap(source);
                            new Thread(() -> {
                                runOnUiThread(() -> {
                                    if (imageBitmap[0] != null) {
                                        Log.d(TAG, "onImageSaved: imageBitmap[0] is not null, setting image view");
                                        cameraResultImageView.setImageBitmap(imageBitmap[0]);
                                        cameraResultImageView.setVisibility(View.VISIBLE);
                                        previewView.setVisibility(View.INVISIBLE);
                                    }
                                });
                                Log.d(TAG, "Running OcrApiClient.detectText");
                                String textResult = OcrApiClient.detectText(getApplicationContext(), imageBitmap[0]);
                                Log.d(TAG, "onClick: OCR result: " + textResult);
                                runOnUiThread(() -> {
                                    ocrResultText.setText(textResult);
                                });
                                ArrayList<String> ingredientsList = ingredientUtils.splitIngredientList(textResult);
                                Log.d(TAG, "Ingredients List: " + ingredientsList);
                            }).start();
                        } catch (Exception e) {
                            Log.e(TAG, "onClick: " + e.getMessage());
                        }

                    } catch (Exception e) {
                        Log.e("PhotoPicker", "Exception when picking picture: " + e.getMessage());
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });


    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }
}