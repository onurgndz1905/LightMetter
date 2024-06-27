package com.lux.light.meter.luminosity.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.databinding.FragmentSettingsBinding
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.AutomaticRecording
import com.lux.light.meter.luminosity.`object`.ClickController
import com.lux.light.meter.luminosity.`object`.IsPremium
import com.lux.light.meter.luminosity.`object`.Lightvalue
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.paywall.PaywallFragment
import java.util.concurrent.Executors

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var interstitialAdManager: InterstitialAdManager

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        interstitialAdManager = InterstitialAdManager(requireContext())

        if (IsPremium.is_premium){
            binding.buttonGoPremium.setBackgroundResource(R.drawable.bg_premium_true_asset)

            binding.visibltyText2.visibility = View.GONE
            binding.visibltyText3.visibility = View.GONE
            binding.textView45Premiumtext.text = getString(R.string.you_are_using_premium)

            // Linear gradient ayarlama
            val paint = binding.textView45Premiumtext.paint
            val width = paint.measureText(binding.textView45Premiumtext.text.toString())
            val textShader = LinearGradient(0f, 0f, width, binding.textView45Premiumtext.textSize,
                intArrayOf(
                    Color.parseColor("#C35F34"), // Başlangıç rengi (mor)
                    Color.parseColor("#FB923C"), // Ortanca rengi (mavi)
                    Color.parseColor("#F97316")  // Bitiş rengi (altın sarısı)
                ), null, Shader.TileMode.CLAMP)
            binding.textView45Premiumtext.paint.shader = textShader

        }
        else{
            binding.buttonGoPremium.setBackgroundResource(R.drawable.bg_premium_assey)

            binding.visibltyText2.visibility = View.VISIBLE
            binding.visibltyText3.visibility = View.VISIBLE
            binding.textView45Premiumtext.text = getString(R.string.experience)

            // Gradient ve arka planı kaldır
            binding.textView45Premiumtext.paint.shader = null
            binding.textView45Premiumtext.setBackgroundColor(Color.TRANSPARENT)
        }
        // Initialize AutomaticRecording based on SharedPreferences
        AutomaticRecording.init(requireContext())
        // Set switch state based on AutomaticRecording value
        binding.switchAutoRecording.isChecked = AutomaticRecording.automatic_recording

        updateCalibrationText()
        updateUnitText()

        binding.switchAutoRecording.setOnCheckedChangeListener { buttonView, isChecked ->
            // Update AutomaticRecording value based on switch state
            AutomaticRecording.automatic_recording = isChecked
            AutomaticRecording.saveAutomaticRecordingState() // Save the state to SharedPreferences
            Toast.makeText(requireContext(), "Automatic Recording: $isChecked", Toast.LENGTH_SHORT).show()
        }

        binding.Callibrationlayout.setOnClickListener {
            showCalibrationDialog()
        }

        binding.constraintLayoutunitsettings.setOnClickListener {
            showUnitSettingsDialog()
        }

        binding.constraintLayoutPaywallSettings.setOnClickListener {
            replaceFragment(PaywallFragment())
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
        }

        binding.buttonGoPremium.setOnClickListener {
            if (IsPremium.is_premium){
                Toast.makeText(requireContext(), "Premium true", Toast.LENGTH_SHORT).show()
                Addisplay.number_of_ad_impressions++
                showInterstitialAdOnClick()
            }
            else{
                replaceFragment(PaywallFragment())

            }

        }

        binding.constraintLayout22RestorePurchase.setOnClickListener {
            if (!IsPremium.is_premium) {
                Toast.makeText(requireContext(), "Premium false", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Premium true", Toast.LENGTH_SHORT).show()
            }
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
        }

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(Executors.newSingleThreadExecutor(), OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result

                    // Main thread operations to access remote configuration values
                    activity?.runOnUiThread {
                        val termsof_use = remoteConfig.getString("term_of_use")
                        val privacyPolicyText = remoteConfig.getString("privacy_policy")
                        val click_count_control = remoteConfig.getString("click_count_control")
                        val giv_us_point = remoteConfig.getString("we_give_points")

                        val click = sharedPreferences.getInt("click", click_count_control.toInt())
                        ClickController.click_count_control = click
                        Log.e("click_count_control", "$click")

                        binding.giveUsPoint.setOnClickListener {
                            if (giv_us_point.isEmpty()) {
                                openUrlInBrowser("https://play.google.com/store/apps/details?id=com.lux.light.meter.luminosity")
                            } else {
                                openUrlInBrowser(giv_us_point)
                            }
                        }

                        binding.privacyPolicy.setOnClickListener {
                            if (privacyPolicyText.isEmpty()) {
                                openUrlInBrowser("https://docs.google.com/document/d/e/2PACX-1vSDE879PGysWGu1RXoA4JIRYL_uszA5XsJklv4_951MM21J84mp7Tj_cQ16SljdmtuMmkYSzk0ToBH3/pub")
                            } else {
                                openUrlInBrowser(privacyPolicyText)
                            }
                        }

                        binding.termsofUse.setOnClickListener {
                            if (termsof_use.isEmpty()) {
                                openUrlInBrowser("https://www.apple.com/legal/internet-services/itunes/dev/stdeula/")
                            } else {
                                openUrlInBrowser(termsof_use)
                            }
                        }
                    }
                } else {
                    // Handle failure
                }
            })

        return binding.root
    }

    private fun showCalibrationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.calibration_radio_group, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
        val buttonClose = dialogView.findViewById<ImageButton>(R.id.close_popup_radio)

        // Setup AlertDialog builder
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)

        // Create AlertDialog
        val alertDialog = builder.create()

        // Set dialog properties
        alertDialog.setOnShowListener {
            val window = alertDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = layoutParams
        }

        // Set radio button listeners and initial state based on saved values
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radibutton1x -> setCalibrationAndSave(1f)
                R.id.radiobutton_1_25x -> setCalibrationAndSave(1.25f)
                R.id.radiobutton_1_5x -> setCalibrationAndSave(1.5f)
                R.id.radiobutton1_75x -> setCalibrationAndSave(1.75f)
                R.id.radiobutton2x -> setCalibrationAndSave(2f)
            }
            alertDialog.dismiss()
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
        }

        // Initialize radio button based on current calibration value
        when (Lightvalue.light_value_calibration) {
            1f -> radioGroup.check(R.id.radibutton1x)
            1.25f -> radioGroup.check(R.id.radiobutton_1_25x)
            1.5f -> radioGroup.check(R.id.radiobutton_1_5x)
            1.75f -> radioGroup.check(R.id.radiobutton1_75x)
            2f -> radioGroup.check(R.id.radiobutton2x)
        }

        // Close button listener
        buttonClose.setOnClickListener {
            alertDialog.dismiss()
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
        }

        alertDialog.show()
    }

    private fun showUnitSettingsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.unitsettings, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup_settings)
        val buttonClose = dialogView.findViewById<ImageButton>(R.id.close_popup_radio_unit)


        // Setup AlertDialog builder
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)

        // Create AlertDialog
        val alertDialog = builder.create()

        // Set dialog properties
        alertDialog.setOnShowListener {
            val window = alertDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = layoutParams
        }

        // Set radio button listeners and initial state based on saved values
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radibutton_LUX -> {
                    setUnit(1f)
                }
                R.id.radiobutton_FC -> {
                    if (IsPremium.is_premium) {
                        setUnit(0.0929f)

                    } else {
                        radioGroup.clearCheck()
                        radioGroup.check(R.id.radibutton_LUX)
                        Toast.makeText(requireContext(), "Premium feature. Please upgrade to access.", Toast.LENGTH_SHORT).show()
                        replaceFragment(PaywallFragment())
                        alertDialog.dismiss()
                    }
                }
            }
            alertDialog.dismiss()
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
        }

        // Initialize radio button based on current unit settings value
        when (Unit.unitsettings) {
            1f -> {
                radioGroup.check(R.id.radibutton_LUX)

            }
            0.0929f -> {
                radioGroup.check(R.id.radiobutton_FC)

            }
        }

        // Close button listener
        buttonClose.setOnClickListener {
            alertDialog.dismiss()
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
        }

        alertDialog.show()
    }

    private fun showInterstitialAdOnClick() {
        if (Addisplay.number_of_ad_impressions%3 ==1){
            interstitialAdManager.loadInterstitialAd()

        }


        interstitialAdManager.showInterstitialAd() // Tabloyu sıfırla
    }

    private fun openUrlInBrowser(url: String) {
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "URL is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCalibrationAndSave(calibrationValue: Float) {
        Lightvalue.light_value_calibration = calibrationValue
        sharedPreferences.edit().putFloat("calibration", calibrationValue).apply()
        updateCalibrationText()
    }

    private fun setUnit(settingsunit: Float) {
        Unit.unitsettings = settingsunit
        sharedPreferences.edit().putFloat("unit_settings", settingsunit).apply()
        updateUnitText()
    }

    private fun updateCalibrationText() {
        binding.calibrationSpeed.text = when (Lightvalue.light_value_calibration) {
            1f -> "1x"
            1.25f -> "1.25x"
            1.5f -> "1.5x"
            1.75f -> "1.75x"
            2f -> "2x"
            else -> "1x"
        }
    }

    private fun updateUnitText() {
        binding.unitText.text = when (Unit.unitsettings) {
            1f -> "Lx"
            0.0929f -> "FC"
            else -> "Lx"
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.full_screen_fragment_settings, fragment)
            .commit()
    }

    fun removePaywallFragment() {
        val fragment = childFragmentManager.findFragmentById(R.id.full_screen_fragment_settings)
        if (fragment != null && fragment is PaywallFragment) {
            childFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }
}
