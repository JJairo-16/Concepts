package com.example.concepts;

import android.view.View;

/** Defines the six game colors and their image and sound resources. Use the exact image ID for the standard color row. */
public enum Color {
    // todo: change sound
    RED(R.id.iv_color_red, R.raw.transition),
    ORANGE(R.id.iv_color_orange, R.raw.ok),
    YELLOW(R.id.iv_color_yellow, R.raw.meh),
    GREEN(R.id.iv_color_green, R.raw.transition),
    BLUE(R.id.iv_color_blue, R.raw.transition),
    PURPLE(R.id.iv_color_purple, R.raw.transition);

    private final int viewId;
    private final int soundId;

    /**
     * Associates a color with its standard image and audio resources.
     *
     * @param viewId standard color image ID
     * @param soundId associated raw audio resource ID
     */
    Color(int viewId, int soundId) {
        this.viewId = viewId;
        this.soundId = soundId;
    }

    /**
     * Returns the resource ID of this color's image.
     */
    int getViewId() {
        return viewId;
    }

    /** Returns the raw audio resource currently assigned to this color. */
    int getSoundId() {
        return soundId;
    }

    /**
     * Returns the color associated with the given view ID.
     *
     * @param color view representing a game color
     * @return matching enum value
     * @throws IllegalArgumentException if the view ID is unknown
     */
    public static Color getColor(View color) {
        int id = color.getId();
        for (Color candidate : values()) {
            if (candidate.viewId == id) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unknown color view ID: " + id);
    }
}
