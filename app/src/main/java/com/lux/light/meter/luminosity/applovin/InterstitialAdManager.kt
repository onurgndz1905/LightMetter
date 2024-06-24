package com.lux.light.meter.luminosity.applovin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.github.mikephil.charting.charts.LineChart
import com.lux.light.meter.luminosity.databinding.FragmentLightmeterBinding
import com.lux.light.meter.luminosity.fragment.LightmeterFragment
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
import com.lux.light.meter.luminosity.`object`.ClickController
import com.lux.light.meter.luminosity.`object`.IsPremium
import java.util.concurrent.TimeUnit

class InterstitialAdManager(private val context: Context) : MaxAdListener {

    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0
    private var isAdLoaded = false
    private var isAdDisplayed = false
    private lateinit var binding: FragmentLightmeterBinding

    fun loadInterstitialAd() {

        val activity = context as? Activity
        activity?.let {
            interstitialAd = MaxInterstitialAd("86893fda715a18da", it)
            interstitialAd.setListener(this)
            interstitialAd.loadAd()
        }

    }

    override fun onAdLoaded(maxAd: MaxAd) {
        // Reklam yüklendiğinde bayrağı true yap
        isAdLoaded = true
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        // Reklam yüklemesi başarısız olduğunda bayrağı sıfırla ve yeniden dene
        isAdLoaded = false
        retryAttempt++
        val delayMillis =
            TimeUnit.SECONDS.toMillis(Math.pow(2.0, Math.min(6.0, retryAttempt)).toLong())
        android.os.Handler().postDelayed({ interstitialAd.loadAd() }, delayMillis)
    }

    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
        interstitialAd.loadAd()

    }

    override fun onAdDisplayed(maxAd: MaxAd) {
        // Reklam gösterildiğinde bayrağı true yap
        isAdDisplayed = true
    }

    override fun onAdHidden(maxAd: MaxAd) {
        // Reklam gizlendiğinde bayrağı sıfırla ve bir sonraki reklamı yükle
        isAdDisplayed = false
        interstitialAd.loadAd()

    }

    override fun onAdClicked(p0: MaxAd) {}

    fun setBinding(binding: FragmentLightmeterBinding) {
        this.binding = binding
    }
    fun showInterstitialAd() {
        if (ClickController.click_count_control != 0 && Addisplay.number_of_ad_impressions % ClickController.click_count_control == 0 && IsPremium.is_premium== false) {
            Log.e("reklam hatası", "${Addisplay.number_of_ad_impressions},${ClickController.click_count_control}")
            if (::interstitialAd.isInitialized && interstitialAd.isReady) {
                interstitialAd.showAd()
                (binding.lineChart as LineChart).clear()
                (binding.lineChart as LineChart).invalidate()
                Log.e("reklam hatası", "tıklama 3ün katı oldu " + "Interstitial ad not ready or failed to load")
            } else {
                // Reklam hazır değilse veya yüklenmediyse bir hata işleyebilirsiniz
                Log.e("reklam hatası", "Interstitial ad not ready or failed to load")
            }
        } else {
            // ClickController.click_count_control sıfır olduğunda bir hata oluşmasını engellemek için bir işlem yapabilirsiniz
            Log.e("reklam hatası", "ClickController.click_count_control is zero or Addisplay.number_of_ad_impressions is not divisible by ClickController.click_count_control")
        }
    }

    @SuppressLint("LogNotTimber")
    fun showInterstitialAdnotlinechart() {
        if (ClickController.click_count_control != 0 && Addisplay.number_of_ad_impressions % ClickController.click_count_control == 0 && IsPremium.is_premium== false) {

            Log.e("reklam hatası","${Addisplay.number_of_ad_impressions},${ClickController.click_count_control}")
            if (::interstitialAd.isInitialized && interstitialAd.isReady) {
                interstitialAd.showAd()
            }
        }
        else {
            // Reklam hazır değilse veya yüklenmediyse bir hata işleyebilirsiniz
            Log.e("reklam hatası", "Interstitial ad not ready or failed to load")
        }
    }
    fun showInterstitialAdone() {

        if (::interstitialAd.isInitialized && interstitialAd.isReady && !IsPremium.is_premium) {
                interstitialAd.showAd()
                Log.e("reklam hatası", "reklam yükleniyor")
        } else {
            // Reklam hazır değilse veya yüklenmediyse bir hata işleyebilirsiniz
            Log.e("reklam hatası", "Interstitial ad not ready or failed to load")
        }
    }

}
