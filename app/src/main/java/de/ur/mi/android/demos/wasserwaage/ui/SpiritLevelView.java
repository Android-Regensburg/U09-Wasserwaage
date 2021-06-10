package de.ur.mi.android.demos.wasserwaage.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.ur.mi.android.demos.wasserwaage.R;

/**
 * This custom view represents a interactive spirit level (or bubble level), consisting of a
 * background plate, a "glass" vial or tube with marks and a moving bubble withing the tube to
 * display the levels orientation. See also: https://en.wikipedia.org/wiki/Spirit_level).
 * <p>
 * A level can be orientated vertical (default) or horizontal by changing the XML element's
 * "tubeOrientation" attribute. The bubble's position can be changed by calling the views
 * setBubblePosition-Method, passing a float value between 0.0 (top) and 1.0 (bottom). 0.5
 * marks the bubbles center position.
 */
public class SpiritLevelView extends ConstraintLayout {

    /**
     * The ImageView representing this level's bubble. This image's vertical bias within its
     * parent layout will be changed to allow visualization of different bubble positions.
     */
    private ImageView bubble;

    public SpiritLevelView(@NonNull Context context) {
        super(context);
        initLayout(context, null);
    }

    public SpiritLevelView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public SpiritLevelView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs);
    }

    public SpiritLevelView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        // Inflate this view's content from an XML file
        LayoutInflater.from(context).inflate(R.layout.spirit_level_view, this, true);
        // Enable layout transitions for smoother bubble movement
        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        setLayoutTransition(transition);
        // Check the orientation, set in the views XML element ...
        TubeOrientation orientation = getTubeOrientation(context, attrs);
        if (orientation == TubeOrientation.HORIZONTAL) {
            // ... and rotatethe view clockwise to create a horizontal level if necessary
            setRotation(90);
        }
        // Reference the bubble view, for use in "setBubblePosition"
        bubble = findViewById(R.id.spirit_level_bubble);
    }


    private TubeOrientation getTubeOrientation(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpiritLevelView, 0, 0);
        int tubeOrientation = attributes.getInteger(R.styleable.SpiritLevelView_tubeOrientation, 0);
        attributes.recycle();
        return TubeOrientation.fromInt(tubeOrientation);
    }

    /**
     * Sets the bubble's relative position from top of the tube (0.0) to its bottom (1.0). For levels with
     * horizontal orientation, the bubble is moved from left (0.0) to right (0.1). The center position is
     * always defined by position = 0.5f.
     *
     * @param position New position (must be between 0.0 and 1.0)
     */
    public void setBubblePosition(float position) {
        // Check if passed value is within range
        if (position < 0.0f || position > 1.0f) {
            return;
        }
        // Read bubbles current layout parameter
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bubble.getLayoutParams();
        // Change the vertical bias to move the bubble within the parent layout
        params.verticalBias = position;
        // Set the modified layout parameter to update the user interface
        bubble.setLayoutParams(params);
    }

    private enum TubeOrientation {
        VERTICAL,
        HORIZONTAL;

        static TubeOrientation fromInt(int value) {
            if (value < 0 || value >= TubeOrientation.values().length) {
                return VERTICAL;
            }
            return TubeOrientation.values()[value];
        }
    }
}
