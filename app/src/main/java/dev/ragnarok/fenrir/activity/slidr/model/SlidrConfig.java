package dev.ragnarok.fenrir.activity.slidr.model;


import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.customview.widget.ViewDragHelper;


/**
 * This class contains the configuration information for all the options available in
 * this library
 */
public class SlidrConfig {

    private boolean fromUnColoredToColoredStatusBar;
    private float touchSize = -1f;
    private float sensitivity = 0.5f;
    private int scrimColor = -1;
    private float scrimStartAlpha = 0.8f;
    private float scrimEndAlpha;
    private float velocityThreshold = 5f;
    private float distanceThreshold = 0.25f;
    private boolean edgeOnly;
    private float edgeSize = 0.18f;
    private boolean ignoreChildScroll;
    private boolean alphaForView = true;

    private SlidrPosition position = SlidrPosition.LEFT;
    private SlidrListener listener;


    private SlidrConfig() {
        // Unused.
    }

    public boolean isFromUnColoredToColoredStatusBar() {
        return fromUnColoredToColoredStatusBar;
    }

    /***********************************************************************************************
     *
     * Setters
     *
     */


    public void setFromUnColoredToColoredStatusBar(boolean en) {
        fromUnColoredToColoredStatusBar = en;
    }

    /**
     * Get the color of the background scrim
     *
     * @return the scrim color integer
     */
    @ColorInt
    public int getScrimColor() {
        return scrimColor;
    }

    public void setScrimColor(@ColorInt int scrimColor) {
        this.scrimColor = scrimColor;
    }

    /**
     * Get teh start alpha value for when the activity is not swiped at all
     *
     * @return the start alpha value (0.0 to 1.0)
     */
    public float getScrimStartAlpha() {
        return scrimStartAlpha;
    }

    public void setScrimStartAlpha(float scrimStartAlpha) {
        this.scrimStartAlpha = scrimStartAlpha;
    }

    /**
     * Get the end alpha value for when the user almost swipes the activity off the screen
     *
     * @return the end alpha value (0.0 to 1.0)
     */
    public float getScrimEndAlpha() {
        return scrimEndAlpha;
    }

    public void setScrimEndAlpha(float scrimEndAlpha) {
        this.scrimEndAlpha = scrimEndAlpha;
    }

    /**
     * Get the position of the slidable mechanism for this configuration. This is the position on
     * the screen that the user can swipe the activity away from
     *
     * @return the slider position
     */
    public SlidrPosition getPosition() {
        return position;
    }

    /**
     * Get the touch 'width' to be used in the gesture detection. This value should incorporate with
     * the device's touch slop
     *
     * @return the touch area size
     */
    public float getTouchSize() {
        return touchSize;
    }

    public void setTouchSize(float touchSize) {
        this.touchSize = touchSize;
    }

    /**
     * Get the velocity threshold at which the slide action is completed regardless of offset
     * distance of the drag
     *
     * @return the velocity threshold
     */
    public float getVelocityThreshold() {
        return velocityThreshold;
    }

    public void setVelocityThreshold(float velocityThreshold) {
        this.velocityThreshold = velocityThreshold;
    }

    /**
     * Get at what % of the screen is the minimum viable distance the activity has to be dragged
     * in-order to be slinged off the screen
     *
     * @return the distant threshold as a percentage of the screen size (width or height)
     */
    public float getDistanceThreshold() {
        return distanceThreshold;
    }

    public void setDistanceThreshold(float distanceThreshold) {
        this.distanceThreshold = distanceThreshold;
    }

    /**
     * Get the touch sensitivity set in the {@link ViewDragHelper} when
     * creating it.
     *
     * @return the touch sensitivity
     */
    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public boolean isAlphaForView() {
        return alphaForView;
    }

    public void setAlphaForView(boolean alphaForView) {
        this.alphaForView = alphaForView;
    }

    /**
     * Get the slidr listener set by the user to respond to certain events in the sliding
     * mechanism.
     *
     * @return the slidr listener
     */
    public SlidrListener getListener() {
        return listener;
    }

    /**
     * Has the user configured slidr to only catch at the edge of the screen ?
     *
     * @return true if is edge capture only
     */
    public boolean isEdgeOnly() {
        return edgeOnly;
    }

    /**
     * Get the size of the edge field that is catchable
     *
     * @return the size of the edge that is grabable
     * @see #isEdgeOnly()
     */
    public float getEdgeSize(float size) {
        return edgeSize * size;
    }

    /**
     * Has the user configured slidr to ignore all scrollable children inside
     *
     * @return true if is ignore all scrollable children under touch
     */
    public boolean isIgnoreChildScroll() {
        return ignoreChildScroll;
    }

    /**
     * The Builder for this configuration class. This is the only way to create a
     * configuration
     */
    public static class Builder {

        private final SlidrConfig config;

        public Builder() {
            config = new SlidrConfig();
        }

        public Builder fromUnColoredToColoredStatusBar(boolean en) {
            config.fromUnColoredToColoredStatusBar = en;
            return this;
        }

        public Builder position(SlidrPosition position) {
            config.position = position;
            return this;
        }

        public Builder touchSize(float size) {
            config.touchSize = size;
            return this;
        }

        public Builder sensitivity(float sensitivity) {
            config.sensitivity = sensitivity;
            return this;
        }

        public Builder scrimColor(@ColorInt int color) {
            config.scrimColor = color;
            return this;
        }

        public Builder scrimStartAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
            config.scrimStartAlpha = alpha;
            return this;
        }

        public Builder scrimEndAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
            config.scrimEndAlpha = alpha;
            return this;
        }

        public Builder velocityThreshold(float threshold) {
            config.velocityThreshold = threshold;
            return this;
        }

        public Builder distanceThreshold(@FloatRange(from = .1f, to = .9f) float threshold) {
            config.distanceThreshold = threshold;
            return this;
        }

        public Builder edge(boolean flag) {
            config.edgeOnly = flag;
            return this;
        }

        public Builder edgeSize(@FloatRange(from = 0f, to = 1f) float edgeSize) {
            config.edgeSize = edgeSize;
            return this;
        }

        public Builder ignoreChildScroll(boolean ignoreChildScroll) {
            config.ignoreChildScroll = ignoreChildScroll;
            return this;
        }

        public Builder setAlphaForView(boolean alphaForView) {
            config.alphaForView = alphaForView;
            return this;
        }

        public Builder listener(SlidrListener listener) {
            config.listener = listener;
            return this;
        }

        public SlidrConfig build() {
            return config;
        }

    }

}
