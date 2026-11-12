package com.example.concepts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

/** Opens the I3, I4, or I5 menu from the home screen, or closes the app screen when Exit is pressed. Menu results are routed through {@link TransitionSound}. */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Launcher registered during activity initialization to open the menus.
     */
    private final ActivityResultLauncher<Intent> menuLauncher =
            TransitionSound.registerMenuLauncher(this);

    /**
     * Loads the main layout and registers its button listeners.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListener(R.id.btn_exit);
        setListener(R.id.btn_i3);
        setListener(R.id.btn_i4);
        setListener(R.id.btn_i5);
    }

    /**
     * Closes the app when EXIT is pressed or opens the selected level menu.
     *
     * @param view view that received the click
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_exit) {
            finish();
            System.exit(0);
            return;
        }

        Intent intent;
        if (id == R.id.btn_i3) {
            intent = new Intent(this, MenuI3Activity.class);
        } else if (id == R.id.btn_i4) {
            intent = new Intent(this, MenuI4Activity.class);
        } else if (id == R.id.btn_i5) {
            intent = new Intent(this, MenuI5Activity.class);
        } else {
            return;
        }

        menuLauncher.launch(intent);
    }

    /**
     * Attaches this activity's click listener to a button in the current layout.
     *
     * @param id view resource ID
     */
    private void setListener(int id) {
        findViewById(id).setOnClickListener(this);
    }
}
