package com.example.insight.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivitySymptomReportBinding;
import com.example.insight.model.Symptom;
import com.example.insight.viewmodel.SymptomViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

public class SymptomReportActivity extends DrawerBaseActivity {

    ActivitySymptomReportBinding binding;
    private SymptomViewModel symptomViewModel;
    private TextInputEditText startDateInput, endDateInput;
    private Button searchButton;
    private Button exportButton;
    private TableLayout symptomTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySymptomReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Report");

        //setContentView(R.layout.activity_symptom_report);

        // Initialize ViewModel
        symptomViewModel = new ViewModelProvider(this).get(SymptomViewModel.class);

        // Initialize Views
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        searchButton = findViewById(R.id.searchButton);
        symptomTable = findViewById(R.id.symptomTable);
        exportButton = findViewById(R.id.exportButton);

        // Set up date pickers for start and end date inputs
        startDateInput.setOnClickListener(v -> showDatePicker(startDateInput));
        endDateInput.setOnClickListener(v -> showDatePicker(endDateInput));

        // Set up the search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchSymptomsByDateRange();
            }
        });

        // Set up the export button click listener
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportTableToDocument();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with exporting the document
                exportTableToDocument();
            } else {
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePicker(TextInputEditText dateInput) {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it to the input field
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void searchSymptomsByDateRange() {
        // Get selected dates from input fields
        String startDateStr = startDateInput.getText().toString();
        String endDateStr = endDateInput.getText().toString();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            // Show an error if dates are not selected
            clearTable();
            addTableRow("Please select both start and end dates.");
            return;
        }

        // Fetch symptoms by date range
        symptomViewModel.GetSymptomsByDateRange(startDateStr, endDateStr);

        // Observe the LiveData to update the table
        symptomViewModel.getSymptomsData().observe(this, symptoms -> {
            if (symptoms != null && !symptoms.isEmpty()) {
                updateTable(symptoms);
            } else {
                clearTable();
                addTableRow("No symptoms found for the selected date range.");
            }
        });
    }

    private void exportTableToDocument() {

        // Check if the device is running Android 11 (API 30) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Check if the app has the MANAGE_EXTERNAL_STORAGE permission
            if (!Environment.isExternalStorageManager()) {
                // Request the MANAGE_EXTERNAL_STORAGE permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return; // Exit the method and wait for the user to grant permission
            }
        }

        // Get the user's email (you can fetch this from your user model or shared preferences)
        String userEmail = "user@example.com"; // Replace with actual user email

        // Create a StringBuilder to hold the document content
        StringBuilder documentContent = new StringBuilder();

        // Add the user email and some text at the top of the document
        documentContent.append("User Email: ").append(userEmail).append("\n\n");
        documentContent.append("Symptom Report\n\n");

        // Add the table headers
        documentContent.append("Symptom Name\tLevel\tDate\tStart Time\tEnd Time\tDescription\n");

        // Iterate through the table rows and add them to the document content
        for (int i = 1; i < symptomTable.getChildCount(); i++) {
            TableRow row = (TableRow) symptomTable.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView textView = (TextView) row.getChildAt(j);
                documentContent.append(textView.getText().toString()).append("\t");
            }
            documentContent.append("\n");
        }

        // Save the document to a file
        saveDocumentToFile(documentContent.toString());
    }

    private void saveDocumentToFile(String content) {
        // Define the file name and type
        String fileName = "SymptomReport_" + System.currentTimeMillis() + ".txt";
        String mimeType = "text/plain";

        // Use MediaStore to create the file in the Downloads directory
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Insert the file into the Downloads directory
        Uri fileUri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);

        if (fileUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(fileUri)) {
                if (outputStream != null) {
                    outputStream.write(content.getBytes());
                    Toast.makeText(this, "Document saved to Downloads", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save document", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTable(List<Symptom> symptoms) {
        // Clear the existing table rows
        clearTable();

        // Add header row
        addTableRow("Symptom Name", "Level", "Date", "Start Time", "End Time", "Description");

        // Add rows for each symptom
        for (Symptom symptom : symptoms) {
            addTableRow(
                    symptom.getSymptomName(),
                    symptom.getSymptomLevel(),
                    symptom.getRecordDate().toString(),
                    symptom.getStartTime().toString(),
                    symptom.getEndTime().orElse(null) != null ? symptom.getEndTime().get().toString() : "N/A",
                    symptom.getSymptomDescription()
            );
        }
    }

    private void addTableRow(String... values) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setBackgroundResource(R.drawable.row_boarder);

        for (int i = 0; i < values.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(values[i]);
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(12);
            //textView.setBackgroundResource(R.drawable.table_border);
            textView.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            textView.setMaxLines(5);
            textView.setEllipsize(TextUtils.TruncateAt.END);

            // Center header text and specific columns 2, 3, 4, 5 (0-based index)
            if (symptomTable.getChildCount() == 0) { // Check if it's the header row
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setBackgroundColor(Color.LTGRAY); // Header background color

            } else if (i >= 1 && i <= 4) {
                textView.setGravity(Gravity.CENTER);
            }
            textView.setGravity(Gravity.CENTER);

            row.addView(textView);
        }
        symptomTable.addView(row);
    }
    private void addTableRow3(String... values) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT, // Width
                TableLayout.LayoutParams.WRAP_CONTENT  // Height
        ));

        // Set column widths
        for (int i = 0; i < values.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(values[i]);
            textView.setPadding(16, 16, 16, 16); // Add padding for better spacing
            textView.setTextSize(12);
            textView.setBackgroundResource(R.drawable.table_border); // Apply border to each cell

            // Set layout parameters for the TextView
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, // Width (allow columns to expand)
                    TableRow.LayoutParams.WRAP_CONTENT  // Height
            );


            textView.setLayoutParams(params);

            row.addView(textView);
        }
        symptomTable.addView(row);
    }

    private void addTableRow1(String... values) {
        TableRow row = new TableRow(this);

        // Set column widths
        for (int i = 0; i < values.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(values[i]);
            textView.setPadding(8, 8, 8, 8);
            textView.setTextSize(10);

            // Set fixed width for the description column (last column)
            if (i == values.length - 1) {
                textView.setWidth(TableRow.LayoutParams.WRAP_CONTENT);
                //textView.setWidth(900); // Fixed width for description column
            } else {
                //textView.setWidth(TableRow.LayoutParams.WRAP_CONTENT); // Wrap content for other columns
                textView.setWidth(400); // Fixed width for description column
            }

            row.addView(textView);
        }
        symptomTable.addView(row);
    }

    private void addTableRow2(String... values) {
        TableRow row = new TableRow(this);
        for (String value : values) {
            TextView textView = new TextView(this);
            textView.setText(value);
            textView.setPadding(8, 8, 8, 8);
            textView.setTextSize(14);
            row.addView(textView);
        }
        symptomTable.addView(row);
    }

    private void clearTable() {
        symptomTable.removeAllViews();
    }
}

