package com.university.todonotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.university.todonotes.models.Note;
import com.university.todonotes.models.TodoTask;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todonotes.db";
    private static final int DATABASE_VERSION = 1;

    // Notes table
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_NOTE_ID = "id";
    private static final String COLUMN_NOTE_TITLE = "title";
    private static final String COLUMN_NOTE_CONTENT = "content";
    private static final String COLUMN_NOTE_CATEGORY = "category";
    private static final String COLUMN_NOTE_CREATED_AT = "created_at";
    private static final String COLUMN_NOTE_UPDATED_AT = "updated_at";

    // Todo table
    private static final String TABLE_TODO = "todo";
    private static final String COLUMN_TODO_ID = "id";
    private static final String COLUMN_TODO_TITLE = "title";
    private static final String COLUMN_TODO_DESCRIPTION = "description";
    private static final String COLUMN_TODO_PRIORITY = "priority";
    private static final String COLUMN_TODO_DUE_DATE = "due_date";
    private static final String COLUMN_TODO_COMPLETED = "completed";
    private static final String COLUMN_TODO_CREATED_AT = "created_at";
    private static final String COLUMN_TODO_COMPLETED_AT = "completed_at";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create notes table
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                COLUMN_NOTE_CONTENT + " TEXT, " +
                COLUMN_NOTE_CATEGORY + " TEXT, " +
                COLUMN_NOTE_CREATED_AT + " INTEGER, " +
                COLUMN_NOTE_UPDATED_AT + " INTEGER)";

        // Create todo table
        String createTodoTable = "CREATE TABLE " + TABLE_TODO + " (" +
                COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TODO_TITLE + " TEXT NOT NULL, " +
                COLUMN_TODO_DESCRIPTION + " TEXT, " +
                COLUMN_TODO_PRIORITY + " INTEGER, " +
                COLUMN_TODO_DUE_DATE + " INTEGER, " +
                COLUMN_TODO_COMPLETED + " INTEGER, " +
                COLUMN_TODO_CREATED_AT + " INTEGER, " +
                COLUMN_TODO_COMPLETED_AT + " INTEGER)";

        db.execSQL(createNotesTable);
        db.execSQL(createTodoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    // ==================== NOTES CRUD ====================

    public long insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getContent());
        values.put(COLUMN_NOTE_CATEGORY, note.getCategory());
        values.put(COLUMN_NOTE_CREATED_AT, note.getCreatedAt());
        values.put(COLUMN_NOTE_UPDATED_AT, note.getUpdatedAt());

        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getContent());
        values.put(COLUMN_NOTE_CATEGORY, note.getCategory());
        values.put(COLUMN_NOTE_UPDATED_AT, System.currentTimeMillis());

        int rows = db.update(TABLE_NOTES, values, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
        return rows;
    }

    public void deleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(noteId)});
        db.close();
    }

    public Note getNote(long noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(noteId)}, null, null, null);

        Note note = null;
        if (cursor.moveToFirst()) {
            note = cursorToNote(cursor);
        }
        cursor.close();
        db.close();
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, null, null, null, null,
                COLUMN_NOTE_UPDATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    public List<Note> searchNotes(String query) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "%" + query + "%";
        Cursor cursor = db.query(TABLE_NOTES, null,
                COLUMN_NOTE_TITLE + " LIKE ? OR " + COLUMN_NOTE_CONTENT + " LIKE ?",
                new String[]{searchQuery, searchQuery}, null, null,
                COLUMN_NOTE_UPDATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    public int getNotesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT)));
        note.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CATEGORY)));
        note.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CREATED_AT)));
        note.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NOTE_UPDATED_AT)));
        return note;
    }

    // ==================== TODO CRUD ====================

    public long insertTodo(TodoTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TODO_TITLE, task.getTitle());
        values.put(COLUMN_TODO_DESCRIPTION, task.getDescription());
        values.put(COLUMN_TODO_PRIORITY, task.getPriority());
        values.put(COLUMN_TODO_DUE_DATE, task.getDueDate());
        values.put(COLUMN_TODO_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_TODO_CREATED_AT, task.getCreatedAt());
        values.put(COLUMN_TODO_COMPLETED_AT, task.getCompletedAt());

        long id = db.insert(TABLE_TODO, null, values);
        db.close();
        return id;
    }

    public int updateTodo(TodoTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TODO_TITLE, task.getTitle());
        values.put(COLUMN_TODO_DESCRIPTION, task.getDescription());
        values.put(COLUMN_TODO_PRIORITY, task.getPriority());
        values.put(COLUMN_TODO_DUE_DATE, task.getDueDate());
        values.put(COLUMN_TODO_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_TODO_COMPLETED_AT, task.getCompletedAt());

        int rows = db.update(TABLE_TODO, values, COLUMN_TODO_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return rows;
    }

    public void deleteTodo(long todoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_TODO_ID + " = ?",
                new String[]{String.valueOf(todoId)});
        db.close();
    }

    public TodoTask getTodo(long todoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODO, null, COLUMN_TODO_ID + " = ?",
                new String[]{String.valueOf(todoId)}, null, null, null);

        TodoTask task = null;
        if (cursor.moveToFirst()) {
            task = cursorToTodo(cursor);
        }
        cursor.close();
        db.close();
        return task;
    }

    public List<TodoTask> getAllTodos() {
        List<TodoTask> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODO, null, null, null, null, null,
                COLUMN_TODO_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTodo(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public List<TodoTask> getTodosByStatus(boolean completed) {
        List<TodoTask> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODO, null,
                COLUMN_TODO_COMPLETED + " = ?",
                new String[]{completed ? "1" : "0"}, null, null,
                COLUMN_TODO_PRIORITY + " DESC, " + COLUMN_TODO_DUE_DATE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTodo(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public List<TodoTask> getPendingTodos() {
        return getTodosByStatus(false);
    }

    public List<TodoTask> getCompletedTodos() {
        return getTodosByStatus(true);
    }

    public int getTodosCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TODO, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getCompletedTodosCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TODO + 
                " WHERE " + COLUMN_TODO_COMPLETED + " = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    private TodoTask cursorToTodo(Cursor cursor) {
        TodoTask task = new TodoTask();
        task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TODO_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_TITLE)));
        task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_DESCRIPTION)));
        task.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_PRIORITY)));
        task.setDueDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TODO_DUE_DATE)));
        task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_COMPLETED)) == 1);
        task.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TODO_CREATED_AT)));
        task.setCompletedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TODO_COMPLETED_AT)));
        return task;
    }
}