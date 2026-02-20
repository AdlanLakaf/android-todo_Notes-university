package com.university.todonotes.utils;

import com.university.todonotes.R;
import com.university.todonotes.models.Note;

public class ColorUtils {

    public static int getCategoryColor(String category) {
        if (category == null) return R.color.category_purple;
        
        switch (category) {
            case Note.CATEGORY_STUDY:
                return R.color.category_blue;
            case Note.CATEGORY_WORK:
                return R.color.category_green;
            case Note.CATEGORY_PERSONAL:
                return R.color.category_orange;
            case Note.CATEGORY_IDEAS:
                return R.color.category_pink;
            case Note.CATEGORY_GENERAL:
            default:
                return R.color.category_purple;
        }
    }

    public static int getPriorityColor(int priority) {
        switch (priority) {
            case 2: // High
                return R.color.priority_high;
            case 1: // Medium
                return R.color.priority_medium;
            case 0: // Low
            default:
                return R.color.priority_low;
        }
    }

    public static int getPriorityBackground(int priority) {
        switch (priority) {
            case 2: // High
                return R.drawable.bg_priority_high;
            case 1: // Medium
                return R.drawable.bg_priority_medium;
            case 0: // Low
            default:
                return R.drawable.bg_priority_low;
        }
    }
}