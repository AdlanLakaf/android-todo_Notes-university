package com.university.todonotes.models;

import java.io.Serializable;

public class Note implements Serializable {
    private long id;
    private String title;
    private String content;
    private String category;
    private long createdAt;
    private long updatedAt;

    // Categories
    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_STUDY = "Study";
    public static final String CATEGORY_WORK = "Work";
    public static final String CATEGORY_PERSONAL = "Personal";
    public static final String CATEGORY_IDEAS = "Ideas";

    public Note() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.category = CATEGORY_GENERAL;
    }

    public Note(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPreview() {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
}