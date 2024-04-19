package com.lux.light.meter.luminosity.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import com.lux.light.meter.luminosity.R

class CustomBottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_bottom_navigation, this, true)

        // Alt gezinme öğelerini burada başlatın ve gerektiğinde dinamik olarak işleyin
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            // Ev düğmesine tıklandığında yapılacak işlemler
            // Örneğin fragment değiştirme gibi
        }

        // Diğer alt gezinme öğelerini burada başlatın ve gerektiğinde dinamik olarak işleyin
    }
}
