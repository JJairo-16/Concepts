package com.example.concepts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

/** Reports the position of clicks on the scene using Android's standard click handling. */
public final class FindColourSceneView extends AppCompatImageView {
    /** Receives coordinates in the scene view's local coordinate system. */
    interface OnSceneClickListener {
        /** Receives the point where the pointer was released. */
        void onSceneClick(float x, float y);
    }

    private OnSceneClickListener sceneClickListener;
    private boolean hasClickPosition;
    private float clickX;
    private float clickY;

    /** Creates the scene image from its layout attributes. */
    public FindColourSceneView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Registers the game callback for clicks with a position. */
    void setOnSceneClickListener(OnSceneClickListener listener) {
        sceneClickListener = listener;
        setClickable(listener != null);
    }

    /** Dispatches clicks on release and delegates the rest of the gesture to Android. */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
                hasClickPosition = false;
                break;
            case MotionEvent.ACTION_UP:
                if (isPressed()) {
                    clickX = event.getX();
                    clickY = event.getY();
                    hasClickPosition = true;
                    setPressed(false);
                    performClick();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /** Dispatches the positioned click after Android recognizes the gesture. */
    @Override
    public boolean performClick() {
        boolean handled = super.performClick();
        if (hasClickPosition && sceneClickListener != null) {
            hasClickPosition = false;
            sceneClickListener.onSceneClick(clickX, clickY);
            return true;
        }
        return handled;
    }
}
