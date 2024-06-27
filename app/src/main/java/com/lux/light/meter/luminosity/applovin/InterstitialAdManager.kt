package com.lux.light.meter.luminosity.applovin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.github.mikephil.charting.charts.LineChart
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.lux.light.meter.luminosity.databinding.FragmentLightmeterBinding
import com.lux.light.meter.luminosity.fragment.LightmeterFragment
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
import com.lux.light.meter.luminosity.`object`.ClickController
import com.lux.light.meter.luminosity.`object`.IsPremium
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class InterstitialAdManager(private val context: Context) : MaxAdListener, MaxAdRevenueListener {

    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0
    private var isAdLoaded = false
    private var isAdDisplayed = false
    private var binding: FragmentLightmeterBinding? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        loadInterstitialAd()  // Load the ad when the manager is initialized
    }

    private var adSuccessCallback: (() -> Unit)? = null
    private var adFailureCallback: (() -> Unit)? = null

    fun showInterstitialAdWithCallback(onSuccess: () -> Unit, onFailure: () -> Unit) {
        adSuccessCallback = onSuccess
        adFailureCallback = onFailure

        if (::interstitialAd.isInitialized && interstitialAd.isReady && !IsPremium.is_premium) {
            interstitialAd.showAd()
        } else {
            Log.e("reklam hatası", "Interstitial ad not ready or failed to load")
            adFailureCallback?.invoke() // Call the failure callback if the ad is not ready
            adFailureCallback = null
        }
    }

    fun loadInterstitialAd() {
        val activity = context as? Activity
        activity?.let {
            interstitialAd = MaxInterstitialAd("86893fda715a18da", it)
            interstitialAd.setListener(this)
            interstitialAd.setRevenueListener(this)
            interstitialAd.loadAd()
        }
    }

    override fun onAdLoaded(maxAd: MaxAd) {
        isAdLoaded = true
        retryAttempt = 0.0 // Reset retry attempt on successful load
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        isAdLoaded = false
        adFailureCallback?.invoke() // Call the failure callback on ad load failure
        adFailureCallback = null
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis(Math.pow(2.0, Math.min(6.0, retryAttempt)).toLong())
        CoroutineScope(Dispatchers.Main).launch {
            delay(delayMillis)
            interstitialAd.loadAd()
        }
    }

    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
        interstitialAd.loadAd()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {
        isAdDisplayed = true
    }

    override fun onAdHidden(maxAd: MaxAd) {
        isAdDisplayed = false
        adSuccessCallback?.invoke() // Call the success callback when the ad is hidden (finished displaying)
        adSuccessCallback = null
        loadInterstitialAd() // Load the next ad
    }

    override fun onAdClicked(p0: MaxAd) {}

    fun setBinding(binding: FragmentLightmeterBinding) {
        this.binding = binding
    }

    fun showInterstitialAd() {
        if (ClickController.click_count_control != 0 && Addisplay.number_of_ad_impressions % ClickController.click_count_control == 0 && !IsPremium.is_premium) {
            if (::interstitialAd.isInitialized && interstitialAd.isReady) {
                interstitialAd.showAd()
                binding?.let {
                    (it.lineChart as LineChart).clear()
                    (it.lineChart as LineChart).invalidate()
                    Log.e("reklam hatası", "tıklama 3ün katı oldu " + "Interstitial ad not ready or failed to load")

                } ?: Log.e("reklam hatası", "Binding is not initialized")
            } else {
                Log.e("reklam hatası", "Interstitial ad not ready or failed to load")
                loadInterstitialAd() // Ensure ad is loaded if not ready
            }
        } else {
            Log.e("reklam hatası", "ClickController.click_count_control is zero or Addisplay.number_of_ad_impressions is not divisible by ClickController.click_count_control")
        }
    }

    fun showInterstitialAdone() {
        if (::interstitialAd.isInitialized && interstitialAd.isReady && !IsPremium.is_premium) {
            interstitialAd.showAd()
            Log.e("reklam hatası", "reklam yükleniyor")
        } else {
            Log.e("reklam hatası", "Interstitial ad not ready or failed to load")
            loadInterstitialAd() // Ensure ad is loaded if not ready
        }
    }

    override fun onAdRevenuePaid(impressionData: MaxAd) {
        Log.d("FirebaseAnalytics", "onAdRevenuePaid Called")
        impressionData.let {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION) {
                param(FirebaseAnalytics.Param.AD_PLATFORM, "appLovin")
                param(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.adUnitId)
                param(FirebaseAnalytics.Param.AD_FORMAT, impressionData.format.label)
                param(FirebaseAnalytics.Param.AD_SOURCE, impressionData.networkName)
                param(FirebaseAnalytics.Param.VALUE, impressionData.revenue)
                param(FirebaseAnalytics.Param.CURRENCY, "USD")
            }
        }
    }
}
