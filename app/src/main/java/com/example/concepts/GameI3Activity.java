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

/**
 * Game screen opened from the I3 menu.
 */
public class GameI3Activity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener {

    /**
     * Launcher registered during activity initialization to open the menus.
     */
    private final ActivityResultLauncher<Intent> menuLauncher =
            TransitionSound.registerMenuLauncher(this);

    /**
     * Loads the game layout and configures the transition sound and RETURN button.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activvity_favorite_color);

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
    }

    /**
     * Accepts local color drags and handles drops on the box.
     *
     * @param view drop target receiving the event
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
     * @param color dragged color image
     */
    private void onColorDropped(View color) {
        if (color.getParent() instanceof ViewGroup) {
            ((ViewGroup) color.getParent()).removeView(color);
        }
    }
}
