package com.lux.light.meter.luminosity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.applovin.mediation.MaxAd
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.lux.light.meter.luminosity.databinding.ActivityMainBinding
import com.lux.light.meter.luminosity.fragment.HistoryFragment
import com.lux.light.meter.luminosity.fragment.LightmeterFragment
import com.lux.light.meter.luminosity.fragment.RecommendFragment
import com.lux.light.meter.luminosity.fragment.SettingsFragment
import com.lux.light.meter.luminosity.`object`.IsPremium
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
import com.revenuecat.purchases.getCustomerInfoWith

class MainActivity : AppCompatActivity(),MaxAdRevenueListener {
    private lateinit var binding: ActivityMainBinding
    var selected_pacaked: Package? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var lightTextView: TextView
    val itemId = "unique_item_id"
    val itemName = "Motivation"
    private var adView: MaxAdView? = null
    private lateinit var interstitialAdManager: InterstitialAdManager

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        interstitialAdManager = InterstitialAdManager(this)

        var bottomNavigationView = binding.bottomNavigationView

        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                this,
                "goog_WpWrDQlWjEJFISieSKGdOxTBChB"
            ).build()
        )
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, itemId)
            param(FirebaseAnalytics.Param.ITEM_NAME, itemName)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            // Tüm menü öğelerinin rengini varsayılan rengine geri döndürün
            for (i in 0 until bottomNavigationView.menu.size()) {
                val item = bottomNavigationView.menu.getItem(i)
                val spannable = SpannableString(item.title.toString())
                spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, spannable.length, 0)
                item.title = spannable

            }

            // Seçilen öğenin metin rengini turuncu yapın
            val spannable = SpannableString(menuItem.title.toString())
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#FFA500")),
                0,
                spannable.length,
                0
            )
            menuItem.title = spannable

            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()

            true
        }

        //fragment yolları

        replaceFragment(LightmeterFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Lightmeter -> replaceFragment(LightmeterFragment())
                R.id.history -> replaceFragment(HistoryFragment())
                R.id.recommend -> replaceFragment(RecommendFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
                else -> {

                }
            }
            true

        }
       checkPremium()

        Log.e("premium",IsPremium.is_premium.toString())



    }


    private fun replaceFragment(fragment: Fragment) {
        val fargmentManager = supportFragmentManager
        val fragmentTransaction = fargmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
    private fun showInterstitialAdOnClick() {
        interstitialAdManager.loadInterstitialAd()
        interstitialAdManager.showInterstitialAd()

    }


    override fun onAdRevenuePaid(p0: MaxAd) {
        // The onImpressionSuccess will be reported when the rewarded video and interstitial ad is
        // opened.
        // For banners, the impression is reported on load success. Log.d(TAG, "onImpressionSuccess" +
        // impressionData)
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION) {
            param(FirebaseAnalytics.Param.AD_PLATFORM, "Max")
            param(FirebaseAnalytics.Param.AD_SOURCE, p0.networkName)
            param(FirebaseAnalytics.Param.AD_FORMAT, p0.format.label)
            param(FirebaseAnalytics.Param.AD_UNIT_NAME, p0.adUnitId)
            param(FirebaseAnalytics.Param.CURRENCY, "USD")
            param(FirebaseAnalytics.Param.VALUE, p0.revenue)
        }


    }
    fun checkPremium(){
        Purchases.sharedInstance.getCustomerInfoWith { customerInfo ->
            haveSubscription(customerInfo.activeSubscriptions.isNotEmpty())
        }
    }
    fun haveSubscription(status:Boolean){
     IsPremium.is_premium = status

    }





}




