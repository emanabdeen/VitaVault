package com.example.insight.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.insight.databinding.ActivityOcrMainBinding;
import com.example.insight.utility.ImageUtils;
import com.example.insight.utility.IngredientUtils;
import com.example.insight.utility.OcrApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;

import com.google.mlkit.vision.text.Text;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

public class OCRMainActivity extends DrawerBaseActivity {
    private static final String TAG = "OCRMainActivity";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;

    private ActivityOcrMainBinding binding;
    private Button cameraButton, parseIngredientsButton;
    private TextView baseInstructionsText;
    private ImageView cameraResultImageView;
    private EditText ocrResultText;
    private Bitmap  capturedImageBitmap, croppedImageBitmap;
    // TODO: Use capturedImageBitmap for preview snapshot and fallback if user cancels crop (if crop cancel doesn't update bitmap for ocr)
    // TODO: Clear capturedImageBitmap at start of each action that might change it, then fallback to capturedImageBitmap if null
    private Uri imageUri, savedImageUri, croppedImageUri;
    private ProcessCameraProvider cameraProvider;

    private LifecycleCameraController cameraController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOcrMainBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());
        allocateActivityTitle("OCR Ingredient Scan");
        previewView = binding.previewViewCameraX;
        cameraResultImageView = binding.imgViewCameraResult;
        ocrResultText = binding.ocrResultText;
        parseIngredientsButton = binding.parseIngredientsButton;
        cameraButton = binding.cameraButton;
        baseInstructionsText = binding.baseInstructionText;


        ocrResultText.setMovementMethod(new ScrollingMovementMethod());

        baseInstructionsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check camera permissions before trying to initialize camera
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onCreate: Camera permission not granted, requesting camera permission");
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                } else {
                    startCameraX();
                }
            }
        });
        baseInstructionsText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    Log.d(TAG, "onClick: OCR TEST - media picker launched");
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
                return true;
            }
        });

        cameraResultImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check camera permissions before trying to initialize camera
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onCreate: Camera permission not granted, requesting camera permission");
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                } else {
                    startCameraX();
                }
            }
        });
        cameraResultImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    Log.d(TAG, "onClick: OCR TEST - media picker launched");
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
                return true;
            }
        });

        previewView.setOnClickListener(new View.OnClickListener() {
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

        parseIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OCRMainActivity.this, "Parse Ingredients Button Clicked", Toast.LENGTH_LONG).show();
                HashMap<String, ArrayList<String>> ingredientsListMap = IngredientUtils.splitIngredientList(ocrResultText.getText().toString());
                Log.d(TAG, "Ingredients ListMap: " + ingredientsListMap);
                Intent intent = new Intent(OCRMainActivity.this, OCRResultsActivity.class);
                intent.putExtra("ocrIngredients", ingredientsListMap);
                startActivity(intent);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (previewView.getVisibility() != View.VISIBLE) {
                        startCameraX();
                    } else {
                        Log.d(TAG, "OnClick: camera button clicked, trying to capture photo");
                        capturePhoto();
                    }
                } else {
                    Log.d(TAG, "cameraButton.onclick(): Camera permission not granted, requesting camera permission");
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });
    }


    private void capturePhoto() {
        Bitmap imageBitmap = previewView.getBitmap();
        try {
            new Thread(() -> {
                runOnUiThread(() -> {
                    if (imageBitmap != null) {
                        cameraResultImageView.setImageBitmap(imageBitmap);
                        showImageView();
                    }
                });
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e.getMessage());
            Toast.makeText(OCRMainActivity.this, "Error taking photo", Toast.LENGTH_LONG).show();
        }
        File tempFile = null;
        try {
            tempFile = File.createTempFile("ocr_image_", ".jpg", getCacheDir());
        } catch (IOException e) {
            Log.e(TAG, "capturePhoto: Error creating temp file", e);
            //Toast.makeText(OCRMainActivity.this, "Error creating temp file", Toast.LENGTH_LONG).show();
        }
        long timestamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(tempFile).build();
        File finalTempFile = tempFile;
        //cameraController.takePicture(
        //outputFileOptions,
        Uri tempImageUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", tempFile);
        cameraController.takePicture(
               outputFileOptions,
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedPhotoUri = Uri.fromFile(finalTempFile);
                        Log.d(TAG, "onImageSaved: " + savedPhotoUri);
                        Bitmap[] imageBitmap = new Bitmap[1];
                        new Thread(() -> {
                            try {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), savedPhotoUri);
                                imageBitmap[0] = ImageDecoder.decodeBitmap(source);
                                runOnUiThread(() -> {
                                    if (imageBitmap[0] != null) {
                                        Log.d(TAG, "onImageSaved: imageBitmap[0] is not null, setting image view");
                                        cameraResultImageView.setImageBitmap(imageBitmap[0]);
                                        showImageView();
                                        croppedImageBitmap = imageBitmap[0];
                                        // Unbind camera controller to stop camera and save battery when not in use
                                        if (cameraController != null) {
                                            cameraController.unbind();
                                        }
                                        detectText();
                                    }
                                });
                                runOnUiThread( () -> startCropActivity(savedPhotoUri));
                            } catch (Exception e) {
                                Log.e(TAG, "onClick: " + e.getMessage());
                                runOnUiThread( () -> Toast.makeText(OCRMainActivity.this, "Error taking photo", Toast.LENGTH_LONG).show());
                            }

                            Log.d(TAG, "onImageSaved: image saved");
                        }).start();
                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        Log.d(TAG, "onError: " + error.getMessage());
                        Toast.makeText(OCRMainActivity.this, "Error taking photo", Toast.LENGTH_LONG).show();
                    }
                }
        );
        savedImageUri = Uri.fromFile(finalTempFile);
    }

    private void detectText(){
        Log.d(TAG, "Running OcrApiClient.detectText");
        Log.d(TAG, "Please wait for result text...");
        if (croppedImageBitmap != null) {
            OcrApiClient.detectText(croppedImageBitmap)
                    .addOnCompleteListener(new OnCompleteListener<Text>() {
                        @Override
                        public void onComplete(@NonNull Task<Text> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DetectText was successful: " + task.getResult().getText());
                                runOnUiThread(() -> {
                                    ocrResultText.setText(task.getResult().getText());
                                });
                            } else {
                                Toast.makeText(OCRMainActivity.this, "Error detecting text: view log ", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "An error occurred while detecting text...");
                                if (task.getException().getMessage() != null ) {
                                    Log.e(TAG, "detectText exception: " + task.getException().getMessage());
                                }
                            }
                        }
                    });
        } else {
            Log.e(TAG, "detectText: imageBitmap is null");
        }
    }

    private void startCropActivity(Uri imageUri) {

        croppedImageUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
        Log.d(TAG, "startCropActivity: cropped image uri: " + croppedImageUri);

        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true); // enable free-style cropping for user control

        Intent uCropIntent = UCrop.of(imageUri, croppedImageUri)
                .withOptions(options)
                .getIntent(OCRMainActivity.this);
        cropActivityResultLauncher.launch(uCropIntent);
    }

    ActivityResultLauncher<Intent> cropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        if (croppedImageUri != null) {
                            Log.d(TAG, "onActivityResult: cropped image uri: " + croppedImageUri);
                            croppedImageBitmap = ImageUtils.loadBitmap(croppedImageUri.getPath());
                            cameraResultImageView.setImageURI(croppedImageUri);
                            showImageView();
                            detectText();
                            // Unbind camera controller to stop camera and save battery when not in use
                            if (cameraController != null) {
                                cameraController.unbind();
                            }
                            // Delete original image file
                            File finalTempFile = new File(savedImageUri.getPath());
                            if (finalTempFile.delete()) {
                                if (finalTempFile.exists()) {
                                    Log.e(TAG, "Captured image file failed to delete.");
                                } else {
                                    Log.d(TAG, "Captured image file deleted.");
                                }
                            }
                            // Delete cropped image file
                            File file = new File(getCacheDir(), "cropped_image.jpg");
                            if (file.delete()) {
                                if (file.exists()) {
                                    Log.e(TAG, "Cropped image file failed to delete.");
                                } else {
                                    Log.d(TAG, "Cropped image file deleted.");
                                }
                            }
                        }
                    } else if (o.getResultCode() == UCrop.RESULT_ERROR) {
                        Log.e(TAG, "onActivityResult: UCrop error");
                    }
                }
            }
    );

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "requestPermissionLauncher: permission granted");
                    Toast.makeText(OCRMainActivity.this, "Camera permission granted, photos can be taken for OCR...", Toast.LENGTH_LONG).show();
                    startCameraX();
                } else {
                    Log.d(TAG, "checkPermissions: permission denied");
                    Toast.makeText(OCRMainActivity.this, "Camera permission denied!\nEnable camera permission in app settings to take photos for OCR...", Toast.LENGTH_LONG).show();
                }
            });

    // Hide imageView and show previewView
    private void showCameraPreview() {
        runOnUiThread(() -> {
            imageUri = null;
            previewView.setVisibility(View.VISIBLE);
            baseInstructionsText.setVisibility(View.INVISIBLE);
            cameraResultImageView.setVisibility(View.INVISIBLE);
        });
    }
    // Hide previewView and show imageView
    private void showImageView() {
        runOnUiThread(() -> {
            cameraResultImageView.setVisibility(View.VISIBLE);
            baseInstructionsText.setVisibility(View.INVISIBLE);
            previewView.setVisibility(View.INVISIBLE);
        });
    }
    // Registers a photo picker activity launcher in single-select mode.
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the photo picker.
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
                                        Log.d(TAG, "Media Picked: imageBitmap[0] is not null, setting image view");
                                        cameraResultImageView.setImageURI(uri);
                                        croppedImageBitmap = imageBitmap[0];
                                        showImageView();
                                        // Unbind camera controller to stop camera and save battery when not in use
                                        if (cameraController != null) {
                                            cameraController.unbind();
                                        }
                                        detectText();
                                    }
                                });
                                runOnUiThread( () -> startCropActivity(uri));
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

    private void startCameraX() {
        // Initialize camera
        showCameraPreview();
        cameraController = new LifecycleCameraController(this.getBaseContext());
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);
    }
}