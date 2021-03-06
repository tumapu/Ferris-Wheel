package ru.github.igla.ferriswheel

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * Created by igor-lashkov on 11/01/2018.
 */
internal class WheelDrawable(private val context: Context) :
        Drawable(),
        WheelDrawableListener {

    private var stateController: StateController? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val drawableCallback = object : Callback {
        override fun invalidateDrawable(who: Drawable?) = invalidateSelf()

        override fun unscheduleDrawable(who: Drawable?, what: Runnable?) {
        }

        override fun scheduleDrawable(who: Drawable?, what: Runnable?, time: Long) {
        }
    }

    override fun isCenterCoordinate(x: Float, y: Float): Boolean = stateController?.isCenterCoordinate(x, y)
            ?: false

    fun build(viewConfig: WheelViewConfig) {
        val drawables = List(viewConfig.cabinsNumber) { number ->
            val cabinColor = viewConfig.cabinColors[number % viewConfig.cabinColors.size]
            CabinDrawable(context, number, cabinColor)
        }
        this.stateController = StateController(context, viewConfig, drawables, bounds)
    }

    fun getLocationCenter(point: PointF) {
        stateController?.getLocationCenter(point)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        stateController?.apply {
            val orientation = context.resources.configuration.orientation
            configure(bounds, orientation)
            invalidateSelf()
        }
    }

    override fun draw(canvas: Canvas) {
        stateController?.drawWheel(canvas)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity() = if (paint.alpha < 255) PixelFormat.TRANSLUCENT else PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter) {
        paint.colorFilter = colorFilter
    }

    private fun throwExceptionIfNotBuild() {
        if (stateController == null) {
            throw IllegalStateException("View is not build up. Call method build()")
        }
    }

    fun startAnimation() {
        throwExceptionIfNotBuild()
        stateController?.startAnimation(drawableCallback)
    }

    fun stopAnimation() {
        throwExceptionIfNotBuild()
        stateController?.stopAnimation()
    }

    fun resumeAnimation() {
        throwExceptionIfNotBuild()
        stateController?.resumeAnimation()
    }

    fun pauseAnimation() {
        throwExceptionIfNotBuild()
        stateController?.pauseAnimation()
    }
}


