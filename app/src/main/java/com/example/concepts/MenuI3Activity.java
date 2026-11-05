package com.example.concepts;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Menu for level I3, opened from {@link MainActivity}.
 * Delegates the opening sound and RETURN action to {@link TransitionSound}.
 */
public class MenuI3Activity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Loads the menu layout and configures the RETURN and activity buttons.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_i3);

        TransitionSound.bindMenu(this, savedInstanceState);
        findViewById(R.id.btn_activity_1).setOnClickListener(this);
    }

    /**
     * Handles clicks on the activity button. Opening the corresponding screen
     * is pending implementation.
     *
     * @param view view that received the click
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_activity_1) {
            // TODO: Open activity 1.
        }
    }

}