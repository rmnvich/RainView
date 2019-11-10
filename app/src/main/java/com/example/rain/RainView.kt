package com.example.rain

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import kotlin.random.Random

class RainView : View {

    private data class Drop(
        val x: Float,
        val width: Float,
        val height: Float,
        val speed: Long
    ) {
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
        initRain()
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
        rainIntensity = intensity
        clearRain()

        requestLayout()
    }

    private fun clearRain() {
        for (animator in rainAnimators) {
            animator.removeAllUpdateListeners()
        }
        (rainAnimators as ArrayList).clear()
        (rainPaints as ArrayList).clear()
        (rain as ArrayList).clear()
    }

    private fun initRain() {
        for (dropIndex in 0 until rainIntensity) {
            val drop = getDrop()
            (rain as ArrayList).add(drop)

            val dropPaint = getDropPaint(drop.width, drop.height)
            (rainPaints as ArrayList).add(dropPaint)

            val dropAnimator = getDropValueAnimator(dropIndex)
            (rainAnimators as ArrayList).add(dropAnimator)

            startAnimation(dropIndex)
        }
    }

    private fun startAnimation(dropIndex: Int) {
        rainAnimators[dropIndex].cancel()
        rainAnimators[dropIndex].start()
    }

    private fun getDrop(): Drop {
        val x = getRandomDropXCoordinate()
        val width = getRandomDropWidth()
        val height = getRandomDropHeight()
        val speed = getRandomDropSpeed()
        return Drop(x, width, height, speed)
    }

    private fun getDropPaint(width: Float, height: Float): Paint {
        val shader = LinearGradient(
            0F,
            0F,
            0F,
            height * 4,
            ContextCompat.getColor(context, R.color.colorDropStartGradient),
            ContextCompat.getColor(context, R.color.colorDropEndGradient),
            Shader.TileMode.MIRROR
        )

        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
            strokeWidth = width
            setShader(shader)
        }
    }

    private fun getDropValueAnimator(dropIndex: Int): ValueAnimator {
        return ValueAnimator.ofInt(0, measuredHeight).apply {
            addUpdateListener { valueAnimator ->
                val newYCoordinate = valueAnimator.animatedValue as Int
                rain[dropIndex].y = newYCoordinate.toFloat()

                invalidate()
            }
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
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

        private const val DEFAULT_MIN_DROP_WIDTH = 4
        private const val DEFAULT_MAX_DROP_WIDTH = 6

        private const val DEFAULT_MIN_DROP_HEIGHT = 8
        private const val DEFAULT_MAX_DROP_HEIGHT = 16

        private const val DEFAULT_MIN_DROP_SPEED = 500
        private const val DEFAULT_MAX_DROP_SPEED = 1200
    }
}