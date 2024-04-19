package com.lux.light.meter.luminosity.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CustomProgress5(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var maxProgress = 1000
    private var progressValue = 0

    fun setProgressValue(value: Int) {
        progressValue = value
        invalidate()
    }

    fun setMaxProgress(max: Int) {
        maxProgress = max
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barCount = 7 // Toplam çubuk sayısı
        val barWidth = width.toFloat() / 12 // 8 tane çubuk ve aralarında 2 boşluk
        val interval = maxProgress / barCount
        val a = 0.9f
        val margin = 32f // Soldan bırakılacak boşluk miktarı

        for (i in 0 until barCount) {

            val barHeightRatio = (a + 0.3f*i) / barCount // Her çubuk için farklı yükseklik oranı
            val barHeight = height.toFloat() * barHeightRatio

            val startProgress = i * interval
            val endProgress = (i + 1) * interval

            val barValue: Float
            if (progressValue >= endProgress) {
                barValue = barHeight
            } else if (progressValue in startProgress..endProgress) {
                val ratio = (progressValue - startProgress).toFloat() / interval
                barValue = ratio * barHeight // Çubuğun yüksekliğini hesapla
            } else {
                barValue = 0f
            }


            val left = i * (barWidth + margin) + margin // Çubuklar arasında boşluk bırakarak ortalama
            val right = left + barWidth
            val bottom = height.toFloat() -160 // Yeniden hesaplanan alt kısmı biraz yukarı kaydır
            val top = bottom - barHeight // Üst kısmı alt kısmından yükseklik kadar yukarıda olacak

            val paint = Paint()

            // Çubuğun rengini ayarla
            val ratio = barValue / barHeight
            val color = if (progressValue >= endProgress) {
                Color.rgb(249, 115, 22) // Progress dolu ise sarı renk
            } else {
                Color.GRAY // Progress dolmamışsa gri renk
            }
            paint.color = color

            // Yuvarlak köşeler için oval
            val rect = RectF(left, top, right, bottom)

            // Çubuğun iç dolgunluğunu çiz
            canvas.drawRoundRect(rect, 20f, 20f, paint)

            // Çubuğun kenarlarını çiz
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(rect, 20f, 20f, paint)

            // Altındaki değeri yaz

            // Çubuğun rengini ayarla
            paint.color = if (ratio < 0.5f) Color.GRAY else Color.rgb(249,115,22)
            paint.textSize = 30f
            paint.style = Paint.Style.FILL // Metnin stili düz yazı olarak ayarlanır

            val text = startProgress.toString()
            val textWidth = paint.measureText(text)
            val barStartX = left + (barWidth - textWidth) / 2
            val barCenterX = barStartX + textWidth / 2
            val x = barCenterX - textWidth / 2
            val y = bottom + 40 // Çubukların altındaki metnin y konumunu düzenle
            canvas.drawText(text, x, y, paint)


        }
    }

}
