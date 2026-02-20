package com.university.todonotes.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.university.todonotes.R;
import com.university.todonotes.activities.AddNoteActivity;
import com.university.todonotes.adapters.NotesAdapter;
import com.university.todonotes.database.DatabaseHelper;
import com.university.todonotes.models.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesFragment extends Fragment implements NotesAdapter.OnNoteClickListener {

    private RecyclerView recyclerNotes;
    private NotesAdapter notesAdapter;
    private LinearLayout emptyState;
    private EditText etSearch;
    private TextView tvNotesCount;
    private FloatingActionButton fabAddNote;

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private Handler mainHandler;

    private ActivityResultLauncher<Intent> addNoteLauncher;
    private ActivityResultLauncher<Intent> editNoteLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadNotes();
                        showSnackbar("Note added successfully");
                    }
                });

        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadNotes();
                        showSnackbar("Note updated successfully");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupFab();

        loadNotes();

        return view;
    }

    private void initViews(View view) {
        recyclerNotes = view.findViewById(R.id.recycler_notes);
        emptyState = view.findViewById(R.id.empty_state);
        etSearch = view.findViewById(R.id.et_search_notes);
        tvNotesCount = view.findViewById(R.id.tv_notes_count);
        fabAddNote = view.findViewById(R.id.fab_add_note);
    }

    private void setupRecyclerView() {
        notesAdapter = new NotesAdapter();
        notesAdapter.setOnNoteClickListener(this);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerNotes.setAdapter(notesAdapter);
        recyclerNotes.setHasFixedSize(true);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFab() {
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddNoteActivity.class);
            addNoteLauncher.launch(intent);
        });
    }

    private void loadNotes() {
        executorService.execute(() -> {
            List<Note> notes = databaseHelper.getAllNotes();
            mainHandler.post(() -> {
                notesAdapter.setNotes(notes);
                updateUI(notes.size());
            });
        });
    }

    private void searchNotes(String query) {
        executorService.execute(() -> {
            List<Note> notes;
            if (query.isEmpty()) {
                notes = databaseHelper.getAllNotes();
            } else {
                notes = databaseHelper.searchNotes(query);
            }
            mainHandler.post(() -> {
                notesAdapter.setNotes(notes);
                updateUI(notes.size());
            });
        });
    }

    private void updateUI(int count) {
        tvNotesCount.setText(String.valueOf(count));
        if (count == 0) {
            recyclerNotes.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerNotes.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNoteClick(Note note, int position) {
        Intent intent = new Intent(requireContext(), AddNoteActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("position", position);
        editNoteLauncher.launch(intent);
    }

    @Override
    public void onNoteLongClick(Note note, int position) {
        showOptionsMenu(note, null, position);
    }

    @Override
    public void onMoreClick(Note note, View anchorView, int position) {
        showOptionsMenu(note, anchorView, position);
    }

    private void showOptionsMenu(Note note, View anchorView, int position) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView != null ? anchorView : recyclerNotes);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.note_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                Intent intent = new Intent(requireContext(), AddNoteActivity.class);
                intent.putExtra("note", note);
                intent.putExtra("position", position);
                editNoteLauncher.launch(intent);
                return true;
            } else if (itemId == R.id.action_delete) {
                deleteNote(note, position);
                return true;
            } else if (itemId == R.id.action_share) {
                shareNote(note);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void deleteNote(Note note, int position) {
        executorService.execute(() -> {
            databaseHelper.deleteNote(note.getId());
            mainHandler.post(() -> {
                notesAdapter.removeNote(position);
                updateUI(notesAdapter.getItemCount());
                showSnackbar("Note deleted");
            });
        });
    }

    private void shareNote(Note note) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, note.getTitle() + "\n\n" + note.getContent());
        startActivity(Intent.createChooser(shareIntent, "Share note"));
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}