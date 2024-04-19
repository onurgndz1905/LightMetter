package com.lux.light.meter.luminosity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
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
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationItemView
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
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith

class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private lateinit var binding: ActivityMainBinding
    var selected_pacaked : Package? = null

    private lateinit var lightTextView: TextView

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bottomNavigationView = binding.bottomNavigationView

        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                this,
                "goog_WpWrDQlWjEJFISieSKGdOxTBChB"
            ).build())

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
            spannable.setSpan(ForegroundColorSpan(Color.parseColor("#FFA500")), 0, spannable.length, 0)
            menuItem.title = spannable

            // Diğer işlevselliği buraya ekleyin

            true
        }

        //fragment yolları

        replaceFragment(LightmeterFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Lightmeter -> replaceFragment(LightmeterFragment())
                R.id.history -> replaceFragment(HistoryFragment())
                R.id.recommend -> replaceFragment(RecommendFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
                else ->{

                }
            }
            true

        }



    }


    private fun replaceFragment(fragment : Fragment){
        val fargmentManager = supportFragmentManager
        val fragmentTransaction = fargmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    override fun onPurchasesUpdated(p0: BillingResult, purchases: MutableList<Purchase>?) {
        when (p0.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // Satın alma işlemi başarılı oldu
                purchases?.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // Satın alma yapıldı
                        val sku = purchase.orderId // Ürün ID'si
                        val purchaseTimeMillis = purchase.purchaseTime // Satın alma zamanı (milisaniye cinsinden)
                        val currentTimeMillis = System.currentTimeMillis() // Şu anki zaman (milisaniye cinsinden)

                        // Satın alınan ürünün süresi (örneğin haftalık abonelik)
                        val subscriptionPeriodMillis = getSubscriptionPeriodMillis(sku.toString())

                        // Süre doldu mu kontrolü
                        val isSubscriptionExpired = currentTimeMillis > purchaseTimeMillis + subscriptionPeriodMillis

                        // isPremium değerini güncelle
                        IsPremium.is_premium = !isSubscriptionExpired
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // Kullanıcı satın alma işlemini iptal etti
            }
            // Diğer durumlar için gerekli işlemleri burada yapabilirsiniz
            // ...
        }
    }

    private fun getSubscriptionPeriodMillis(sku: String): Long {
        // Ürün ID'sine göre abonelik süresini döndür
        // Örneğin, haftalık abonelik için 7 günün milisaniye cinsinden değeri
        // Daha fazla ürün ve süre tanımlamalarını burada yapabilirsiniz
        return when (sku) {
            "weekly_subscription" -> 7 * 24 * 60 * 60 * 1000L // 7 gün
            "monthly_subscription" -> 30 * 24 * 60 * 60 * 1000L // 30 gün
            "yearly_subscription" -> 365 * 24 * 60 * 60 * 1000L // 365 gün
            else -> 0L // Bilinmeyen ürün ID'si
        }
    }



}
