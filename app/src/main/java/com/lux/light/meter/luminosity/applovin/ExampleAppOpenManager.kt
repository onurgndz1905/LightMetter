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
    private val appOpenAd: MaxAppOpenAd
    private val ADS_UNIT = "9588c349a9370487"

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        appOpenAd = MaxAppOpenAd(ADS_UNIT, context)
        appOpenAd.setListener(this)
        appOpenAd.loadAd()
    }

    private fun showAdIfReady() {
        if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized() && IsPremium.is_premium== false) return

        if (appOpenAd.isReady() && IsPremium.is_premium== false) {
            appOpenAd.showAd(ADS_UNIT)
        } else {
            appOpenAd.loadAd()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfReady()
    }

    override fun onAdLoaded(ad: MaxAd) {}

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}

    override fun onAdDisplayed(ad: MaxAd) {}

    override fun onAdClicked(ad: MaxAd) {}

    override fun onAdHidden(ad: MaxAd) {
        appOpenAd.loadAd()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        appOpenAd.loadAd()
    }
}
