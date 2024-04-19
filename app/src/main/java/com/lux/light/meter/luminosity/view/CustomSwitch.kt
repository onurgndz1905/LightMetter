package com.lux.light.meter.luminosity.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.lux.light.meter.luminosity.R

class CustomSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isChecked = false
    private val switchWidth = 120f
    private val switchHeight = 60f
    private val switchRadius = switchHeight / 2
    private val padding = 2f
    private val switchPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        switchPaint.color = ContextCompat.getColor(context, R.color.switch_color)
        thumbPaint.color = ContextCompat.getColor(context, R.color.switchicon_color)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRoundRect(
            RectF(0f, 0f, switchWidth, switchHeight),
            switchRadius,
            switchRadius,
            switchPaint
        )

        val thumbColor = if (isChecked) {
            ContextCompat.getColor(context, R.color.switchicon_color)
        } else {
            ContextCompat.getColor(context, R.color.switch_color_off)
        }

        thumbPaint.color = thumbColor

        val thumbLeft = if (isChecked) {
            switchWidth - switchHeight
        } else {
            0f
        }

        canvas.drawCircle(
            thumbLeft + switchHeight / 2,
            switchHeight / 2,
            switchHeight / 2 - padding,
            thumbPaint
        )
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    fun toggle() {
        isChecked = !isChecked
        invalidate()
    }
}
