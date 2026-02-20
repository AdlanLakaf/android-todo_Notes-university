# ToDoNotes App

A native Android application for managing personal notes and to-do tasks. Built with Java using Android SDK.

## Features

- **Notes** — Create, edit, delete, search, and share notes with category tags (General, Study, Work, Personal, Ideas)
- **To-Do Tasks** — Create tasks with priority levels (Low, Medium, High) and due dates, mark as complete/pending
- **Filter & Search** — Filter tasks by status (All, Pending, Completed) and search notes by title or content
- **Progress Tracking** — Visual progress bar showing task completion percentage

## Tech Stack

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Database**: SQLite (via `SQLiteOpenHelper`)
- **UI**: Material Design 3, RecyclerView, CardView, BottomNavigationView
- **Architecture**: Activity/Fragment pattern with Singleton database helper

## Project Structure

```
app/src/main/java/com/university/todonotes/
├── activities/          # MainActivity, AddNoteActivity, AddTodoActivity
├── adapters/            # NotesAdapter, TodoAdapter (RecyclerView)
├── database/            # DatabaseHelper (SQLite CRUD)
├── fragments/           # NotesFragment, TodoFragment
├── models/              # Note, TodoTask (data classes)
└── utils/               # ColorUtils, DateUtils (helpers)
```

## How to Build

1. Open the project in Android Studio
2. Sync Gradle files
3. Run on emulator or physical device (API 24+)

## Dependencies

- `androidx.appcompat` — Backward-compatible Activity/Fragment support
- `com.google.android.material` — Material Design 3 components
- `androidx.recyclerview` — Efficient scrollable lists
- `androidx.cardview` — Card-style UI containers
- `androidx.lifecycle` — Lifecycle-aware components
- `androidx.swiperefreshlayout` — Pull-to-refresh pattern
- `com.google.code.gson` — JSON serialization (available for future use)