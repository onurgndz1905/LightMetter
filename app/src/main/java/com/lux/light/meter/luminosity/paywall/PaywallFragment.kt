package com.lux.light.meter.luminosity.paywall

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.lux.light.meter.luminosity.MainActivity
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentPaywallBinding
import com.lux.light.meter.luminosity.fragment.RecommendFragment
import com.lux.light.meter.luminosity.fragment.SettingsFragment
import com.lux.light.meter.luminosity.`object`.IsPremium
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel2
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith


class PaywallFragment : Fragment() {

    var click_Paywall = true

    var selected_pacaked : Package? = null
    var selected_pacaked_weekly : Package? = null
    var selected_pacaked_yearly : Package? = null
    private lateinit var paywallviemodel: PaywallViewModel
    private lateinit var paywallviemodel2: PaywallViewModel2
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: FragmentPaywallBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaywallBinding.inflate(layoutInflater,container,false)

        paywallviemodel = ViewModelProvider(requireActivity()).get(PaywallViewModel::class.java)
        paywallviemodel2 = ViewModelProvider(requireActivity()).get(PaywallViewModel2::class.java)

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        IsPremium.is_premium = sharedPreferences.getBoolean("isPremium", false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RevenueCat SDK'nın yapılandırma ayarları
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                requireContext(),
                "goog_WpWrDQlWjEJFISieSKGdOxTBChB"
            ).build())

        // Teklifleri alma işlemi
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                /* Optional error handling */

                Log.e("revenucat","${error}")
            },
            onSuccess = { offerings ->
                // Display current offering with offerings.current
                val currentOffering = offerings.current
                currentOffering?.let { offering ->
                    val weeklyPackage = offering.availablePackages.find { it.packageType == PackageType.WEEKLY }
                    val yearlyPackage = offering.availablePackages.find { it.packageType == PackageType.ANNUAL }

                    weeklyPackage?.let { weekly ->
                        binding.monthlyPaywallText.text = weekly.product.price.formatted
                        selected_pacaked_weekly = weekly
                        selected_pacaked = selected_pacaked_weekly

                    }

                    yearlyPackage?.let { yearly ->
                        binding.paywallyearlytext.text = yearly.product.price.formatted
                        selected_pacaked_yearly=yearly
                    }
                }
            }
        )

        binding.montlyPaywallLayout.setOnClickListener {
            if (!click_Paywall){
                binding.yearlyLayoutPaywall.setBackgroundResource(0)
                binding.montlyPaywallLayout.setBackgroundResource(R.drawable.click_paywall_background)
                click_Paywall = true
                Log.e("girdi","girdi")
                selected_pacaked = selected_pacaked_weekly
                binding.buttonPaywall.text = getString(R.string.continue2)
            }
        }
        binding.yearlyLayoutPaywall.setOnClickListener {
            if (click_Paywall){
                binding.yearlyLayoutPaywall.setBackgroundResource(R.drawable.click_paywall_background)
                binding.montlyPaywallLayout.setBackgroundResource(0)
                click_Paywall = false
                selected_pacaked = selected_pacaked_yearly
                binding.buttonPaywall.text = getString(R.string.try_or_Free)
            }
        }
        binding.closePaywallFragment.setOnClickListener {
            // removeRecommendFragment fonksiyonunu çağırmadan önce parentFragment'in doğru şekilde cast edildiğinden emin olun
  
            // Diğer fragmentleri kapatmak için gerekirse burada çağırabilirsiniz
            val settingsFragment = parentFragment as? SettingsFragment
            settingsFragment?.removePaywallFragment()

            // Premium durumu ve onboarding işlemleri
            paywallviemodel.setBooleanValue(false)
            paywallviemodel2.setBooleanValue(false)

            if (!isOnBoardingFinished()) {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                onBoardingFinished()
            }
        }

        binding.buttonPaywall.setOnClickListener {
            if(selected_pacaked !=null)  {
                Purchases.sharedInstance.purchaseWith(
                    PurchaseParams.Builder(requireActivity(),selected_pacaked!!).build(),
                    onError = { error, userCancelled ->
                        // Handle error
                    },
                    onSuccess = { purchase: StoreTransaction?, customerInfo: CustomerInfo ->
                        IsPremium.is_premium = true
                        // Save premium status to shared preferences
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isPremium", true)
                        editor.apply()
                        // Optionally navigate to the main activity or update UI
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Try again later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onBoardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished", true) // onboarding kontrolü için false çevirdik false olduğunda onboarding her uygulama açıldığında çalışır !!
        editor.apply()
    }

    private fun isOnBoardingFinished(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }
}
