package com.example.concepts;

import android.content.res.XmlResourceParser;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.PathParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Find the color game screen, opened from the I5 menu.
 */
public class FindTheColourActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final int[] SCENES = {
            R.drawable.find_color_1, R.drawable.find_color_2, R.drawable.find_color_3
    };

    private final SoundPlayer player = new SoundPlayer(this);
    private final ColorShuffler shuffler = new ColorShuffler();
    private final List<SceneArea> areas = new ArrayList<>();
    private final Matrix inverseImageMatrix = new Matrix();
    private final float[] touchPoint = new float[2];
    private FindColourSceneView scene;
    private Region viewport;

    /**
     * Clears the parser cache, picks a scene, and wires the speaker and scene input.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_the_colour);
        TransitionSound.bindMenu(this, savedInstanceState);
        ColorElementParser.clearCache();

        initializeScene();
        bindSpeaker();
    }

    /** Loads one random scene and connects its click coordinates to the game. */
    private void initializeScene() {
        int sceneId = SCENES[new Random().nextInt(SCENES.length)];
        scene = findViewById(R.id.iv_find_scene);
        scene.setImageResource(sceneId);
        loadSceneAreas(sceneId);
        scene.setOnSceneClickListener(this::onSceneClick);
    }

    /** Wires the speaker icon and its surrounding card to the same action. */
    private void bindSpeaker() {
        findViewById(R.id.card_find_speaker).setOnClickListener(this);
        findViewById(R.id.iv_find_speaker).setOnClickListener(this);
    }

    /** Plays the current target sound, or error feedback after the game is complete. */
    @Override
    public void onClick(View v) {
        if (shuffler.hasMoreColorsToOrder()) {
            player.play(shuffler.getInputColor().getSoundId());
        } else {
            player.play(R.raw.meh);
        }
    }

    /**
     * Reads the visible fills of the chosen vector in drawing order.
     * The vector's own paths are also used as hit areas.
     */
    private void loadSceneAreas(int sceneId) {
        try (XmlResourceParser xml = getResources().getXml(sceneId)) {
            while (xml.next() != XmlPullParser.END_DOCUMENT) {
                if (xml.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                if ("vector".equals(xml.getName())) {
                    int width = Math.round(xml.getAttributeFloatValue(ANDROID_NAMESPACE, "viewportWidth", 0));
                    int height = Math.round(xml.getAttributeFloatValue(ANDROID_NAMESPACE, "viewportHeight", 0));
                    viewport = new Region(0, 0, width, height);
                } else if ("path".equals(xml.getName())) {
                    addSceneArea(xml);
                }
            }
        } catch (IOException | XmlPullParserException exception) {
            throw new IllegalStateException("Unable to read Find the Colour scene", exception);
        }
    }

    /** Adds a filled path, including neutral fills that can cover other paths. */
    private void addSceneArea(XmlResourceParser xml) {
        int fillId = xml.getAttributeResourceValue(ANDROID_NAMESPACE, "fillColor", 0);
        if (fillId == 0) {
            return;
        }
        String pathData = xml.getAttributeValue(ANDROID_NAMESPACE, "pathData");
        Region region = new Region();
        region.setPath(PathParser.createPathFromPathData(pathData), viewport);
        areas.add(new SceneArea(region, ColorElementParser.parse(getResources(), fillId)));
    }

    /** Maps the clicked point to the vector viewport and checks the selected color. */
    private void onSceneClick(float viewX, float viewY) {
        if (!shuffler.hasMoreColorsToOrder() || !scene.getImageMatrix().invert(inverseImageMatrix)) {
            return;
        }
        touchPoint[0] = viewX - scene.getPaddingLeft();
        touchPoint[1] = viewY - scene.getPaddingTop();
        inverseImageMatrix.mapPoints(touchPoint);

        Rect bounds = scene.getDrawable().getBounds();
        if (touchPoint[0] >= bounds.left && touchPoint[0] < bounds.right
                && touchPoint[1] >= bounds.top && touchPoint[1] < bounds.bottom) {
            Rect viewportBounds = viewport.getBounds();
            int x = (int) ((touchPoint[0] - bounds.left) * viewportBounds.width() / bounds.width());
            int y = (int) ((touchPoint[1] - bounds.top) * viewportBounds.height() / bounds.height());
            checkSelection(colorAt(x, y));
        }
    }

    /** Returns the color of the topmost filled path at the selected point. */
    private Color colorAt(int x, int y) {
        for (int index = areas.size() - 1; index >= 0; index--) {
            SceneArea area = areas.get(index);
            if (area.region.contains(x, y)) {
                return area.color;
            }
        }
        return null;
    }

    /** Plays feedback and advances the target only when the selected color matches. */
    private void checkSelection(Color selectedColor) {
        if (selectedColor != shuffler.getInputColor()) {
            player.play(R.raw.meh);
            return;
        }
        shuffler.pushInput();
        player.play(shuffler.hasMoreColorsToOrder() ? R.raw.ok : R.raw.good_job);
    }

    /** A filled scene path and its parsed color, if it has one. */
    private static final class SceneArea {
        final Region region;
        final Color color;

        /** Stores the hit region and the color associated with its fill. */
        SceneArea(Region region, Color color) {
            this.region = region;
            this.color = color;
        }
    }
}
