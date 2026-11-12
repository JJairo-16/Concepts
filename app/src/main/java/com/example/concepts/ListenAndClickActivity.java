package com.example.concepts;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/** Runs the I4 listening game. The display order is shuffled independently of the requested colors. Again plays the current prompt, and a correct click advances to the next color. */
public class ListenAndClickActivity extends AppCompatActivity implements  View.OnClickListener{

    private final ColorShuffler shuffler = new ColorShuffler();
    private final SoundPlayer player = new SoundPlayer(this);


    /**
     * Loads the listen and click layout.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_and_click);

        shuffleColorViews();

        TransitionSound.bindMenu(this, savedInstanceState);
        setOnClickListeners();
    }

    /**
     * Applies the shuffled display order to the existing color images.
     */
    private void shuffleColorViews() {
        ViewGroup row = findViewById(R.id.colors_row);
        for (int index = 0; index < row.getChildCount(); index++) {
            View color = row.findViewById(shuffler.getViewColor().getViewId());
            row.bringChildToFront(color);
            shuffler.pushView();
        }
    }

    /** Registers the Again button and all color choices with this activity. */
    private void setOnClickListeners() {
        setOnClickListener(R.id.btn_again);

        setOnClickListener(R.id.iv_color_red);
        setOnClickListener(R.id.iv_color_orange);
        setOnClickListener(R.id.iv_color_yellow);
        setOnClickListener(R.id.iv_color_green);
        setOnClickListener(R.id.iv_color_blue);
        setOnClickListener(R.id.iv_color_purple);
    }

    /**
     * Routes clicks on the specified layout view to this activity.
     *
     * @param id resource ID of the clickable view
     */
    private void setOnClickListener(int id) {
        findViewById(id).setOnClickListener(this);
    }

    /**
     * Replays the current prompt or checks the selected color and plays feedback.
     *
     * @param v clicked button or color image
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_again) {
            again();
            return;
        } else if (!shuffler.hasMoreColorsToOrder()) return;

        Color color = Color.getColor(v);
        if (color != shuffler.getInputColor()) {
            player.play(R.raw.meh);
            return;
        }

        shuffler.pushInput();
        if (shuffler.hasMoreColorsToOrder()) player.play(R.raw.ok);
        else player.play(R.raw.good_job);
    }

    /** Animates the speaker and plays the current color prompt while a round remains. Otherwise plays the error sound. */
    private void again() {
        if (!shuffler.hasMoreColorsToOrder()) {
            player.play(R.raw.meh);
            return;
        }

        animateListenIcon();
        Color color = shuffler.getInputColor();
        player.play(color.getSoundId());
    }

    /** Resets the animated drawable and starts its wave sequence from the beginning. */
    private void animateListenIcon() {
        ImageView listenIcon = findViewById(R.id.iv_listen_icon);
        AnimatedVectorDrawable animation = (AnimatedVectorDrawable) listenIcon.getDrawable();
        animation.reset();
        animation.start();
    }
}
