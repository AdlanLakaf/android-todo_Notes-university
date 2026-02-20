package com.university.todonotes.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.university.todonotes.R;
import com.university.todonotes.database.DatabaseHelper;
import com.university.todonotes.models.TodoTask;
import com.university.todonotes.utils.DateUtils;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTodoActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnSave;
    private RadioGroup rgPriority;
    private RadioButton rbLow, rbMedium, rbHigh;
    private TextInputEditText etTitle;
    private TextInputEditText etDueDate;
    private TextInputEditText etDescription;

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private Handler mainHandler;

    private TodoTask existingTask;
    private int position = -1;
    private int selectedPriority = TodoTask.PRIORITY_LOW;
    private long selectedDueDate = 0;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        calendar = Calendar.getInstance();

        initViews();
        loadExistingTask();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        rgPriority = findViewById(R.id.rg_priority);
        rbLow = findViewById(R.id.rb_low);
        rbMedium = findViewById(R.id.rb_medium);
        rbHigh = findViewById(R.id.rb_high);
        etTitle = findViewById(R.id.et_title);
        etDueDate = findViewById(R.id.et_due_date);
        etDescription = findViewById(R.id.et_description);
    }

    private void loadExistingTask() {
        existingTask = (TodoTask) getIntent().getSerializableExtra("task");
        position = getIntent().getIntExtra("position", -1);

        if (existingTask != null) {
            etTitle.setText(existingTask.getTitle());
            etDescription.setText(existingTask.getDescription());
            selectedPriority = existingTask.getPriority();
            selectedDueDate = existingTask.getDueDate();

            // Set priority radio button
            switch (selectedPriority) {
                case TodoTask.PRIORITY_HIGH:
                    rbHigh.setChecked(true);
                    break;
                case TodoTask.PRIORITY_MEDIUM:
                    rbMedium.setChecked(true);
                    break;
                case TodoTask.PRIORITY_LOW:
                default:
                    rbLow.setChecked(true);
                    break;
            }

            // Set due date
            if (selectedDueDate > 0) {
                etDueDate.setText(DateUtils.formatDate(selectedDueDate));
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        rgPriority.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_high) {
                selectedPriority = TodoTask.PRIORITY_HIGH;
            } else if (checkedId == R.id.rb_medium) {
                selectedPriority = TodoTask.PRIORITY_MEDIUM;
            } else if (checkedId == R.id.rb_low) {
                selectedPriority = TodoTask.PRIORITY_LOW;
            }
        });

        etDueDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        Calendar currentCalendar = Calendar.getInstance();
        if (selectedDueDate > 0) {
            currentCalendar.setTimeInMillis(selectedDueDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth, 23, 59, 59);
                    selectedDueDate = calendar.getTimeInMillis();
                    etDueDate.setText(DateUtils.formatDate(selectedDueDate));
                },
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void saveTask() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (title.isEmpty()) {
            etTitle.setError("Please enter a task");
            etTitle.requestFocus();
            return;
        }

        btnSave.setEnabled(false);

        executorService.execute(() -> {
            long result;
            if (existingTask != null) {
                existingTask.setTitle(title);
                existingTask.setDescription(description);
                existingTask.setPriority(selectedPriority);
                existingTask.setDueDate(selectedDueDate);
                result = databaseHelper.updateTodo(existingTask);
            } else {
                TodoTask newTask = new TodoTask(title, description, selectedPriority, selectedDueDate);
                result = databaseHelper.insertTodo(newTask);
            }

            mainHandler.post(() -> {
                btnSave.setEnabled(true);
                if (result > 0) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("position", position);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Failed to save task", Snackbar.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}