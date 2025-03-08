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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.insight.databinding.ActivityIngredientsScanBinding;
import com.example.insight.utility.ImageUtils;
import com.example.insight.utility.IngredientUtils;
import com.example.insight.utility.OcrApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;

import com.google.mlkit.vision.text.Text;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

public class OCRMainActivity extends AppCompatActivity {
    private static final String TAG = "OCRMainActivity";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;

    private ActivityIngredientsScanBinding binding;
    private IngredientUtils ingredientUtils;
    private Button cameraButton, ocrTestButton, parseIngredientsButton, detectTextButton;
    private ImageCapture imageCapture;
    private ImageView cameraResultImageView;
    private EditText ocrResultText;
    private Bitmap capturedImageBitmap, croppedImageBitmap;
    // TODO: Use capturedImageBitmap for preview snapshot and fallback if user cancels crop (if crop cancel doesn't update bitmap for ocr)
    // TODO: Clear capturedImageBitmap at start of each action that might change it, then fallback to capturedImageBitmap if null
    private Uri imageUri, croppedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientsScanBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());
        ingredientUtils = new IngredientUtils();
        previewView = binding.previewViewCameraX;
        cameraResultImageView = binding.imgViewCameraResult;
        ocrResultText = binding.ocrResultText;
        parseIngredientsButton = binding.parseIngredientsButton;
        detectTextButton = binding.detectTextButton;
        ocrTestButton = binding.ocrTestButton;
        cameraButton = binding.cameraButton;


        ocrResultText.setMovementMethod(new ScrollingMovementMethod());
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);


        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (Exception e) {
                Log.e(TAG, "onCreate: " + e.getMessage());
            }
        }, getExecutor());
        cameraResultImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraResultImageView.setVisibility(View.INVISIBLE);
                previewView.setVisibility(View.VISIBLE);
            }
        });

        parseIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OCRMainActivity.this, "Parse Ingredients Button Clicked", Toast.LENGTH_LONG).show();
                HashMap<String, ArrayList<String>> ingredientsListMap = ingredientUtils.splitIngredientList(ocrResultText.getText().toString());
                Log.d(TAG, "Ingredients List: " + ingredientsListMap);
            }
        });

        detectTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OCRMainActivity.this, "Detect Text Button Clicked", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Running OcrApiClient.detectText");
                Log.d(TAG, "Please wait for result text...");
                if (croppedImageBitmap != null) {
                    Task<Text> textResult = OcrApiClient.detectText(croppedImageBitmap);
                    textResult.addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    Log.d(TAG, "onSuccess: visionText: " + visionText.getText());
                                    runOnUiThread(() -> {
                                        ocrResultText.setText(visionText.getText());
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: " + e.getMessage());
                                    Toast.makeText(OCRMainActivity.this, "Error detecting text: view log ", Toast.LENGTH_LONG).show();
                                }
                            });


                } else {
                    Log.e(TAG, "onClick: imageBitmap is null");
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    Log.d(TAG, "OnClick: camera button clicked, trying to capture photo");
                    capturePhotoFromPreview();
                    capturePhoto();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.CAMERA);
                }
            }
        });

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

    private void capturePhotoFromPreview() {
        Bitmap imageBitmap = previewView.getBitmap();
        try {
            Log.d(TAG, "onImageSaved: imageBitmap[0] is not null");

            new Thread(() -> {
                runOnUiThread(() -> {
                    if (imageBitmap != null) {
                        Log.d(TAG, "onImageSaved: imageBitmap[0] is not null, setting image view");
                        cameraResultImageView.setImageBitmap(imageBitmap);
                        cameraResultImageView.setVisibility(View.VISIBLE);
                        previewView.setVisibility(View.INVISIBLE);
                    }
                });
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e.getMessage());
            Toast.makeText(OCRMainActivity.this, "Error taking photo", Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "onImageSaved: image saved");
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
                            runOnUiThread( () -> startCropActivity(imageUri));
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                            imageBitmap[0] = ImageDecoder.decodeBitmap(source);
                            Log.d(TAG, "onImageSaved: imageBitmap[0] is not null");

                            new Thread(() -> {
                                runOnUiThread(() -> {
                                    if (imageBitmap[0] != null) {
                                        Log.d(TAG, "onImageSaved: imageBitmap[0] is not null, setting image view");
                                        cameraResultImageView.setImageBitmap(imageBitmap[0]);
                                        cameraResultImageView.setVisibility(View.VISIBLE);
                                        previewView.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }).start();
                        } catch (Exception e) {
                            Log.e(TAG, "onClick: " + e.getMessage());
                            Toast.makeText(OCRMainActivity.this, "Error taking photo", Toast.LENGTH_LONG).show();
                        }
                        Log.d(TAG, "onImageSaved: image saved");
                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        Log.d(TAG, "onError: " + error.getMessage());
                        Toast.makeText(OCRMainActivity.this, "Error taking photo", Toast.LENGTH_LONG).show();
                    }
                }
        );
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
                            cameraResultImageView.setVisibility(View.VISIBLE);
                            previewView.setVisibility(View.INVISIBLE);
                            imageUri = croppedImageUri;
                        }
                    }
                    else if (o.getResultCode() == UCrop.RESULT_ERROR) {
                        Log.e(TAG, "onActivityResult: UCrop error");
                    }
                }
            }
    );

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "requestPermissionLauncher: permission granted");
                    Toast.makeText(OCRMainActivity.this, "Camera permission granted, try capturing photo again", Toast.LENGTH_LONG).show();
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
                                runOnUiThread( () -> startCropActivity(uri));
                                runOnUiThread(() -> {
                                    if (imageBitmap[0] != null) {
                                        Log.d(TAG, "onImageSaved: imageBitmap[0] is not null, setting image view");
                                        cameraResultImageView.setImageURI(uri);
                                        cameraResultImageView.setVisibility(View.VISIBLE);
                                        previewView.setVisibility(View.INVISIBLE);
                                    }
                                });
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
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }
}