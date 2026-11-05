package com.example.concepts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Coordinates the sound when opening a menu and returning via RETURN.
 * The menu plays the sound when first created. The activity that opened it
 * plays the sound upon receiving {@link Activity#RESULT_OK}. The player is released
 * when playback finishes or its activity stops.
 */
final class TransitionSound implements DefaultLifecycleObserver {

    private MediaPlayer player;

    /**
     * Ties player cleanup to an activity's lifecycle.
     *
     * @param activity activity whose lifecycle is observed
     */
    private TransitionSound(AppCompatActivity activity) {
        activity.getLifecycle().addObserver(this);
    }

    /**
     * Registers the menu launcher and plays the sound when RETURN yields
     * {@link Activity#RESULT_OK}. Register the launcher before the activity starts.
     *
     * @param activity activity that opens the menus
     * @return launcher that accepts the intent for the selected menu
     */
    static ActivityResultLauncher<Intent> registerMenuLauncher(AppCompatActivity activity) {
        TransitionSound sound = new TransitionSound(activity);
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        sound.play(activity);
                    }
                });
    }

    /**
     * Sets up RETURN and plays the sound when the menu is first created.
     * Clicking RETURN sets {@link Activity#RESULT_OK} and finishes the menu,
     * revealing the previous activity. Call this after loading the menu layout.
     *
     * @param activity menu activity whose layout contains the RETURN button
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    static void bindMenu(AppCompatActivity activity, Bundle savedInstanceState) {
        activity.findViewById(R.id.btn_return).setOnClickListener(view -> {
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        });

        if (savedInstanceState == null) {
            new TransitionSound(activity).play(activity);
        }
    }

    /**
     * Releases any previous player and starts the transition sound.
     * Releases the player on completion if it is still the active player.
     *
     * @param context context used to access the audio resource
     */
    private void play(Context context) {
        release();

        player = MediaPlayer.create(context, R.raw.transition);
        if (player == null) {
            return;
        }

        player.setOnCompletionListener(completedPlayer -> {
            if (completedPlayer == player) {
                release();
            }
        });
        player.start();
    }

    /**
     * Releases the player when the observed activity stops.
     *
     * @param owner lifecycle owner that dispatched the stop event
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        release();
    }

    /**
     * Releases the active player and clears its reference.
     */
    private void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
