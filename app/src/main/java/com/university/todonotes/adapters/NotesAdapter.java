package com.university.todonotes.adapters;

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
import com.university.todonotes.models.Note;
import com.university.todonotes.utils.ColorUtils;
import com.university.todonotes.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note, int position);
        void onNoteLongClick(Note note, int position);
        void onMoreClick(Note note, View anchorView, int position);
    }

    public NotesAdapter() {
        this.notes = new ArrayList<>();
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public void addNote(Note note) {
        notes.add(0, note);
        notifyItemInserted(0);
    }

    public void updateNote(Note note, int position) {
        if (position >= 0 && position < notes.size()) {
            notes.set(position, note);
            notifyItemChanged(position);
        }
    }

    public void removeNote(int position) {
        if (position >= 0 && position < notes.size()) {
            notes.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<Note> getNotes() {
        return notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvCategory;
        private TextView tvDate;
        private TextView tvTitle;
        private TextView tvContent;
        private ImageView btnMore;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            btnMore = itemView.findViewById(R.id.btn_more);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onNoteClick(notes.get(position), position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onNoteLongClick(notes.get(position), position);
                    return true;
                }
                return false;
            });

            btnMore.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMoreClick(notes.get(position), btnMore, position);
                }
            });
        }

        public void bind(Note note) {
            tvCategory.setText(note.getCategory());
            tvDate.setText(DateUtils.getRelativeTime(note.getUpdatedAt()));
            tvTitle.setText(note.getTitle());
            tvContent.setText(note.getPreview());

            // Set category badge color
            int colorRes = ColorUtils.getCategoryColor(note.getCategory());
            int color = ContextCompat.getColor(itemView.getContext(), colorRes);
            tvCategory.setTextColor(color);
            tvCategory.getBackground().setTint(color & 0x20FFFFFF | 0x10000000);
        }
    }
}