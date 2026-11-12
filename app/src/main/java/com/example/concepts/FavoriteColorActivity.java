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

/** Runs the I3 favorite color activity. Dropping a color into the box plays its assigned sound and removes it from the row. */
public class FavoriteColorActivity extends AppCompatActivity implements View.OnDragListener {
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
        setDragListener();
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
     * Removes a color dropped inside the box from its container.
     *
     * @param viewColor dragged color image
     */
    private void onColorDropped(View viewColor) {
        Color color = Color.getColor(viewColor);
        destroyColor(viewColor);
        player.play(color.getSoundId());
    }

    /**
     * Removes the color from its current parent if it is still attached.
     *
     * @param color color view to remove
     */
    private void destroyColor(View color) {
        if (color.getParent() instanceof ViewGroup) {
            ((ViewGroup) color.getParent()).removeView(color);
        }
    }
}
