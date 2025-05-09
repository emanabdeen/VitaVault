package com.example.insight.view;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.insight.R;
import com.example.insight.databinding.ActivitySymptomReportBinding;
import com.example.insight.model.Symptom;
import com.example.insight.model.UserAccount;
import com.example.insight.utility.DatePickerValidator;
import com.example.insight.utility.StringHandler;
import com.example.insight.utility.SymptomsCategories;
import com.example.insight.utility.TimeValidator;
import com.example.insight.viewmodel.AccountViewModel;
import com.example.insight.viewmodel.SymptomViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SymptomReportActivity extends DrawerBaseActivity {

    ActivitySymptomReportBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private SymptomViewModel symptomViewModel;
    AccountViewModel viewModel;
    UserAccount userAccount;
    private String selectedType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySymptomReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Report");

        // Initialize ViewModel
        symptomViewModel = new ViewModelProvider(this).get(SymptomViewModel.class);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        viewModel.GetUserProfile();

        //to observe the data and display it
        viewModel.getUserAccountData().observe(this, userAccountData -> {
            userAccount = userAccountData;
        });



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
                R.layout.custom_spinner_selected_item // Default layout for Spinner items
        );
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Dropdown layout
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item); // Dropdown layout
        binding.symptomTypeSpinner.setAdapter(adapter);

        // Set a default selection (All Symptoms) first item
        binding.symptomTypeSpinner.setSelection(0);
        binding.symptomTypeSpinner.setDropDownWidth(510); // Set width in pixels

        // Handle Spinner item selection
        binding.symptomTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString().toLowerCase(Locale.ROOT);
                if (s.equals("all symptoms")) {
                    selectedType = "";
                } else {
                    String input = parent.getItemAtPosition(position).toString();
                    selectedType = SymptomsCategories.getEnumNameBySymptomCategory(input);
                }
                Log.d("activity", "Selected: " + selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        binding.btnClearFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.startDateInput.setText("");
            }
        });
        binding.btnClearTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.endDateInput.setText("");
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayMillis = calendar.getTimeInMillis();

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setEnd(todayMillis)
                .setValidator(new DatePickerValidator())
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(todayMillis)
                .setCalendarConstraints(constraints)
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert the selection (UTC millis) into UTC calendar to extract the correct date
            Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            utcCalendar.setTimeInMillis(selection);

            int year = utcCalendar.get(Calendar.YEAR);
            int month = utcCalendar.get(Calendar.MONTH) + 1; // MONTH is 0-based
            int day = utcCalendar.get(Calendar.DAY_OF_MONTH);

            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            dateInput.setText(selectedDate);
        });

    }

    private void searchSymptomsByDateRange(String symptomType) {
        // Get selected dates from input fields
        String startDateStr = StringHandler.defaultIfNull(binding.startDateInput.getText().toString());
        String endDateStr = StringHandler.defaultIfNull(binding.endDateInput.getText().toString());

        //Get the data from firestore
        clearTable();
        symptomViewModel.GetSymptomsByDateRange(startDateStr, endDateStr, symptomType);

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
                    SymptomsCategories.valueOf(symptom.getSymptomName()).getSymptom(),
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
        headerPaint.setTextSize(16);
        canvas.drawText(appName, startX, startY, headerPaint);
        startY += 30; // Move Y position down



        // User data ----------------------------------------------------------------------------------

        String userEmail = ""; // Replace with actual user email
        String userAgeRange = ""; // Replace with actual age range
        String userGender = ""; // Replace with actual gender

        if (userAccount != null) {
            userAgeRange= userAccount.getAgeRange();
            userGender = userAccount.getGender();
        }


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

