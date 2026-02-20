package com.university.todonotes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.university.todonotes.R;
import com.university.todonotes.database.DatabaseHelper;
import com.university.todonotes.models.Note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddNoteActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnSave;
    private ChipGroup chipGroupCategory;
    private TextInputEditText etTitle;
    private TextInputEditText etContent;

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private Handler mainHandler;

    private Note existingNote;
    private int position = -1;
    private String selectedCategory = Note.CATEGORY_GENERAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupCategoryChips();
        loadExistingNote();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        chipGroupCategory = findViewById(R.id.chip_group_category);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
    }

    private void setupCategoryChips() {
        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_general) {
                selectedCategory = Note.CATEGORY_GENERAL;
            } else if (checkedId == R.id.chip_study) {
                selectedCategory = Note.CATEGORY_STUDY;
            } else if (checkedId == R.id.chip_work) {
                selectedCategory = Note.CATEGORY_WORK;
            } else if (checkedId == R.id.chip_personal) {
                selectedCategory = Note.CATEGORY_PERSONAL;
            } else if (checkedId == R.id.chip_ideas) {
                selectedCategory = Note.CATEGORY_IDEAS;
            }
        });
    }

    private void loadExistingNote() {
        existingNote = (Note) getIntent().getSerializableExtra("note");
        position = getIntent().getIntExtra("position", -1);

        if (existingNote != null) {
            etTitle.setText(existingNote.getTitle());
            etContent.setText(existingNote.getContent());
            selectedCategory = existingNote.getCategory();

            // Select the appropriate chip
            int chipId = getChipIdForCategory(selectedCategory);
            chipGroupCategory.check(chipId);
        }
    }

    private int getChipIdForCategory(String category) {
        switch (category) {
            case Note.CATEGORY_STUDY:
                return R.id.chip_study;
            case Note.CATEGORY_WORK:
                return R.id.chip_work;
            case Note.CATEGORY_PERSONAL:
                return R.id.chip_personal;
            case Note.CATEGORY_IDEAS:
                return R.id.chip_ideas;
            case Note.CATEGORY_GENERAL:
            default:
                return R.id.chip_general;
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";

        if (title.isEmpty()) {
            etTitle.setError("Please enter a title");
            etTitle.requestFocus();
            return;
        }

        btnSave.setEnabled(false);

        executorService.execute(() -> {
            long result;
            if (existingNote != null) {
                existingNote.setTitle(title);
                existingNote.setContent(content);
                existingNote.setCategory(selectedCategory);
                result = databaseHelper.updateNote(existingNote);
            } else {
                Note newNote = new Note(title, content, selectedCategory);
                result = databaseHelper.insertNote(newNote);
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
                            "Failed to save note", Snackbar.LENGTH_SHORT).show();
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