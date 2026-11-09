package com.example.concepts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Menu for level I3, opened from {@link MainActivity}.
 * Delegates the opening sound and RETURN action to {@link TransitionSound}.
 */
public class MenuI3Activity extends AppCompatActivity implements View.OnClickListener {

    /** Launches the game and receives its RETURN result. */
    private final ActivityResultLauncher<Intent> gameLauncher =
            TransitionSound.registerMenuLauncher(this);

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
     * Opens the I3 game when the activity button is clicked.
     *
     * @param view view that received the click
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_activity_1) {
            gameLauncher.launch(new Intent(this, FavoriteColorActivity.class));
        }
    }

}