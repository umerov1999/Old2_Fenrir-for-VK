package dev.ragnarok.fenrir.view.swipehelper

import android.content.Context
import android.util.AttributeSet
import android.util.Property
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.customview.widget.ViewDragHelper
import dev.ragnarok.fenrir.settings.Settings
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class HorizontalSwipeBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <V : View> from(v: V): HorizontalSwipeBehavior<V> {
            val lp = v.layoutParams
            require(lp is CoordinatorLayout.LayoutParams)
            val behavior = lp.behavior
            requireNotNull(behavior)
            require(behavior is HorizontalSwipeBehavior)
            return behavior as HorizontalSwipeBehavior<V>
        }
    }

    var sideEffect: SideEffect = AlphaElevationSideEffect()
    var clamp: HorizontalClamp = FractionClamp(1f, 1f)
    var settle: PostAction = OriginSettleAction()
    var listener: SwipeListener? = null
    var canSwipe = true

    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var dragHelper: ViewDragHelper? = null
    private var interceptingEvents = false

    private val callback = object : ViewDragHelper.Callback() {

        private val INVALID_POINTER_ID = -1
        private var currentPointer = INVALID_POINTER_ID
        private var originLeft: Int = 0

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (!canSwipe)
                return false
            return currentPointer == INVALID_POINTER_ID || pointerId == currentPointer
        }

        override fun onViewCaptured(child: View, activePointerId: Int) {
            listener?.onCaptured()
            originLeft = child.left
            currentPointer = activePointerId
            //
            sideEffect.onViewCaptured(child)
            settle.onViewCaptured(child)
            clamp.onViewCaptured(child.left)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return child.top
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return clamp.constraint(child.width, left, dx)
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return child.width
        }

        override fun onViewReleased(child: View, xvel: Float, yvel: Float) {
            val diff = child.left - originLeft
            val settled = dragHelper?.let {
                if (abs(diff) > Settings.get().ui().isPhoto_swipe_triggered_pos - 40) {
                    settle.releasedAbove(it, diff, child)
                } else {
                    settle.releasedBelow(it, diff, child)
                }
            } ?: false
            if (settled) {
                listener?.onPreSettled(diff)
                child.postOnAnimation(RecursiveSettle(child, diff))
            } else
                listener?.onReleased()
            currentPointer = INVALID_POINTER_ID
        }

        override fun onViewPositionChanged(child: View, left: Int, top: Int, dx: Int, dy: Int) {
            val factor = if (left < originLeft) {
                val diff = originLeft - left
                -clamp.leftCast(diff, left, child.width, dx)
            } else {
                val diff = left - originLeft
                clamp.rightCast(diff, left, child.width, dx)
            }
            sideEffect.apply(child, factor)
        }
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        ev: MotionEvent
    ): Boolean {
        var isIntercept = interceptingEvents
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isIntercept = parent.isPointInChildBounds(child, ev.x.toInt(), ev.y.toInt())
                interceptingEvents = isIntercept
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                interceptingEvents = false
            }
        }
        return if (isIntercept) {
            helper(parent).shouldInterceptTouchEvent(ev)
        } else false
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        val helper = helper(parent)
        return if (helper.capturedView == child || helper.isViewUnder(
                child,
                ev.x.toInt(),
                ev.y.toInt()
            )
        ) {
            helper.processTouchEvent(ev)
            true
        } else {
            false
        }
    }

    private fun helper(parent: ViewGroup): ViewDragHelper {
        var h = dragHelper
        if (h == null) {
            h = ViewDragHelper.create(parent, callback)
            dragHelper = h
            return h
        }
        return h
    }

    private inner class RecursiveSettle(private val child: View, private val diff: Int) : Runnable {

        override fun run() {
            if (dragHelper?.continueSettling(true) == true) {
                child.postOnAnimation(this)
            } else {
                child.removeCallbacks(this)
                val isSuccess = abs(diff) > Settings.get().ui().isPhoto_swipe_triggered_pos - 40
                val isLeft = diff < -Settings.get().ui().isPhoto_swipe_triggered_pos + 40
                listener?.onPostSettled(isSuccess, isLeft)
            }
        }
    }

    interface SwipeListener {

        /**
         * Сalled before settle
         * @param diff passed distance
         */
        fun onPreSettled(diff: Int)

        /**
         * Call after settle
         * @param success is complete
         */
        fun onPostSettled(success: Boolean, left: Boolean)

        fun onCaptured()

        fun onReleased()
    }

    /**
     * Changing alpha and elevation of view
     */
    class AlphaElevationSideEffect : SideEffect {

        private var elevation: Float = 0f

        override fun onViewCaptured(child: View) {
            elevation = child.elevation
        }

        override fun apply(child: View, factor: Float) {
            child.elevation = elevation * (1f - abs(factor)) // special for elevation-aware view
            child.alpha = 1f - abs(factor)
        }
    }

    /**
     *  Restricts movement down a part of the view width
     *  @param maxFraction maximum position limit factor
     *  @param minFraction upward progress factor
     */
    class BelowFractionalClamp(
        private val maxFraction: Float = 1f,
        private val minFraction: Float = 1f
    ) : HorizontalClamp {

        init {
            require(maxFraction > 0)
            require(minFraction > 0)
        }

        private var originLeft: Int = -1

        override fun onViewCaptured(left: Int) {
            originLeft = left
        }

        override fun constraint(width: Int, left: Int, dx: Int): Int {
            return min(left.toFloat(), originLeft + width * maxFraction).toInt()
        }

        override fun rightCast(distance: Int, left: Int, width: Int, dx: Int): Float {
            return distance / (width * maxFraction)
        }

        override fun leftCast(distance: Int, left: Int, width: Int, dx: Int): Float {
            return distance / (width * minFraction)
        }
    }

    /**
     * Restricts movement up and down by part of the view width
     * @param maxFraction maximum position limit factor
     * @param minFraction minimum position limit factor
     */
    class FractionClamp(
        private val maxFraction: Float = 1f,
        private val minFraction: Float = 1f
    ) : HorizontalClamp {

        init {
            require(maxFraction > 0)
            require(minFraction > 0)
        }

        private var originLeft: Int = -1

        override fun onViewCaptured(left: Int) {
            originLeft = left
        }

        override fun constraint(width: Int, left: Int, dx: Int): Int {
            val min = min(left, originLeft + (width * minFraction).toInt())
            return max(min, originLeft - (width * maxFraction).toInt())
        }

        override fun rightCast(distance: Int, left: Int, width: Int, dx: Int): Float {
            return distance / (width * maxFraction)
        }

        override fun leftCast(distance: Int, left: Int, width: Int, dx: Int): Float {
            return distance / (width * minFraction)
        }
    }

    /**
     * Applies the [delegate] only if view moves upwards
     */
    @Suppress("unused")
    class NegativeFactorFilterSideEffect(private val delegate: SideEffect) :
        SideEffect by delegate {

        override fun apply(child: View, factor: Float) {
            if (factor < 0) {
                delegate.apply(child, abs(factor))
            }
        }
    }

    /**
     * When the gesture is complete, it moves the view to the starting position
     */
    class OriginSettleAction : PostAction {

        private var originLeft: Int = -1

        override fun onViewCaptured(child: View) {
            originLeft = child.left
        }

        override fun releasedBelow(helper: ViewDragHelper, diff: Int, child: View): Boolean {
            return helper.settleCapturedViewAt(originLeft, child.top)
        }

        override fun releasedAbove(helper: ViewDragHelper, diff: Int, child: View): Boolean {
            return helper.settleCapturedViewAt(originLeft, child.top)
        }
    }

    /**
     * Responsible for changing the view position after the gesture is completed
     */
    interface PostAction {

        fun onViewCaptured(child: View)

        /**
         * View was released below initial position
         * @param helper motion animation "visitor"
         * @param diff released distance
         * @param child target view
         * @return whether or not the motion settle was triggered
         */
        fun releasedBelow(helper: ViewDragHelper, diff: Int, child: View): Boolean

        /**
         * View was released above initial position
         * @param helper motion animation "visitor"
         * @param diff released distance
         * @param child target view
         * @return whether or not the motion settle was triggered
         */
        fun releasedAbove(helper: ViewDragHelper, diff: Int, child: View): Boolean
    }

    /**
     * Common way for changing several properties of view at the same time
     */
    class PropertySideEffect(vararg props: Property<View, Float>) : SideEffect {

        private val properties = props
        private val capturedValues: Array<Float> = Array(props.size) { 0f }

        override fun onViewCaptured(child: View) {
            for ((index, property) in properties.withIndex()) {
                val value = property.get(child)
                capturedValues[index] = value
            }
        }

        override fun apply(child: View, factor: Float) {
            for ((index, property) in properties.withIndex()) {
                val value = capturedValues[index] * (1 - abs(factor))
                property.set(child, value)
            }
        }
    }

    /**
     * Reduces each move by several times and delegates the definition of the restriction
     * @param upSensitivity Sensitivity when moving up
     * @param delegate delegate
     * @param downSensitivity Sensitivity when moving down
     */

    class SensitivityClamp(
        private val upSensitivity: Float = 1f,
        private val delegate: HorizontalClamp,
        private val downSensitivity: Float = 1f
    ) : HorizontalClamp by delegate {

        override fun constraint(width: Int, left: Int, dx: Int): Int {
            val coefficient = if (dx > 0) downSensitivity else upSensitivity
            val newDx = (dx * coefficient).toInt()
            val newLeft = left - dx + newDx
            return delegate.constraint(width, newLeft, newDx)
        }
    }

    /**
     * When view moved downwards, it returns to the initial position.
     * Moves above - takes away from the screen.
     */
    @Suppress("unused")
    class SettleOnLeftAction : PostAction {

        private var originLeft: Int = -1

        override fun onViewCaptured(child: View) {
            originLeft = child.left
        }

        override fun releasedBelow(helper: ViewDragHelper, diff: Int, child: View): Boolean {
            return helper.settleCapturedViewAt(originLeft, child.top)
        }

        override fun releasedAbove(helper: ViewDragHelper, diff: Int, child: View): Boolean {
            return helper.settleCapturedViewAt(
                if (diff < 0) -child.width else child.width,
                child.top
            )
        }
    }

    /**
     * Change of view properties depending on the progress of movement.
     * @see HorizontalClamp
     */
    interface SideEffect {

        fun onViewCaptured(child: View)

        /**
         * Apply new property value for [child] depends on [factor]
         * @param child target movement
         * @param factor movement progress, from 0 to 1
         * @see [HorizontalClamp.rightCast]
         * @see [HorizontalClamp.leftCast]
         */
        fun apply(child: View, factor: Float)
    }

    /**
     * Sets limits on moving the view horizontally
     */
    interface HorizontalClamp {

        fun onViewCaptured(left: Int)

        /**
         * Limits maximum and/or minimum position for view
         * @param width width of view
         * @param left position of view
         * @param dx last movement of view
         * @return new position for view, see [android.view.View.getLeft]
         */
        fun constraint(width: Int, left: Int, dx: Int): Int

        /**
         * Calculate movement progress down
         * @param distance total distance
         * @param left position of view
         * @param width width of view
         * @param dx last movement of view
         * @return movement progress down from 0 to 1
         * @see [SideEffect.apply]
         */
        fun leftCast(distance: Int, left: Int, width: Int, dx: Int): Float

        /**
         * Calculate movement progress up
         * @param distance total distance
         * @param left position of view
         * @param width width of view
         * @param dx last movement of view
         * @return movement progress up from 0 to 1
         * @see [SideEffect.apply]
         */
        fun rightCast(distance: Int, left: Int, width: Int, dx: Int): Float
    }

    /**
     * Does not change any properties of view
     */
    @Suppress("unused")
    class WithoutSideEffect : SideEffect {

        override fun onViewCaptured(child: View) {
            // ignore
        }

        override fun apply(child: View, factor: Float) {
            // ignore
        }
    }
}
