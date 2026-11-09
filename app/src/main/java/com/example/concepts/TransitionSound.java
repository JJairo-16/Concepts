package com.example.concepts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Coordinates the sound when opening a menu and returning via RETURN.
 * The menu plays the sound when first created. The activity that opened it
 * plays the sound upon receiving {@link Activity#RESULT_OK}. The player is released
 * when playback finishes or its activity stops.
 */
final class TransitionSound {

    private TransitionSound() {
    }

    /**
     * Registers the menu launcher and plays the sound when RETURN yields
     * {@link Activity#RESULT_OK}. Register the launcher before the activity starts.
     *
     * @param activity activity that opens the menus
     * @return launcher that accepts the intent for the selected menu
     */
    static ActivityResultLauncher<Intent> registerMenuLauncher(AppCompatActivity activity) {
        SoundPlayer sound = new SoundPlayer(activity);
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) return;
                    sound.play(R.raw.transition);
                });
    }

    /**
     * Sets up RETURN and plays the sound when the menu is first created.
     * Clicking RETURN sets {@link Activity#RESULT_OK} and finishes the menu,
     * revealing the previous activity. Call this after loading the menu layout.
     *
     * @param activity           menu activity whose layout contains the RETURN button
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    static void bindMenu(AppCompatActivity activity, Bundle savedInstanceState) {
        activity.findViewById(R.id.btn_return).setOnClickListener(view -> {
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        });

        if (savedInstanceState == null) {
            new SoundPlayer(activity).play(R.raw.transition);
        }
    }

}
