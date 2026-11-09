package com.example.concepts;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.activity.ComponentActivity;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/** Plays raw audio resources on demand for an activity. */
public final class SoundPlayer implements DefaultLifecycleObserver {

    private final Context context;
    private MediaPlayer player;

    /**
     * Creates a player that releases its audio when the activity stops.
     *
     * @param activity activity that owns this player
     */
    @MainThread
    public SoundPlayer(ComponentActivity activity) {
        // The activity's base context may still be unattached during field initialization.
        context = activity;
        activity.getLifecycle().addObserver(this);
    }

    /**
     * Starts the requested sound, replacing any sound this instance is playing.
     *
     * @param soundId audio resource from {@code res/raw}
     */
    @MainThread
    public void play(@RawRes int soundId) {
        release();
        player = MediaPlayer.create(context, soundId);
        if (player == null) {
            return;
        }

        player.setOnCompletionListener(this::releaseIfCurrent);
        player.setOnErrorListener((failedPlayer, what, extra) -> {
            releaseIfCurrent(failedPlayer);
            return true;
        });
        player.start();
    }

    /**
     * Stops playback and releases the player when the activity stops.
     *
     * @param owner activity lifecycle that dispatched the event
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        release();
    }

    private void releaseIfCurrent(MediaPlayer finishedPlayer) {
        if (finishedPlayer == player) {
            release();
        }
    }

    private void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
