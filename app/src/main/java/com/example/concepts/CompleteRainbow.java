package com.example.concepts;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import java.util.HashSet;
import java.util.Set;

/** Runs the I4 rainbow game. A dragged chip previews the band under the pointer. A valid drop fills a matching, unoccupied band and plays feedback. */
public class CompleteRainbow extends AppCompatActivity {

    private final ColorShuffler shuffler = new ColorShuffler();
    private final SoundPlayer player = new SoundPlayer(this);

    private static final int[] CHIP_IDS = {
            R.id.iv_chip_red, R.id.iv_chip_orange, R.id.iv_chip_yellow,
            R.id.iv_chip_green, R.id.iv_chip_blue, R.id.iv_chip_purple
    };
    private static final int[] BAND_IDS = {
            R.id.iv_rainbow_red, R.id.iv_rainbow_orange, R.id.iv_rainbow_yellow,
            R.id.iv_rainbow_green, R.id.iv_rainbow_blue, R.id.iv_rainbow_purple
    };
    private static final int[] CHIP_COLORS = {
            R.color.color_red, R.color.color_orange, R.color.color_yellow,
            R.color.color_green, R.color.color_blue, R.color.color_purple
    };

    private final Set<Integer> occupiedBandIds = new HashSet<>();
    private int previewBandId = View.NO_ID;

    /**
     * Loads the board, shuffles the chips, and registers drag and return handlers.
     *
     * @param savedInstanceState activity state supplied by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_rainbow);
        TransitionSound.bindMenu(this, savedInstanceState);

        shuffleColorViews();

        for (int index = 0; index < CHIP_IDS.length; index++) {
            bindChipDrag(findViewById(CHIP_IDS[index]), index);
        }
        findViewById(R.id.rainbow_container).setOnDragListener(this::handleRainbowDrag);
    }

    /**
     * Applies the shuffled display order to the rainbow's color chips.
     */
    private void shuffleColorViews() {
        ViewGroup row = findViewById(R.id.chips_row);
        while (shuffler.hasMoreColorsToView()) {
            int colorIndex = shuffler.getViewColor().ordinal();
            View chip = row.findViewById(CHIP_IDS[colorIndex]);
            row.bringChildToFront(chip);
            shuffler.pushView();
        }
    }

    /**
     * Registers a touch listener that starts a drag once the chip moves past the touch threshold.
     *
     * @param chip draggable color view
     * @param chipIndex index used as the drag payload
     */
    private void bindChipDrag(View chip, int chipIndex) {
        int touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        chip.setOnTouchListener(new View.OnTouchListener() {
            private float downX;
            private float downY;
            private boolean dragging;

            /**
             * Tracks the starting point and starts the drag when movement exceeds the touch threshold.
             *
             * @param view chip receiving the gesture
             * @param event current touch event
             * @return {@code true} because the listener consumes the gesture
             */
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        dragging = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!dragging && Math.hypot(event.getX() - downX, event.getY() - downY)
                                > touchSlop) {
                            dragging = view.startDragAndDrop(
                                    null, new View.DragShadowBuilder(view), chipIndex, 0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!dragging) {
                            view.performClick();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Routes drag movement to the preview, drop events to validation, and exit or end events to preview cleanup.
     *
     * @param rainbow container receiving the drag
     * @param event current drag event
     * @return whether the event was handled
     */
    private boolean handleRainbowDrag(View rainbow, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return event.getLocalState() instanceof Integer;
            case DragEvent.ACTION_DRAG_LOCATION:
                int bandIndex = findBand(rainbow, event.getX(), event.getY());
                updatePreview(bandIndex, (int) event.getLocalState());
                return true;
            case DragEvent.ACTION_DROP:
                updatePreview(-1, 0);
                return dropChip(rainbow, event);
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                updatePreview(-1, 0);
                return true;
            default:
                return true;
        }
    }

    /**
     * Validates the target band and chip color, then commits the drop or plays error feedback for a rejected band.
     *
     * @param rainbow container used to interpret the drop coordinates
     * @param event drop event carrying the chip index
     * @return whether the chip was placed
     */
    private boolean dropChip(View rainbow, DragEvent event) {
        int bandIndex = findBand(rainbow, event.getX(), event.getY());
        if (bandIndex == -1) {
            return false;
        }

        int bandId = BAND_IDS[bandIndex];
        int chipIndex = (int) event.getLocalState();
        if (occupiedBandIds.contains(bandId) || bandId != BAND_IDS[chipIndex]) {
            player.play(R.raw.meh);
            return false;
        }

        onCorrectDrop(chipIndex, bandId);
        return true;
    }

    /**
     * Marks the band occupied, paints it, hides the chip, and plays the appropriate success sound.
     *
     * @param colorIndex index of the placed chip's color
     * @param bandId resource ID of the destination band
     */
    private void onCorrectDrop(int colorIndex, int bandId) {
        occupiedBandIds.add(bandId);
        paintBand(bandId, getColor(CHIP_COLORS[colorIndex]));
        findViewById(CHIP_IDS[colorIndex]).setVisibility(View.INVISIBLE);
        player.play(occupiedBandIds.size() == BAND_IDS.length ? R.raw.good_job : R.raw.ok);
    }

    /**
     * Maps container coordinates to the concentric ellipses in the rainbow vectors.
     *
     * @param rainbow container used to scale the drawable viewport
     * @param x horizontal coordinate within the container
     * @param y vertical coordinate within the container
     * @return zero-based band index, or {@code -1} outside the bands
     */
    private int findBand(View rainbow, float x, float y) {
        // The ImageViews fit-center a 252 × 102 viewport with center at (126, 102).
        float scale = Math.min(rainbow.getWidth() / 252f, rainbow.getHeight() / 102f);
        float centerX = rainbow.getWidth() / 2f;
        float centerY = (rainbow.getHeight() + 102f * scale) / 2f;
        if (scale <= 0 || y > centerY) {
            return -1;
        }
        double radius = Math.hypot((x - centerX) / (125f * scale), (y - centerY) / (100f * scale));
        double innerRadius = 0.54;
        if (radius < innerRadius || radius > 1) {
            return -1;
        }
        double bandWidth = (1 - innerRadius) / BAND_IDS.length;
        return Math.min(BAND_IDS.length - 1, (int) ((1 - radius) / bandWidth));
    }

    /**
     * Moves or clears the drag preview while leaving occupied bands intact.
     *
     * @param bandIndex candidate band index, or {@code -1} to clear the preview
     * @param chipIndex index of the dragged chip's color
     */
    private void updatePreview(int bandIndex, int chipIndex) {
        int bandId = bandIndex >= 0 ? BAND_IDS[bandIndex] : View.NO_ID;
        if (occupiedBandIds.contains(bandId)) {
            bandId = View.NO_ID;
        }
        if (bandId == previewBandId) {
            return;
        }
        if (previewBandId != View.NO_ID) {
            ImageView previousBand = findViewById(previewBandId);
            previousBand.clearColorFilter();
        }
        previewBandId = bandId;
        if (bandId != View.NO_ID) {
            int color = ColorUtils.blendARGB(getColor(CHIP_COLORS[chipIndex]), Color.WHITE, 0.55f);
            paintBand(bandId, color);
        }
    }

    /**
     * Applies the requested color filter to a band.
     *
     * @param bandId resource ID of the band view
     * @param color resolved ARGB color
     */
    private void paintBand(int bandId, int color) {
        ImageView band = findViewById(bandId);
        band.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }
}
