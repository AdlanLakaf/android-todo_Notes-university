package com.university.todonotes.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.university.todonotes.R;
import com.university.todonotes.fragments.NotesFragment;
import com.university.todonotes.fragments.TodoFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Fragment notesFragment;
    private Fragment todoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();

        // Show Notes fragment by default
        if (savedInstanceState == null) {
            showFragment(0);
        }
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_notes) {
                showFragment(0);
                return true;
            } else if (itemId == R.id.nav_todo) {
                showFragment(1);
                return true;
            }
            return false;
        });
    }

    private void showFragment(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Hide all fragments first
        if (notesFragment != null) transaction.hide(notesFragment);
        if (todoFragment != null) transaction.hide(todoFragment);

        switch (index) {
            case 0:
                if (notesFragment == null) {
                    notesFragment = new NotesFragment();
                    transaction.add(R.id.fragment_container, notesFragment);
                } else {
                    transaction.show(notesFragment);
                }
                break;
            case 1:
                if (todoFragment == null) {
                    todoFragment = new TodoFragment();
                    transaction.add(R.id.fragment_container, todoFragment);
                } else {
                    transaction.show(todoFragment);
                }
                break;
        }

        transaction.commit();
    }
}