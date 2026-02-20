package com.university.todonotes.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.university.todonotes.R;
import com.university.todonotes.activities.AddTodoActivity;
import com.university.todonotes.adapters.TodoAdapter;
import com.university.todonotes.database.DatabaseHelper;
import com.university.todonotes.models.TodoTask;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoFragment extends Fragment implements TodoAdapter.OnTodoClickListener {

    private RecyclerView recyclerTodo;
    private TodoAdapter todoAdapter;
    private LinearLayout emptyState;
    private ChipGroup chipGroupFilter;
    private Chip chipAll, chipPending, chipCompleted;
    private TextView tvTasksCount;
    private TextView tvProgressText;
    private ProgressBar progressTasks;
    private FloatingActionButton fabAddTodo;

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private Handler mainHandler;

    private ActivityResultLauncher<Intent> addTodoLauncher;
    private ActivityResultLauncher<Intent> editTodoLauncher;

    private int currentFilter = 0; // 0 = All, 1 = Pending, 2 = Completed

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        addTodoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadTasks();
                        showSnackbar("Task added successfully");
                    }
                });

        editTodoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadTasks();
                        showSnackbar("Task updated successfully");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        initViews(view);
        setupRecyclerView();
        setupFilterChips();
        setupFab();

        loadTasks();

        return view;
    }

    private void initViews(View view) {
        recyclerTodo = view.findViewById(R.id.recycler_todo);
        emptyState = view.findViewById(R.id.empty_state);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        chipAll = view.findViewById(R.id.chip_all);
        chipPending = view.findViewById(R.id.chip_pending);
        chipCompleted = view.findViewById(R.id.chip_completed);
        tvTasksCount = view.findViewById(R.id.tv_tasks_count);
        tvProgressText = view.findViewById(R.id.tv_progress_text);
        progressTasks = view.findViewById(R.id.progress_tasks);
        fabAddTodo = view.findViewById(R.id.fab_add_todo);
    }

    private void setupRecyclerView() {
        todoAdapter = new TodoAdapter();
        todoAdapter.setOnTodoClickListener(this);
        recyclerTodo.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerTodo.setAdapter(todoAdapter);
        recyclerTodo.setHasFixedSize(true);
    }

    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_all)) {
                currentFilter = 0;
            } else if (checkedIds.contains(R.id.chip_pending)) {
                currentFilter = 1;
            } else if (checkedIds.contains(R.id.chip_completed)) {
                currentFilter = 2;
            }
            loadTasks();
        });
    }

    private void setupFab() {
        fabAddTodo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddTodoActivity.class);
            addTodoLauncher.launch(intent);
        });
    }

    private void loadTasks() {
        executorService.execute(() -> {
            List<TodoTask> tasks;
            switch (currentFilter) {
                case 1:
                    tasks = databaseHelper.getPendingTodos();
                    break;
                case 2:
                    tasks = databaseHelper.getCompletedTodos();
                    break;
                case 0:
                default:
                    tasks = databaseHelper.getAllTodos();
                    break;
            }

            int totalTasks = databaseHelper.getTodosCount();
            int completedTasks = databaseHelper.getCompletedTodosCount();
            int progress = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;

            mainHandler.post(() -> {
                todoAdapter.setTasks(tasks);
                updateUI(tasks.size(), totalTasks, progress);
            });
        });
    }

    private void updateUI(int visibleCount, int totalCount, int progress) {
        tvTasksCount.setText(String.valueOf(totalCount));
        progressTasks.setProgress(progress);
        tvProgressText.setText(progress + "%");

        if (visibleCount == 0) {
            recyclerTodo.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerTodo.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTodoClick(TodoTask task, int position) {
        Intent intent = new Intent(requireContext(), AddTodoActivity.class);
        intent.putExtra("task", task);
        intent.putExtra("position", position);
        editTodoLauncher.launch(intent);
    }

    @Override
    public void onCheckboxClick(TodoTask task, int position) {
        task.setCompleted(!task.isCompleted());
        executorService.execute(() -> {
            databaseHelper.updateTodo(task);
            mainHandler.post(() -> {
                todoAdapter.updateTask(task, position);
                loadTasks(); // Reload to update progress
                String message = task.isCompleted() ? "Task completed!" : "Task marked as pending";
                showSnackbar(message);
            });
        });
    }

    @Override
    public void onMoreClick(TodoTask task, View anchorView, int position) {
        showOptionsMenu(task, anchorView, position);
    }

    private void showOptionsMenu(TodoTask task, View anchorView, int position) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView != null ? anchorView : recyclerTodo);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.todo_options_menu, popup.getMenu());

        // Update menu text based on completion status
        popup.getMenu().findItem(R.id.action_mark_complete).setTitle(
                task.isCompleted() ? "Mark as Pending" : "Mark as Complete");

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                Intent intent = new Intent(requireContext(), AddTodoActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("position", position);
                editTodoLauncher.launch(intent);
                return true;
            } else if (itemId == R.id.action_delete) {
                deleteTask(task, position);
                return true;
            } else if (itemId == R.id.action_mark_complete) {
                toggleTaskCompletion(task, position);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void toggleTaskCompletion(TodoTask task, int position) {
        task.setCompleted(!task.isCompleted());
        executorService.execute(() -> {
            databaseHelper.updateTodo(task);
            mainHandler.post(() -> {
                todoAdapter.updateTask(task, position);
                loadTasks();
                String message = task.isCompleted() ? "Task completed!" : "Task marked as pending";
                showSnackbar(message);
            });
        });
    }

    private void deleteTask(TodoTask task, int position) {
        executorService.execute(() -> {
            databaseHelper.deleteTodo(task.getId());
            mainHandler.post(() -> {
                todoAdapter.removeTask(position);
                loadTasks();
                showSnackbar("Task deleted");
            });
        });
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