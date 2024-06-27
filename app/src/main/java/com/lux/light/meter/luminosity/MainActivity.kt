package com.lux.light.meter.luminosity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.applovin.mediation.MaxAd
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
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.ads.MaxAdView
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel2
import com.revenuecat.purchases.getCustomerInfoWith

class MainActivity : AppCompatActivity(),MaxAdRevenueListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var lightTextView: TextView

    private var adView: MaxAdView? = null
    private lateinit var interstitialAdManager: InterstitialAdManager
    private lateinit var paywallViewModel: PaywallViewModel
    private lateinit var paywallviemodel2: PaywallViewModel2
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

        paywallViewModel = ViewModelProvider(this).get(PaywallViewModel::class.java)
        paywallviemodel2 = ViewModelProvider(this).get(PaywallViewModel2::class.java)


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



            true
        }

        //fragment yolları

        replaceFragment(LightmeterFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Lightmeter ->
                {
                    replaceFragment(LightmeterFragment())
                    Unit.homeactiffe = true
                    Addisplay.number_of_ad_impressions++
                    showInterstitialAdOnClick()
                }
                R.id.history ->
                {
                    replaceFragment(HistoryFragment())
                    Addisplay.number_of_ad_impressions++
                    showInterstitialAdOnClick()
                    Unit.homeactiffe = false

                }
                R.id.recommend ->
                {
                    replaceFragment(RecommendFragment())
                    Addisplay.number_of_ad_impressions++
                    showInterstitialAdOnClick()
                    Unit.homeactiffe = false

                }
                R.id.settings ->
                {
                    replaceFragment(SettingsFragment())
                    Addisplay.number_of_ad_impressions++
                    showInterstitialAdOnClick()
                    Unit.homeactiffe = false

                }
                else -> {

                }
            }
            true

        }
       checkPremium()

        if (!IsPremium.is_premium){
            paywallViewModel.setBooleanValue(true)

        }

        Log.e("premium",IsPremium.is_premium.toString())



    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showInterstitialAdOnClick() {

        if (Addisplay.number_of_ad_impressions%3 ==1){
            interstitialAdManager.loadInterstitialAd()

        }

        interstitialAdManager.showInterstitialAd() // Tabloyu sıfırla


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





    fun checkPremium(){
        Purchases.sharedInstance.getCustomerInfoWith { customerInfo ->
            haveSubscription(customerInfo.activeSubscriptions.isNotEmpty())
        }
    }
    fun haveSubscription(status:Boolean){
     IsPremium.is_premium = status

    }

    override fun onBackPressed() {

        if (paywallViewModel.booleanLiveData.value == true || paywallviemodel2.booleanLiveData.value == true || !Unit.homeactiffe) {
            paywallViewModel.setBooleanValue(false)
            paywallviemodel2.setBooleanValue(false)
            binding.bottomNavigationView.selectedItemId = R.id.Lightmeter
        }
        else{
            super.onBackPressed()

        }
    }





}




