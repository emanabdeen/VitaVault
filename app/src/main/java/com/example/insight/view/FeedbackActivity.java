package com.example.insight.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivityFeedbackBinding;
import com.example.insight.databinding.ActivityMyProfileBinding;
import com.example.insight.model.UserAccount;
import com.example.insight.utility.StringHandler;
import com.example.insight.viewmodel.AccountViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FeedbackActivity extends DrawerBaseActivity {
    ActivityFeedbackBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    AccountViewModel viewModel;
    UserAccount userAccount;
    private static final String TAG = "Activity";

    // Variables to track selected rating
    private String selectedRating = "";
    private ImageView lastSelectedImageView = null; // Tracks the last selected ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Feedback");

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        viewModel.GetUserProfile();

        // Observe user account data
        viewModel.getUserAccountData().observe(this, userAccountData -> {
            userAccount = userAccountData;
        });

        // Set click listeners for face images
        binding.faceVeryUnhappy.setOnClickListener(v -> handleImageSelection(binding.faceVeryUnhappy, "Very Unhappy"));
        binding.faceUnhappy.setOnClickListener(v -> handleImageSelection(binding.faceUnhappy, "Unhappy"));
        binding.faceNeutral.setOnClickListener(v -> handleImageSelection(binding.faceNeutral, "Neutral"));
        binding.faceHappy.setOnClickListener(v -> handleImageSelection(binding.faceHappy, "Happy"));
        binding.faceVeryHappy.setOnClickListener(v -> handleImageSelection(binding.faceVeryHappy, "Very Happy"));

        // Handle submit button click
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount updatedUserAccount = new UserAccount();
                String userFeedback = StringHandler.defaultIfNull(binding.txtFeedback.getText().toString());

                // Update user data in Firestore
                if (userAccount != null) {
                    updatedUserAccount.setFeedback(userFeedback);
                    updatedUserAccount.setAppRating(selectedRating);

                    viewModel.UpdateUserFeedbackRatingData(updatedUserAccount)
                            .thenAccept(success -> {
                                if (success) {
                                    Log.d(TAG, "User data updated successfully!");
                                    Toast.makeText(FeedbackActivity.this, "Thank You!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Log.e(TAG, "Failed to update user data.");
                                    Toast.makeText(FeedbackActivity.this, "Failed to submit your feedback. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void handleImageSelection(ImageView imageView, String ratingValue) {
        // Reset the appearance of the last selected image (if any)
        if (lastSelectedImageView != null) {
            lastSelectedImageView.setBackgroundResource(0); // Remove background
        }

        // Highlight the selected image
        imageView.setBackgroundResource(R.drawable.selected_image_border);
        lastSelectedImageView = imageView;

        // Update the selected rating value
        selectedRating = ratingValue;
    }
}