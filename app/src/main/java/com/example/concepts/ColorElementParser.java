package com.example.concepts;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Resolves colors from underscore-delimited image resource names and caches results by resource ID. Use from the UI thread. */
public final class ColorElementParser {
    /** Prevents construction of this utility class. */
    private ColorElementParser() {
    }

    private static final Map<Integer, Color> CACHE = new HashMap<>();
    private static final Set<Integer> NULL_CACHE = new HashSet<>();

    /**
     * Returns the first color named in the image's resource ID.
     * For example, the resource entry name {@code iv_chip_red} maps to {@link Color#RED}.
     *
     * @param view image whose ID identifies the color
     * @return matching color, or {@code null} if the ID has no recognizable color
     */
    public static Color parse(ImageView view) {
        int id = view.getId();
        if (id == View.NO_ID) {
            return null;
        }

        Color cachedColor = CACHE.get(id);
        if (cachedColor != null || NULL_CACHE.contains(id)) {
            return cachedColor;
        }

        return resolveAndCache(view.getResources(), id);
    }

    /**
     * Resolves a resource name and caches either its color or a negative result. Unresolvable IDs are skipped.
     *
     * @param resources resources used to look up the ID
     * @param id image resource ID
     * @return parsed color, or {@code null} if the name is unavailable or contains no color
     */
    private static Color resolveAndCache(Resources resources, int id) {
        String resourceName;
        try {
            resourceName = resources.getResourceEntryName(id);
        } catch (Resources.NotFoundException exception) {
            return null;
        }

        Color color = findColorInName(resourceName);
        if (color == null) {
            NULL_CACHE.add(id);
        } else {
            CACHE.put(id, color);
        }
        return color;
    }

    /**
     * Matches complete underscore-delimited segments in left-to-right order, ignoring case.
     *
     * @param resourceName resource entry name
     * @return first matching color, or {@code null} if no segment matches
     */
    private static Color findColorInName(String resourceName) {
        Color[] colors = Color.values();
        for (String segment : resourceName.split("_")) {
            for (Color color : colors) {
                if (color.name().equalsIgnoreCase(segment)) {
                    return color;
                }
            }
        }
        return null;
    }

    /** Clears the cached colors and the IDs already checked without a match. */
    public static void clearCache() {
        CACHE.clear();
        NULL_CACHE.clear();
    }
}
