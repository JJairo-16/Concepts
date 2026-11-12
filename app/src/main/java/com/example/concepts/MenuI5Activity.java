package com.example.concepts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

/** Opens Colours and Shapes from the I5 menu. {@link TransitionSound} handles return navigation and sounds. */
public class MenuI5Activity extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.activity_menu_i5);

        TransitionSound.bindMenu(this, savedInstanceState);
        findViewById(R.id.btn_activity_1).setOnClickListener(this);
        findViewById(R.id.btn_activity_2).setOnClickListener(this);
    }

    /**
     * Opens Colours and Shapes when its button is pressed. The first activity button has no launch action yet.
     *
     * @param view clicked activity button
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_activity_1) {
            gameLauncher.launch(new Intent(this, FindTheColourActivity.class));
        } else if (id == R.id.btn_activity_2) {
            gameLauncher.launch(new Intent(this, ColoursAndShapesActivity.class));
        }
    }

}
