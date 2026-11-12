package com.example.concepts;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Colours and shapes game screen, opened from the I5 menu.
 */
public class ColoursAndShapesActivity extends AppCompatActivity {

    private static final int[] WORD_IDS = {
            R.id.tv_word_red, R.id.tv_word_orange, R.id.tv_word_yellow,
            R.id.tv_word_green, R.id.tv_word_blue, R.id.tv_word_purple
    };
    private static final int[] SHAPE_IDS = {
            R.id.iv_shape_heart, R.id.iv_shape_hexagon, R.id.iv_shape_triangle,
            R.id.iv_shape_square, R.id.iv_shape_circle, R.id.iv_shape_star
    };

    private final View[] words = new View[WORD_IDS.length];
    private final View[] shapes = new View[SHAPE_IDS.length];
    private final SoundPlayer player = new SoundPlayer(this);
    private final int[] overlayLocation = new int[2];
    private ConnectionView fixedConnections;
    private ConnectionView previewConnection;
    private int selectedWord = -1;
    private int matchedCount;
    private float pointerX;
    private float pointerY;

    /**
     * Loads the colours and shapes layout.
     *
     * @param savedInstanceState state saved by Android, or {@code null} on first creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colours_and_shapes);
        TransitionSound.bindMenu(this, savedInstanceState);

        ViewGroup board = findViewById(R.id.colours_shapes_root);
        fixedConnections = new ConnectionView(true);
        board.addView(fixedConnections, board.indexOfChild(findViewById(WORD_IDS[0])),
                new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        previewConnection = new ConnectionView(false);
        board.addView(previewConnection,
                new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        for (int index = 0; index < WORD_IDS.length; index++) {
            int pair = index;
            words[index] = findViewById(WORD_IDS[index]);
            shapes[index] = findViewById(SHAPE_IDS[index]);
            words[index].setOnClickListener(view -> selectWord(pair));
            shapes[index].setOnClickListener(view -> selectShape(pair));
        }
        shuffleWords();
    }

    private void shuffleWords() {
        int[] rows = new int[words.length];
        for (int index = 0; index < words.length; index++) {
            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) words[index].getLayoutParams();
            rows[index] = params.topToTop;
        }

        ColorShuffler shuffler = new ColorShuffler();
        for (int row : rows) {
            View word = words[shuffler.getViewColor().ordinal()];
            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) word.getLayoutParams();
            params.topToTop = row;
            params.bottomToBottom = row;
            word.setLayoutParams(params);
            shuffler.pushView();
        }
    }

    private void selectWord(int pair) {
        if (selectedWord == pair) {
            selectedWord = -1;
        } else {
            selectedWord = pair;
            pointerX = words[pair].getRight();
            pointerY = centerY(words[pair]);
        }
        previewConnection.invalidate();
    }

    private void selectShape(int pair) {
        if (selectedWord == -1) {
            return;
        }
        boolean correct = selectedWord == pair;
        selectedWord = -1;
        if (correct) {
            words[pair].setEnabled(false);
            shapes[pair].setEnabled(false);
            matchedCount++;
            fixedConnections.invalidate();
            player.play(matchedCount == WORD_IDS.length ? R.raw.good_job : R.raw.ok);
        } else {
            player.play(R.raw.meh);
        }
        previewConnection.invalidate();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (selectedWord != -1 && event.getActionMasked() == MotionEvent.ACTION_HOVER_MOVE) {
            previewConnection.getLocationInWindow(overlayLocation);
            pointerX = event.getX() - overlayLocation[0];
            pointerY = event.getY() - overlayLocation[1];
            previewConnection.invalidate();
        }
        return super.dispatchGenericMotionEvent(event);
    }

    private static float centerY(View view) {
        return view.getTop() + view.getHeight() / 2f;
    }

    private final class ConnectionView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final boolean fixed;

        ConnectionView(boolean fixed) {
            super(ColoursAndShapesActivity.this);
            this.fixed = fixed;
            setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            paint.setColor(ColoursAndShapesActivity.this.getColor(R.color.menu_text_dark));
            paint.setStrokeWidth(3 * getResources().getDisplayMetrics().density);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (fixed) {
                for (int index = 0; index < words.length; index++) {
                    View word = words[index];
                    if (!word.isEnabled()) {
                        View shape = shapes[index];
                        canvas.drawLine(word.getRight(), centerY(word),
                                shape.getLeft() + shape.getWidth() / 2f, centerY(shape), paint);
                    }
                }
            } else if (selectedWord != -1) {
                View word = words[selectedWord];
                canvas.drawCircle(word.getRight(), centerY(word), paint.getStrokeWidth(), paint);
                canvas.drawLine(word.getRight(), centerY(word), pointerX, pointerY, paint);
            }
        }
    }
}
