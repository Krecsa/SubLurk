package com.krecsa.sublurk.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var progress = 0F
    private var animator: ValueAnimator? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    init {
        startAnimation()
    }

    fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0F, 1F).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    fun stopAnimation() {
        animator?.cancel()
        progress = 0F
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = minOf(width, height) / 2f - 12f

        for (i in 0..2) {
            val waveProgress = (progress + i * 0.33f) % 1F
            val radius = maxRadius * waveProgress

            val alpha = ((1F - waveProgress) * 255).toInt().coerceIn(0, 255)

            val color = getThemeColor()
            paint.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

            canvas.drawCircle(centerX, centerY, radius, paint)
        }
    }

    private fun getThemeColor(): Int {
        val typedValue = android.util.TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}