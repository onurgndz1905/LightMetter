package com.lux.light.meter.luminosity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lux.light.meter.luminosity.R

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        supportActionBar?.hide()

    }
}