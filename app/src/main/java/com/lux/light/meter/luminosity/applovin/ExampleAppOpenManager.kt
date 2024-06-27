package com.lux.light.meter.luminosity.applovin

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.sdk.AppLovinSdk
import com.lux.light.meter.luminosity.`object`.IsPremium

class ExampleAppOpenManager(private val context: Context) : LifecycleObserver, MaxAdListener {
    private var appOpenAd: MaxAppOpenAd? = null
    private val ADS_UNIT = "9588c349a9370487"

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        initializeAd()
    }

    private fun initializeAd() {
        appOpenAd = MaxAppOpenAd(ADS_UNIT, context)
        appOpenAd?.setListener(this)
        appOpenAd?.loadAd()
    }

    private fun showAdIfReady() {
        if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized || IsPremium.is_premium) return

        if (appOpenAd?.isReady == true) {
            appOpenAd?.showAd(ADS_UNIT)
        } else {
            // Reklam hazır değilse veya başka bir nedenle gösterilemiyorsa tekrar yükle
            appOpenAd?.loadAd()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfReady()
    }

    override fun onAdLoaded(ad: MaxAd) {
        // Reklam yüklendiğinde yapılacak işlemler
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        // Reklam yüklenemediğinde yapılacak işlemler, hata durumunu yönetin
        appOpenAd?.loadAd()
    }

    override fun onAdDisplayed(ad: MaxAd) {
        // Reklam gösterildiğinde yapılacak işlemler
    }

    override fun onAdClicked(ad: MaxAd) {
        // Reklama tıklandığında yapılacak işlemler
    }

    override fun onAdHidden(ad: MaxAd) {
        // Reklam gizlendiğinde yapılacak işlemler, reklamı tekrar yükle
        appOpenAd?.loadAd()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        // Reklam gösterilemediğinde yapılacak işlemler, hata durumunu yönetin
        appOpenAd?.loadAd()
    }
}
