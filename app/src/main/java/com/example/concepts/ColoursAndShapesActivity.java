package com.example.concepts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Colours and shapes game screen, opened from the I5 menu.
 */
public class ColoursAndShapesActivity extends AppCompatActivity {

    /**
     * Loads the colours and shapes layout.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colours_and_shapes);
    }
}
