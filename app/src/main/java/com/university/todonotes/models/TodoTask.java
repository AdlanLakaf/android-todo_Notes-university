package com.university.todonotes.models;

import java.io.Serializable;

public class TodoTask implements Serializable {
    private long id;
    private String title;
    private String description;
    private int priority; // 0 = Low, 1 = Medium, 2 = High
    private long dueDate;
    private boolean completed;
    private long createdAt;
    private long completedAt;

    // Priority constants
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;

    public TodoTask() {
        this.createdAt = System.currentTimeMillis();
        this.completed = false;
        this.priority = PRIORITY_LOW;
    }

    public TodoTask(String title, String description, int priority, long dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            this.completedAt = System.currentTimeMillis();
        }
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public String getPriorityText() {
        switch (priority) {
            case PRIORITY_HIGH:
                return "High";
            case PRIORITY_MEDIUM:
                return "Medium";
            case PRIORITY_LOW:
            default:
                return "Low";
        }
    }
}