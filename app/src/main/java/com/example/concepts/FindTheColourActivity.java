package com.example.concepts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Find the colour game screen, opened from the I5 menu.
 */
public class FindTheColourActivity extends AppCompatActivity {

    /**
     * Loads the find the colour layout.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_the_colour);
    }
}
