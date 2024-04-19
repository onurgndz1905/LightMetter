package com.lux.light.meter.luminosity.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.lux.light.meter.luminosity.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomProgressBar3(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress = 0
    private var progressMax = 1000
    private val strokeWidth = 2f
    private val progressStartColor = Color.rgb(255, 92, 0) // Renkleri değiştirildi
    private val progressEndColor = Color.rgb(255, 162, 109) // Renkleri değiştirildi
    private val color_stroke = Color.parseColor("#202020")
    private val textSize = 50f
    private val numberOfBars = 24

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
        progress = typedArray.getInt(R.styleable.CustomProgressBar_progress, 0)
        progressMax = typedArray.getInt(R.styleable.CustomProgressBar_progressMax, 500)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)



        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 3.0f * 1.1f - strokeWidth // Yarıçapı 1.1 kat artır
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
            -180f,
            180f,
            false,
            paint
        )

        // Progress arc
        val progressAngle = (180 * progress / progressMax.toFloat()) // İlerleme açısı hesaplanıyor
        paint.clearShadowLayer() // Önceki gölge katmanını temizleyin
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
        // Draw shadow
        val shadowRadius = radius + halfStrokeWidth // Gölgenin uygulanacağı yarıçap
        paint.setShadowLayer(10f, 0f, 0f, Color.rgb(255, 165, 0)) // Gölgelendirme katmanını ayarla
        paint.style = Paint.Style.STROKE // Şeklin dış çizgisine gölge uygula
        paint.color = Color.TRANSPARENT // Rengi şeffaf yap
        canvas.drawArc(
            width / 2 - shadowRadius,
            height / 2 - shadowRadius,
            width / 2 + shadowRadius,
            height / 2 + shadowRadius,
            -180f,//dairenin  doluma başladığı yeri belirler
            progressAngle,
            false,
            paint
        )
        // Draw progress bars
        val barLength = 50f
        val angleStep = 180f / numberOfBars // Changed angle step for half circle
        val gapLength = 10f // Boşluk uzunluğu

        val currentProgress = progress.toFloat() / progressMax.toFloat()
        val threshold = numberOfBars * currentProgress

        for (i in 0 until numberOfBars) {
            val angle = -180f + i * angleStep // Bars start from -180 degrees
            val startX = (width / 2 + (radius + halfStrokeWidth + gapLength) * cos(Math.toRadians(angle.toDouble()))).toFloat()
            val startY = (height / 2 + (radius + halfStrokeWidth + gapLength) * sin(Math.toRadians(angle.toDouble()))).toFloat()
            val stopX = (width / 2 + (radius + halfStrokeWidth + gapLength + barLength) * cos(Math.toRadians(angle.toDouble()))).toFloat()
            val stopY = (height / 2 + (radius + halfStrokeWidth + gapLength + barLength) * sin(Math.toRadians(angle.toDouble()))).toFloat()

            val currentColor = if (i < threshold) getColorBetween(progressEndColor, progressStartColor, currentProgress) else Color.GRAY

            paint.color = currentColor
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
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
