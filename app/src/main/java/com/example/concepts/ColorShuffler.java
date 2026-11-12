package com.example.concepts;

import java.util.Arrays;
import java.util.Collections;

/** Maintains two independently shuffled color sequences, one for display and one for game input. Each sequence has its own cursor. */
public final class ColorShuffler {
    private final Color[] viewOrder;
    private int viewIdx = 0;

    private final Color[] inputOrder;
    private int inputIdx = 0;

    /** Creates the shuffled orders. */
    public ColorShuffler() {
        viewOrder = shuffle();
        inputOrder = shuffle();
    }

    /** Returns the current display color. Check {@link #hasMoreColorsToView()} before reading it. */
    public Color getViewColor() {
        return viewOrder[viewIdx];
    }

    /** Advances the display cursor by one position. */
    public void pushView() {
        viewIdx++;
    }

    /** Returns whether the display cursor points to a remaining color. */
    public boolean hasMoreColorsToView() {
        return viewIdx < viewOrder.length;
    }

    /** Returns the current requested color. Check {@link #hasMoreColorsToOrder()} before reading it. */
    public Color getInputColor() {
        return inputOrder[inputIdx];
    }

    /** Advances the input cursor by one position. */
    public void pushInput() {
        inputIdx++;
    }

    /** Returns whether the input cursor points to a remaining color. */
    public boolean hasMoreColorsToOrder() {
        return inputIdx < inputOrder.length;
    }

    /** Returns a randomly ordered array containing each color exactly once. */
    private static Color[] shuffle() {
        Color[] colors = Color.values();
        Collections.shuffle(Arrays.asList(colors));
        return colors;
    }
}
