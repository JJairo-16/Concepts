package com.example.concepts;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

/** Implements a variant of the I3 game that asks for colors in a shuffled order. The instruction icon plays the current target, and correct drops advance the sequence. */
public class GameI3ActivityCopy extends AppCompatActivity implements View.OnClickListener, View.OnDragListener {
    private final ColorShuffler shuffler = new ColorShuffler();
    private final SoundPlayer player = new SoundPlayer(this);

    /**
     * Loads the game layout, shuffles the colors, and configures drag and RETURN.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_color);

        TransitionSound.bindMenu(this, savedInstanceState);
        findViewById(R.id.iv_instruction_icon).setOnClickListener(this);

        shuffleColorViews();
        setDragListener();
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

    /**
     * Registers one drag starter for all colors and one drop listener for the box.
     */
    private void setDragListener() {
        int touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        View.OnTouchListener startDrag = new View.OnTouchListener() {
            private float downX;
            private float downY;

            /**
             * Starts a color drag when the gesture passes the touch threshold.
             *
             * @param color color view receiving the touch
             * @param event current touch event
             * @return {@code true} because the listener consumes the gesture
             */
            @Override
            public boolean onTouch(View color, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.hypot(event.getX() - downX, event.getY() - downY) > touchSlop) {
                            color.startDragAndDrop(null, new View.DragShadowBuilder(color), color, 0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        color.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        };
        int[] colorIds = {
                R.id.iv_color_red, R.id.iv_color_orange, R.id.iv_color_yellow,
                R.id.iv_color_green, R.id.iv_color_blue, R.id.iv_color_purple
        };
        for (int colorId : colorIds) {
            View color = findViewById(colorId);
            color.setTag(this);
            color.setOnTouchListener(startDrag);
        }
        findViewById(R.id.iv_box).setOnDragListener(this);
    }

    /**
     * Handles clicks dispatched to this activity.
     *
     * @param view view that received the click
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.iv_instruction_icon) return;

        if (!shuffler.hasMoreColorsToOrder()) {
            player.play(R.raw.meh);
            return;
        }

        Color color = shuffler.getInputColor();
        player.play(color.getSoundId());
    }

    /**
     * Accepts local color drags and handles drops on the box.
     *
     * @param view  drop target receiving the event
     * @param event current drag event
     * @return whether the event was handled
     */
    @Override
    public boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return event.getLocalState() instanceof View
                        && ((View) event.getLocalState()).getTag() == this;
            case DragEvent.ACTION_DROP:
                onColorDropped((View) event.getLocalState());
                return true;
            default:
                return true;
        }
    }

    /**
     * Checks a dropped color against the current target and advances the sequence only on a match.
     *
     * @param viewColor dragged color image
     */
    private void onColorDropped(View viewColor) {
        Color color = Color.getColor(viewColor);

        if (color != shuffler.getInputColor()) {
            player.play(R.raw.meh);
            return;
        }

        destroyColor(viewColor);
        shuffler.pushInput();

        if (shuffler.hasMoreColorsToOrder()) {
            player.play(R.raw.ok);
        } else {
            // todo: good job
        }
    }

    /**
     * Removes a correctly placed color from its current parent.
     *
     * @param color color view to remove
     */
    private void destroyColor(View color) {
        if (color.getParent() instanceof ViewGroup) {
            ((ViewGroup) color.getParent()).removeView(color);
        }
    }
}
