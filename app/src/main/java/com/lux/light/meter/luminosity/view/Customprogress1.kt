package com.lux.light.meter.luminosity.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.lux.light.meter.luminosity.R
import kotlin.math.min



@SuppressLint("CustomViewStyleable")
class Customprogress1(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress = 0
    private var progressMax = 1000
    private val strokeWidth = 20f
    private val progressStartColor = Color.rgb(255, 162, 109)
    private val progressEndColor = Color.rgb(255, 92, 0)
    private val backgroundColor = Color.parseColor("#202020")
    private val textSize = 50f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
        progress = typedArray.getInt(R.styleable.CustomProgressBar_progress, 0)
        progressMax = typedArray.getInt(R.styleable.CustomProgressBar_progressMax, 500)
        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 2 - strokeWidth
        val halfStrokeWidth = strokeWidth / 2

        val paint = Paint()
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth

        // Background circle
        paint.style = Paint.Style.STROKE
        paint.color = backgroundColor
        canvas.drawCircle(width / 2, height / 2, radius, paint)

        // Progress arc background
        paint.style = Paint.Style.STROKE
        paint.color = backgroundColor // Gri renk
        paint.strokeWidth = strokeWidth * 2
        canvas.drawArc(
            width / 2 - radius + halfStrokeWidth,
            height / 2 - radius + halfStrokeWidth,
            width / 2 + radius - halfStrokeWidth,
            height / 2 + radius - halfStrokeWidth,
            0f,
            360f,
            false,
            paint
        )

        // Progress arc
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = strokeWidth * 2

        // Gradient for progress
        val gradient = LinearGradient(
            0f, 0f, width, height,
            progressStartColor, progressEndColor,
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient


        canvas.drawArc(
            width / 2 - radius + halfStrokeWidth,
            height / 2 - radius + halfStrokeWidth,
            width / 2 + radius - halfStrokeWidth,
            height / 2 + radius - halfStrokeWidth,
            -90f,
            360 * progress / progressMax.toFloat(),
            false,
            paint
        )

        // Text (progress value only)
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        val text = "$progress"
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(
            text,
            width / 2f,
            height / 2f + textBounds.height() / 2f,
            paint
        )


    // Gradient for progress


    }

    fun setProgress(progress: Int) {
        if (progress in 0..progressMax) {
            this.progress = progress
            invalidate()
        }
    }

    fun setMaxProgress(max: Int) {
        if (max > 0) {
            this.progressMax = max
            invalidate()
        }
    }
}
