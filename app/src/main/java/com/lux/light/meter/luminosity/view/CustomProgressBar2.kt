package com.lux.light.meter.luminosity.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.lux.light.meter.luminosity.`object`.Unit

class CustomProgressBar2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private lateinit var sharedPreferences: SharedPreferences
    private var unitSettings: Float = 1f // Default unit settings

    private val barCount = 18 // Çubuk sayısı
    private val barSpacing = 32 // Çubuklar arası boşluk
    private val barHeight = 120 // Çubuk yüksekliği
    private val barThickness = 8 // Çubuk kalınlığı
    private val cornerRadius = 32 // Köşe yarıçapı

    private val maxProgress = 500 // Maksimum ilerleme değeri
    private var progress = 40 // Doluluk oranı yüzdesi

    private val textSize = 72f // Yazı boyutu
    private val textMargin = 0f // Yazı kenar boşluğu

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // shared preferences başlatılıyor
        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings = savedUnit
        val totalWidth = width - paddingLeft - paddingRight
        val singleBarWidth = (totalWidth - (barCount - 1) * barSpacing) / barCount

        val paint = Paint()
        paint.isAntiAlias = true

        for (i in 0 until barCount) {
            val left = i * (singleBarWidth + barSpacing) + paddingLeft
            val right = left + singleBarWidth
            val top = (height - barHeight) / 2
            val bottom = top + barHeight

            // Çubuk boyanacak yüzdeyi hesapla
            val filledPercentage = if (progress <= maxProgress) {
                (progress * 100) / maxProgress
            } else {
                (progress * 100) / 1000
            }

            // Rengi belirle
            val color = if (i < filledPercentage * barCount / 100) {
                Color.rgb(249, 115, 22) // Turuncu
            } else {
                Color.rgb(32, 32, 32)
            }

            paint.color = color
            // Çubuk kalınlığını barThickness değeriyle belirle
            paint.strokeWidth = barThickness.toFloat()
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawRoundRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat(), paint)
        }

        val textColor = Color.rgb(249, 115, 22)

        // Robotik yazıyı çiz
        val textPaint = Paint()
        textPaint.color = textColor // Yazı rengini belirle
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER

        val textY = height.toFloat() - textMargin
        val textX = width.toFloat() / 2

        val unitText = when (Unit.unitsettings) {
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

        canvas.drawText("$progress $unitText", textX, textY, textPaint)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }


}
