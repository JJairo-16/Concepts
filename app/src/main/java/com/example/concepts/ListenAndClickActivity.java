package com.example.concepts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Listen and click game screen, opened from the I4 menu.
 */
public class ListenAndClickActivity extends AppCompatActivity {

    /**
     * Loads the listen and click layout.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_and_click);
    }
}
