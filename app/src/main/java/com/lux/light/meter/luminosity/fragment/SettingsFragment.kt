package com.lux.light.meter.luminosity.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.databinding.FragmentSettingsBinding
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
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
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("recording", Context.MODE_PRIVATE)
        interstitialAdManager = InterstitialAdManager(this)

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        AutomaticRecording.init(requireContext())

        val dialogView = LayoutInflater.from(context).inflate(R.layout.calibration_radio_group, null)
        val builder = AlertDialog.Builder(requireContext())
        val button_close = dialogView.findViewById<ImageButton>(R.id.close_popup_radio)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        val dialogView2 = LayoutInflater.from(context).inflate(R.layout.unitsettings, null)
        val builder2 = AlertDialog.Builder(requireContext())
        val button_close2 = dialogView2.findViewById<ImageButton>(R.id.close_popup_radio_unit)
        val radioGroup2 = dialogView2.findViewById<RadioGroup>(R.id.radioGroup_settings)



        builder.setView(dialogView)
        builder2.setView(dialogView2)

        val alertDialog = builder.create()
        val alertDialog2 = builder2.create()


        // Check if calibration value is stored in SharedPreferences, if not use default value
        val savedCalibration = sharedPreferences.getFloat("calibration", 1f)
        Lightvalue.light_value_calibration = savedCalibration

        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
            Unit.unitsettings= savedUnit



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

        Log.e("degerboolen","${AutomaticRecording.automatic_recording}")

        binding.Callibrationlayout.setOnClickListener {
            alertDialog.show()
            val window = alertDialog.window
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window?.attributes)
            val width = resources.getDimensionPixelSize(R.dimen.dialog_width) // Bu boyutu dimens.xml dosyanızda tanımlamanız gerekiyor
            layoutParams.width = width
            window?.attributes = layoutParams

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radibutton1x -> {
                        setCalibrationAndSave(1f)
                    }
                    R.id.radiobutton_1_25x -> {
                        setCalibrationAndSave(1.25f)
                    }
                    R.id.radiobutton_1_5x -> {
                        setCalibrationAndSave(1.5f)
                    }
                    R.id.radiobutton1_75x -> {
                        setCalibrationAndSave(1.75f)
                    }
                    R.id.radiobutton2x -> {
                        setCalibrationAndSave(2f)
                    }
                }
            }



            // Şimdi de light_value_calibration değerine göre ilgili radio düğmesini işaretleyin
            when (Lightvalue.light_value_calibration) {
                1f -> radioGroup.check(R.id.radibutton1x)
                1.25f -> radioGroup.check(R.id.radiobutton_1_25x)
                1.5f -> radioGroup.check(R.id.radiobutton_1_5x)
                1.75f -> radioGroup.check(R.id.radiobutton1_75x)
                2f -> radioGroup.check(R.id.radiobutton2x)
            }


            button_close.setOnClickListener {
                alertDialog.dismiss()
                Addisplay.number_of_ad_impressions++
                showInterstitialAdOnClick()

            }



        }

        binding.constraintLayoutunitsettings.setOnClickListener {
            alertDialog2.show() // İkinci dialog gösteriliyor
            val window = alertDialog2.window
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window?.attributes)
            val width = resources.getDimensionPixelSize(R.dimen.dialog_width) // Bu boyutu dimens.xml dosyanızda tanımlamanız gerekiyor
            layoutParams.width = width
            window?.attributes = layoutParams
            radioGroup2.setOnCheckedChangeListener{group ,checkId->
                when(checkId){
                    R.id.radibutton_LUX ->{
                        setUnit(1f)
                    }
                    R.id.radiobutton_FC ->{
                        setUnit(0.0929f)
                    }

                }
            }
            when(Unit.unitsettings){
                1f ->radioGroup2.check(R.id.radibutton_LUX)
                0.0929f->radioGroup2.check(R.id.radiobutton_FC)
            }
            button_close2.setOnClickListener {
                alertDialog2.dismiss()
                Addisplay.number_of_ad_impressions++
                showInterstitialAdOnClick()
            }

        }


        binding.constraintLayoutPaywallSettings.setOnClickListener {
            replaceFragment(PaywallFragment())
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()

        }
        binding.constraintLayout22RestorePurchase.setOnClickListener {
            if(!IsPremium.is_premium){
                Toast.makeText(requireContext(),"Premium false",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(),"Premium true",Toast.LENGTH_SHORT).show()

            }
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()

        }

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(Executors.newSingleThreadExecutor(), OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result

                    // Ana iş parçacığında çalışacak şekilde işlemleri yönlendir
                    activity?.runOnUiThread {
                        // Uzak yapılandırma değerlerine eriş
                        val termsof_use = remoteConfig.getString("term_of_use")
                        val privacyPolicyText = remoteConfig.getString("privacy_policy")
                        val click_count_control = remoteConfig.getString("click_count_control")
                        val giv_us_point = remoteConfig.getString("giv_us_point")

                        val click = sharedPreferences.getInt("click", click_count_control.toInt())
                        ClickController.click_count_control = click
                        Log.e("click_count_control","${click}")



                        binding.privacyPolicy.setOnClickListener{
                            if (giv_us_point.isEmpty()) {
                                // Metin değeri boş ise
                                openUrlInBrowser("https://docs.google.com/document/d/e/2PACX-1vSDE879PGysWGu1RXoA4JIRYL_uszA5XsJklv4_951MM21J84mp7Tj_cQ16SljdmtuMmkYSzk0ToBH3/pub")
                            } else {
                                openUrlInBrowser(giv_us_point)
                            }
                        }

                        binding.privacyPolicy.setOnClickListener{
                            if (privacyPolicyText.isEmpty()) {
                                // Metin değeri boş ise
                                openUrlInBrowser("https://docs.google.com/document/d/e/2PACX-1vSDE879PGysWGu1RXoA4JIRYL_uszA5XsJklv4_951MM21J84mp7Tj_cQ16SljdmtuMmkYSzk0ToBH3/pub")
                            } else {
                                openUrlInBrowser(privacyPolicyText)
                            }
                        }

                        binding.termsofUse.setOnClickListener{
                            if (termsof_use.isEmpty()) {
                                // Metin değeri boş ise
                                openUrlInBrowser("https://www.apple.com/legal/internet-services/itunes/dev/stdeula/")
                            } else {
                                openUrlInBrowser(termsof_use)

                            }
                        }
                    }
                } else {
                }
            })


        return binding.root
    }

    private fun showInterstitialAdOnClick() {
        interstitialAdManager.loadInterstitialAd()
        interstitialAdManager.showInterstitialAdnotlinechart() // Tabloyu sıfırla
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
    private fun setUnit(settingsunit:Float) {
        Unit.unitsettings =settingsunit
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
            else ->
                "1x"
        }
    }
    private fun updateUnitText(){
        binding.unitText.text = when(Unit.unitsettings){
            1f -> "Lx"
            0.0929f -> "FC"
            else -> {
                "Lx"
            }
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
