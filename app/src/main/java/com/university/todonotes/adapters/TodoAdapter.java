package com.university.todonotes.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.university.todonotes.R;
import com.university.todonotes.models.TodoTask;
import com.university.todonotes.utils.ColorUtils;
import com.university.todonotes.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoTask> tasks;
    private OnTodoClickListener listener;

    public interface OnTodoClickListener {
        void onTodoClick(TodoTask task, int position);
        void onCheckboxClick(TodoTask task, int position);
        void onMoreClick(TodoTask task, View anchorView, int position);
    }

    public TodoAdapter() {
        this.tasks = new ArrayList<>();
    }

    public void setOnTodoClickListener(OnTodoClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<TodoTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void addTask(TodoTask task) {
        tasks.add(0, task);
        notifyItemInserted(0);
    }

    public void updateTask(TodoTask task, int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.set(position, task);
            notifyItemChanged(position);
        }
    }

    public void removeTask(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<TodoTask> getTasks() {
        return tasks;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoTask task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView ivCheckbox;
        private TextView tvTitle;
        private TextView tvDueDate;
        private TextView tvPriority;
        private ImageView btnMore;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivCheckbox = itemView.findViewById(R.id.iv_checkbox);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvPriority = itemView.findViewById(R.id.tv_priority);
            btnMore = itemView.findViewById(R.id.btn_more);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTodoClick(tasks.get(position), position);
                }
            });

            ivCheckbox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCheckboxClick(tasks.get(position), position);
                }
            });

            btnMore.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMoreClick(tasks.get(position), btnMore, position);
                }
            });
        }

        public void bind(TodoTask task) {
            tvTitle.setText(task.getTitle());
            tvDueDate.setText(DateUtils.getDueDateText(task.getDueDate()));
            tvPriority.setText(task.getPriorityText());

            // Checkbox state
            if (task.isCompleted()) {
                ivCheckbox.setImageResource(R.drawable.ic_checkbox_checked);
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setAlpha(0.5f);
            } else {
                ivCheckbox.setImageResource(R.drawable.ic_checkbox_unchecked);
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setAlpha(1.0f);
            }

            // Priority badge
            int bgRes = ColorUtils.getPriorityBackground(task.getPriority());
            int colorRes = ColorUtils.getPriorityColor(task.getPriority());
            tvPriority.setBackgroundResource(bgRes);
            tvPriority.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));

            // Due date color
            if (task.getDueDate() > 0 && !task.isCompleted()) {
                long now = System.currentTimeMillis();
                if (task.getDueDate() < now) {
                    tvDueDate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.priority_high));
                } else if (task.getDueDate() - now < 24 * 60 * 60 * 1000) {
                    tvDueDate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.priority_medium));
                } else {
                    tvDueDate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_hint));
                }
            } else {
                tvDueDate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_hint));
            }
        }
    }
}