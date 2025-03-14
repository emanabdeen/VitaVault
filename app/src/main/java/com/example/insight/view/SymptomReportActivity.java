package com.example.insight.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.Locale;

public class SymptomReportActivity extends DrawerBaseActivity {

    ActivitySymptomReportBinding binding;
    private SymptomViewModel symptomViewModel;
    private String selectedType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySymptomReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Report");

        // Initialize ViewModel
        symptomViewModel = new ViewModelProvider(this).get(SymptomViewModel.class);

        // Set up date pickers for start and end date inputs
        binding.startDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(binding.startDateInput);
            }
        });
        binding.endDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(binding.endDateInput);
            }
        });

        // Populate the Spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.symptom_types, // Reference to the string array
                android.R.layout.simple_spinner_item // Default layout for Spinner items
        );
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Dropdown layout
        adapter.setDropDownViewResource(R.layout.custom_spinner_item); // Dropdown layout
        binding.symptomTypeSpinner.setAdapter(adapter);

        // Set a default selection (All Symptoms) first item
        binding.symptomTypeSpinner.setSelection(0);

        // Handle Spinner item selection
        binding.symptomTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString().toLowerCase(Locale.ROOT);
                if (s.equals("all symptoms")) {
                    selectedType = "";
                } else {
                    selectedType = parent.getItemAtPosition(position).toString();
                }
                Log.d("activity", "Selected: " + selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up the search button click listener
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchSymptomsByDateRange(selectedType);
            }
        });

        // Set up the export button click listener
        binding.exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportTableToPdf();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { // Request code for WRITE_EXTERNAL_STORAGE
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with exporting the document
                exportTableToPdf();
            } else {
                // Permission denied, show a message to the user
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

    private void searchSymptomsByDateRange(String symptomType) {
        // Get selected dates from input fields
        String startDateStr = binding.startDateInput.getText().toString();
        String endDateStr = binding.endDateInput.getText().toString();

        //if no dates are selected, get all symptoms
        if (startDateStr.isEmpty() && endDateStr.isEmpty()) {
            clearTable();
            //if no dates are selected
            symptomViewModel.GetAllSymptoms();
        } else {
            clearTable();
            //if dates are selected
            symptomViewModel.GetSymptomsByDateRange(startDateStr, endDateStr, symptomType);
        }

        // Observe the LiveData to update the table
        symptomViewModel.getSymptomsData().observe(this, symptoms -> {
            if (symptoms != null && !symptoms.isEmpty()) {
                binding.symptomTable.setVisibility(View. VISIBLE);//show the table
                binding.exportButton.setVisibility(View.VISIBLE);//show the export button
                binding.txtMessage.setVisibility(View.GONE);
                updateTable(symptoms);
            } else {
                // If no symptoms are found, show the search result message
                symptomViewModel.getSearchResultMessageData().observe(this, searchResultMessageData -> {
                    if (searchResultMessageData != null && !searchResultMessageData.isEmpty()) {
                        clearTable(); // Clear the table before adding the message
                        //addTableRow(searchResultMessageData); // Add the message to the table

                        binding.txtMessage.setText(searchResultMessageData);
                        binding.txtMessage.setVisibility(View.VISIBLE);//show the message
                        binding.symptomTable.setVisibility(View. GONE);//hide the table
                        binding.exportButton.setVisibility(View.GONE);//hide the export button
                    }
                });
            }
        });
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
            if (binding.symptomTable.getChildCount() == 0) { // Check if it's the header row
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setBackgroundColor(getResources().getColor(R.color.accent_light)); // Header background color
                textView.setTextColor(Color.BLACK); // text color

            } else if (i >= 1 && i <= 4) {
                textView.setGravity(Gravity.CENTER);
            }
            textView.setGravity(Gravity.CENTER);

            row.addView(textView);
        }
        binding.symptomTable.addView(row);
    }

    private void exportTableToPdf() {

        // For Android 11 (API 30) and higher, check if the user granted the MANAGE_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // Request the MANAGE_EXTERNAL_STORAGE permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return; // Exit the method and wait for the user to grant permission
            }
        }

        // Create a new PdfDocument
        PdfDocument pdfDocument = new PdfDocument();

        // Define the page width and height (A4 size in points, landscape)
        int pageWidth = 842;  // A4 height in points (landscape)
        int pageHeight = 595; // A4 width in points (landscape)

        // Define margins
        int margin = 50;
        int startX = margin;
        int startY = margin;

        // Start the first page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Set the background color of the canvas to white
        canvas.drawColor(Color.WHITE);

        // Define text paint for headers and content
        TextPaint headerPaint = new TextPaint();
        headerPaint.setColor(Color.BLACK);
        headerPaint.setTextSize(12);
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        TextPaint contentPaint = new TextPaint();
        contentPaint.setColor(Color.BLACK);
        contentPaint.setTextSize(10);
        contentPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // Define border paint
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);

        // Define text paint for user data
        TextPaint userDataPaint = new TextPaint();
        userDataPaint.setColor(Color.BLACK);
        userDataPaint.setTextSize(12);
        userDataPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));


        // Load the app logo from resources-------------------------------------------------------------------------------
        //Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.vitavault_icon_report3);
        String appName=" VitaVault ";

        // Draw the logo at the top of the page
        /*int logoWidth = 2; // Adjust the width as needed
        int logoHeight = (int) (logo.getHeight() * ((float) logoWidth / logo.getWidth())); // Maintain aspect ratio
        canvas.drawBitmap(logo, startX, startY, null);
        startY += logoHeight + 40; // Add spacing below the logo*/
        headerPaint.setTextSize(16);
        canvas.drawText(appName, startX, startY, headerPaint);
        startY += 30; // Move Y position down



        // User data ----------------------------------------------------------------------------------
        String userEmail = "user@example.com"; // Replace with actual user email
        String userAgeRange = "25-34"; // Replace with actual age range
        String userGender = "Male"; // Replace with actual gender

        // Draw user data
        // canvas.drawText("Email: " + userEmail, startX, startY, userDataPaint);
        //startY += 20; // Move Y position down
        canvas.drawText("Age Range: " + userAgeRange, startX, startY, userDataPaint);
        startY += 20; // Move Y position down
        canvas.drawText("Gender: " + userGender, startX, startY, userDataPaint);
        startY += 30; // Add spacing before the table


        // Define column widths for 6 columns --------------------------------------------------------------------
        int[] columnWidths = {150, 80, 100, 100, 100, 200};

        // Draw the table headers
        headerPaint.setTextSize(12);
        String[] headers = {"Symptom Name", "Level", "Date", "Start Time", "End Time", "Description"};
        startY = drawTableRow(canvas, headers, startX, startY, columnWidths, headerPaint, borderPaint);
        startY += 10; // Add spacing between header and rows

        // Draw the table rows
        for (int i = 1; i < binding.symptomTable.getChildCount(); i++) {
            TableRow row = (TableRow) binding.symptomTable.getChildAt(i);
            String[] rowData = new String[row.getChildCount()];
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView textView = (TextView) row.getChildAt(j);
                rowData[j] = textView.getText().toString();
            }

            // Check if the current row exceeds the page height
            if (startY + 50 > pageHeight - margin) { // 50 is an estimated row height
                // Finish the current page
                pdfDocument.finishPage(page);

                // Start a new page
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                canvas.drawColor(Color.WHITE); // Set background color
                startY = margin; // Reset Y position for the new page

                // Redraw the table headers on the new page
                startY = drawTableRow(canvas, headers, startX, startY, columnWidths, headerPaint, borderPaint);
                startY += 10; // Add spacing between header and rows
            }

            // Draw the current row
            startY = drawTableRow(canvas, rowData, startX, startY, columnWidths, contentPaint, borderPaint);
        }

        // Finish the last page
        pdfDocument.finishPage(page);

        // Save the PDF to a file
        String fileName = "SymptomReport_" + System.currentTimeMillis() + ".pdf";
        savePdfToFile(pdfDocument, fileName);

        // Close the document
        pdfDocument.close();
    }

    private int drawTableRow(Canvas canvas, String[] data, int startX, int startY, int[] columnWidths, TextPaint textPaint, Paint borderPaint) {
        int x = startX;
        int y = startY;
        int maxHeight = 0; // Track the maximum height of the row

        // Draw each cell in the row
        for (int i = 0; i < data.length; i++) {
            // Calculate text height
            StaticLayout staticLayout = new StaticLayout(
                    data[i],
                    textPaint,
                    columnWidths[i],
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
            );
            int textHeight = staticLayout.getHeight();

            // Draw cell border
            canvas.drawRect(x, y, x + columnWidths[i], y + textHeight + 10, borderPaint); // Use dynamic height

            // Draw cell text
            canvas.save();
            canvas.translate(x + 5, y + 5); // Add padding inside the cell
            staticLayout.draw(canvas);
            canvas.restore();

            // Update maximum height
            if (textHeight > maxHeight) {
                maxHeight = textHeight;
            }

            // Move to the next column
            x += columnWidths[i];
        }

        // Return the Y position for the next row
        return y + maxHeight + 10; // Add spacing between rows
    }

    private void savePdfToFile(PdfDocument pdfDocument, String fileName) {
        // Use MediaStore to save the file in the Downloads directory
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Insert the file into the Downloads directory
        Uri fileUri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);

        if (fileUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(fileUri)) {
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream);
                    Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearTable() {
        binding.symptomTable.removeAllViews();
    }

}

