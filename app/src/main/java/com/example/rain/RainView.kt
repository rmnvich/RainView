package com.example.rain

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import kotlin.random.Random

class RainView : View {

    private data class Drop(
        val width: Float,
        val height: Float,
        val speed: Long
    ) {
        var x: Float = 0F
        var y: Float = -100F
    }

    private val rain: List<Drop> = ArrayList()

    private val rainPaints: List<Paint> = ArrayList()
    private val rainAnimators: List<ValueAnimator> = ArrayList()

    private var rainIntensity: Int = 0

    constructor(context: Context?) : super(context) {
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RainView, 0, 0)

            rainIntensity =
                typedArray.getInt(R.styleable.RainView_rainIntensity, getDefaultRainIntensity())

            typedArray.recycle()
        } else {
            rainIntensity = getDefaultRainIntensity()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        //Init rain
        if (rain.isEmpty()) {
            addDrops(0, rainIntensity)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        rain.forEachIndexed { dropIndex, drop ->
            canvas.drawLine(
                drop.x,
                drop.y,
                drop.x,
                drop.y + context.dpToPx(drop.height),
                rainPaints[dropIndex]
            )
        }
    }

    fun getRainIntensity(): Int = rainIntensity

    fun setRainIntensity(intensity: Int) {
        if (intensity != 0) {
            if (rainIntensity < intensity) {
                addDrops(rainIntensity, intensity)
            } else {
                removeDrops(rainIntensity - 1, intensity)
            }
        } else clearRain()

        rainIntensity = intensity
        requestLayout()
    }

    private fun addDrops(from: Int, to: Int) {
        for (dropIndex in from until to) {
            val drop = getDrop()
            (rain as ArrayList).add(dropIndex, drop)

            val dropPaint = getDropPaint(drop.width)
            (rainPaints as ArrayList).add(dropPaint)

            val dropAnimator = getDropValueAnimator(dropIndex)
            (rainAnimators as ArrayList).add(dropAnimator)

            startAnimation(dropIndex)
        }
    }

    private fun removeDrops(from: Int, to: Int) {
        for (dropIndex in from downTo to) {

            rainAnimators[dropIndex].removeAllUpdateListeners()

            (rainAnimators as ArrayList).removeAt(dropIndex)
            (rainPaints as ArrayList).removeAt(dropIndex)
            (rain as ArrayList).removeAt(dropIndex)
        }
    }

    private fun clearRain() {
        for (animator in rainAnimators) {
            animator.removeAllUpdateListeners()
        }
        (rainAnimators as ArrayList).clear()
        (rainPaints as ArrayList).clear()
        (rain as ArrayList).clear()
    }

    private fun startAnimation(dropIndex: Int) {
        rainAnimators[dropIndex].cancel()
        rainAnimators[dropIndex].start()
    }

    private fun getDrop(): Drop {
        val width = getRandomDropWidth()
        val height = getRandomDropHeight()
        val speed = getRandomDropSpeed()
        val drop = Drop(width, height, speed)

        val x = getRandomDropXCoordinate()
        drop.x = x

        return drop
    }

    private fun getDropPaint(width: Float): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorDropEndGradient)
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
            strokeWidth = width
        }
    }

    private fun getDropValueAnimator(dropIndex: Int): ValueAnimator {
        return ValueAnimator.ofInt(0, measuredHeight).apply {
            addUpdateListener { valueAnimator ->
                val newYCoordinate = (valueAnimator.animatedValue as Int).toFloat()
                rain[dropIndex].y = newYCoordinate

                invalidate()
            }
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            duration = rain[dropIndex].speed
            startDelay = getRandomDropSpeed()
        }
    }

    private fun getRandomDropWidth() = (Random.nextInt(
        (DEFAULT_MAX_DROP_WIDTH - DEFAULT_MIN_DROP_WIDTH) + 1
    ) + DEFAULT_MIN_DROP_WIDTH).toFloat()

    private fun getRandomDropHeight() = (Random.nextInt(
        (DEFAULT_MAX_DROP_HEIGHT - DEFAULT_MIN_DROP_HEIGHT) + 1
    ) + DEFAULT_MIN_DROP_HEIGHT).toFloat()

    private fun getRandomDropSpeed() = (Random.nextInt(
        (DEFAULT_MAX_DROP_SPEED - DEFAULT_MIN_DROP_SPEED) + 1
    ) + DEFAULT_MIN_DROP_SPEED).toLong()

    private fun getRandomDropXCoordinate() = Random.nextInt(measuredWidth).toFloat()

    private fun getDefaultRainIntensity() = DEFAULT_RAIN_INTENSITY

    companion object {
        private const val DEFAULT_RAIN_INTENSITY = 200

        private const val DEFAULT_MIN_DROP_WIDTH = 1
        private const val DEFAULT_MAX_DROP_WIDTH = 2

        private const val DEFAULT_MIN_DROP_HEIGHT = 20
        private const val DEFAULT_MAX_DROP_HEIGHT = 60

        private const val DEFAULT_MIN_DROP_SPEED = 500
        private const val DEFAULT_MAX_DROP_SPEED = 1200
    }
}