package com.lux.light.meter.luminosity.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.`object`.Unit
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Customprogress4(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress = 0
    private var progressMax = 1000
    private val strokeWidth = 4f
    private val progressStartColor = Color.rgb(255, 92, 0) // Renkleri değiştirildi
    private val progressEndColor = Color.rgb(255, 162, 109) // Renkleri değiştirildi
    private val color_stroke = Color.parseColor("#202020")
    private val textSize = 50f
    private val numberOfBars = 32
    private lateinit var sharedPreferences: SharedPreferences


    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
        progress = typedArray.getInt(R.styleable.CustomProgressBar_progress, 0)
        progressMax = typedArray.getInt(R.styleable.CustomProgressBar_progressMax, 500)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // shared preferences başlatılıyor
        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings = savedUnit
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 3.0f - strokeWidth
        val halfStrokeWidth = strokeWidth / 2

        val paint = Paint()
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth

        // Progress arc background
        paint.style = Paint.Style.STROKE
        paint.color = color_stroke
        paint.strokeWidth = strokeWidth * 2

        canvas.drawArc(
            width / 2 - radius + halfStrokeWidth,
            height / 2 - radius + halfStrokeWidth,
            width / 2 + radius - halfStrokeWidth,
            height / 2 + radius - halfStrokeWidth,
            0f,
            360f, // Tam daire
            false,
            paint
        )

        // Progress arc
        val progressAngle = (360 * progress / progressMax.toFloat()) // İlerleme açısı hesaplanıyor
        paint.color = getColorBetween(Color.rgb(234,88,12),Color.rgb(234,88,12), progress.toFloat() / progressMax.toFloat())
     // Yuvarlağın içini progress değerine göre boyayalım
        canvas.drawArc(
            width / 2 - radius + halfStrokeWidth,
            height / 2 - radius + halfStrokeWidth,
            width / 2 + radius - halfStrokeWidth,
            height / 2 + radius - halfStrokeWidth,
            -180f,//dairenin  doluma başladığı yeri belirler
            progressAngle,
            false,
            paint
        )


        val barLength = 40f
        val angleStep = 360f / numberOfBars

        val currentProgress = progress.toFloat() / progressMax.toFloat()
        val threshold = numberOfBars * currentProgress

        for (i in 0 until numberOfBars) {
            val angle = -180f + i * angleStep // Çubukların başlangıç açısını -180 derece olarak ayarlayalım
            val startX = (width / 2 + (radius + halfStrokeWidth + barLength) * cos(Math.toRadians(angle.toDouble()))).toFloat()
            val startY = (height / 2 + (radius + halfStrokeWidth + barLength) * sin(Math.toRadians(angle.toDouble()))).toFloat()
            val stopX = (width / 2 + (radius + halfStrokeWidth + barLength + barLength) * cos(Math.toRadians(angle.toDouble()))).toFloat()
            val stopY = (height / 2 + (radius + halfStrokeWidth + barLength + barLength) * sin(Math.toRadians(angle.toDouble()))).toFloat()


            val currentColor = if (i < threshold) getColorBetween(progressEndColor, progressStartColor, currentProgress) else Color.GRAY

            paint.color = currentColor
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }


        // Text (progress value only)
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(234,88,12)
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        val text = "$progress" + " ${
            when (Unit.unitsettings) {
                0.0929f -> {
                    "FC"
                }
                1f -> {
                    "Lux"

                }
                else -> {
                    "Lux"
                }
            }
        }"
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(
            text,
            width / 2f,
            height / 2f + textBounds.height() / 2f,
            paint
        )
    }

    private fun getColorBetween(startColor: Int, endColor: Int, ratio: Float): Int {
        val r = (Color.red(startColor) * (1 - ratio) + Color.red(endColor) * ratio).toInt()
        val g = (Color.green(startColor) * (1 - ratio) + Color.green(endColor) * ratio).toInt()
        val b = (Color.blue(startColor) * (1 - ratio) + Color.blue(endColor) * ratio).toInt()
        return Color.rgb(r, g, b)
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
