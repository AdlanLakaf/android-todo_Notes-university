# Technical Summary — What Was Built & How

## App Architecture

The app uses the **Activity + Fragment** pattern with a **Singleton SQLite database**.

- `MainActivity` holds a `BottomNavigationView` and swaps between two fragments
- Each fragment manages its own RecyclerView, adapter, and data loading
- Database operations run on a background thread using `ExecutorService`, results posted to UI via `Handler`

---

## Files & What They Do

### Activities (`activities/`)
| File | Purpose |
|------|---------|
| `MainActivity.java` | Entry point. Hosts bottom nav, switches between Notes and Todo fragments |
| `AddNoteActivity.java` | Form to create/edit a note (title, content, category chips) |
| `AddTodoActivity.java` | Form to create/edit a task (title, description, priority, due date picker) |

### Fragments (`fragments/`)
| File | Purpose |
|------|---------|
| `NotesFragment.java` | Displays notes list, handles search, delete, share, empty state |
| `TodoFragment.java` | Displays tasks list, handles filter chips, checkbox toggle, progress bar |

### Adapters (`adapters/`)
| File | Purpose |
|------|---------|
| `NotesAdapter.java` | RecyclerView adapter — binds Note data to `item_note.xml` layout |
| `TodoAdapter.java` | RecyclerView adapter — binds TodoTask data to `item_todo.xml` layout |

### Models (`models/`)
| File | Purpose |
|------|---------|
| `Note.java` | Data class — id, title, content, category, timestamps. Implements `Serializable` |
| `TodoTask.java` | Data class — id, title, description, priority, dueDate, completed, timestamps |

### Database (`database/`)
| File | Purpose |
|------|---------|
| `DatabaseHelper.java` | Singleton `SQLiteOpenHelper` — creates tables, handles all CRUD operations |

### Utils (`utils/`)
| File | Purpose |
|------|---------|
| `ColorUtils.java` | Maps category names to color resources, priority levels to backgrounds |
| `DateUtils.java` | Formats timestamps to readable strings ("Just now", "2 days ago", "Due tomorrow") |

---

## Layouts (`res/layout/`)
| File | Purpose |
|------|---------|
| `activity_main.xml` | FrameLayout + BottomNavigationView |
| `activity_add_note.xml` | Toolbar + ChipGroup (categories) + TextInputLayouts |
| `activity_add_todo.xml` | Toolbar + RadioGroup (priority) + TextInputLayouts + DatePicker trigger |
| `fragment_notes.xml` | Header with search bar + RecyclerView + empty state |
| `fragment_todo.xml` | Header with progress bar + filter chips + RecyclerView + empty state |
| `item_note.xml` | CardView — category badge, date, title, content preview |
| `item_todo.xml` | CardView — checkbox, title, due date, priority badge |

---

## Key Technologies Used

| Technology | Why It's Used |
|-----------|---------------|
| `SQLiteOpenHelper` | Local database — stores notes and tasks on device, no internet needed |
| `RecyclerView` | Efficiently displays long scrollable lists without loading everything at once |
| `Material Design 3` | Google's latest design system — chips, buttons, text fields, bottom nav |
| `ExecutorService` | Runs database queries off the main thread so the UI doesn't freeze |
| `Handler + Looper` | Posts results back to the main thread to update the UI after background work |
| `ActivityResultLauncher` | Modern way to launch an activity and get a result back (replaced `startActivityForResult`) |
| `Serializable` | Allows passing Note/TodoTask objects between activities via Intent extras |
| `PopupMenu` | Shows edit/delete/share options when tapping the "..." button on a card |
| `ChipGroup` | Category and filter selection with single-select behavior |
| `DatePickerDialog` | Native Android date picker for selecting due dates |

---

## Color & Theme System

All colors are defined in `res/values/colors.xml` and referenced by name everywhere. Changing a color in one place updates the entire app. The gradient header and buttons all use `@color/primary` and `@color/gradient_start/end`.

Chip state colors (checked vs unchecked) are handled by **color state list selectors** in `res/color/` — XML files that define different colors based on the widget's state (`state_checked`, etc.).
