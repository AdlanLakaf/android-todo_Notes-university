package com.university.todonotes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MMM dd", Locale.getDefault());

    public static String formatDate(long timestamp) {
        if (timestamp == 0) return "";
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        if (timestamp == 0) return "";
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatShortDate(long timestamp) {
        if (timestamp == 0) return "";
        return SHORT_DATE_FORMAT.format(new Date(timestamp));
    }

    public static String getRelativeTime(long timestamp) {
        if (timestamp == 0) return "";
        
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            return formatDate(timestamp);
        }
    }

    public static String getDueDateText(long dueDate) {
        if (dueDate == 0) return "No due date";
        
        long now = System.currentTimeMillis();
        long diff = dueDate - now;
        
        long days = diff / (1000 * 60 * 60 * 24);
        
        if (diff < 0) {
            return "Overdue";
        } else if (days == 0) {
            return "Due today";
        } else if (days == 1) {
            return "Due tomorrow";
        } else {
            return "Due " + formatShortDate(dueDate);
        }
    }
}