package com.example.concepts;

import java.util.Arrays;
import java.util.Collections;

/** Generates independent permutations for display and game input. */
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

    /** Returns the current color in the display order. */
    public Color getViewColor() {
        return viewOrder[viewIdx];
    }

    /** Advances the display cursor by one position. */
    public void pushView() {
        viewIdx++;
    }

    public boolean hasMoreColorsToView() {
        return viewIdx < viewOrder.length;
    }

    /** Returns the current color in the input order. */
    public Color getInputColor() {
        return inputOrder[inputIdx];
    }

    /** Advances the input cursor by one position. */
    public void pushInput() {
        inputIdx++;
    }

    public boolean hasMoreColorsToOrder() {
        return inputIdx < inputOrder.length;
    }

    /** Shuffles all colors, excluding their original order. */
    private static Color[] shuffle() {
        Color[] colors = Color.values();
        Collections.shuffle(Arrays.asList(colors));
        return colors;
    }
}
