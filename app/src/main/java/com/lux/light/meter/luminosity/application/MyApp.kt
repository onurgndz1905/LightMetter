package com.lux.light.meter.luminosity.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lux.light.meter.luminosity.applovin.ExampleAppOpenManager
import com.lux.light.meter.luminosity.`object`.ClickController
import com.lux.light.meter.luminosity.`object`.CurrentIndex
import com.revenuecat.purchases.Package
import java.util.concurrent.Executors

class MyApp : Application() {
    private lateinit var appOpenManager: ExampleAppOpenManager
    private lateinit var sharedPreferences: SharedPreferences
    var selected_package: Package? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // SDK'yi başlat
        AppLovinSdk.getInstance(this).initializeSdk { configuration: AppLovinSdkConfiguration ->
            appOpenManager = ExampleAppOpenManager(applicationContext)
        }

        // Uygulama başladığında veya yeniden başladığında çağrılacak metot
        resetCurrentIndex()

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(Executors.newSingleThreadExecutor(), OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    val click_count_control = remoteConfig.getString("click_count_control")
                    val click = sharedPreferences.getInt("click", click_count_control.toInt())
                    ClickController.click_count_control = click
                }
            })
    }

    private fun resetCurrentIndex() {
        val sharedPreferences = applicationContext.getSharedPreferences(CurrentIndex.PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(CurrentIndex.KEY_CURRENT_INDEX, 0) // Değeri 0 olarak ayarla
        editor.apply()
    }
}
