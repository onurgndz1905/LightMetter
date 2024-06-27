package com.lux.light.meter.luminosity.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.textfield.TextInputEditText
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.data.LightData
import com.lux.light.meter.luminosity.databinding.FragmentRecommendedLightBinding
import com.lux.light.meter.luminosity.history.DateTimeUtil
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
import com.lux.light.meter.luminosity.`object`.CurrentIndex
import com.lux.light.meter.luminosity.`object`.IsPremium
import com.lux.light.meter.luminosity.`object`.Lightvalue
import com.lux.light.meter.luminosity.`object`.Lightvalue.lightValue
import com.lux.light.meter.luminosity.`object`.RecommendationName
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.paywall.PaywallFragment
import com.lux.light.meter.luminosity.viewmodel.LightDataViewModel
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel2
import com.lux.light.meter.luminosity.viewmodel.SensorDataViewModel
import java.util.*
import kotlin.concurrent.timerTask

class RecommendedLightFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var chart: LineChart? = null
    private var dataSet: LineDataSet? = null
    private var entries = mutableListOf<Entry>()
    private var startTime = 0L
    private val interval = 10000L // 10 saniye
    private var timer: Timer? = null
    private var isMeasurementRunning = true
    private var minLightValue = Float.MAX_VALUE
    private var maxLightValue = Float.MIN_VALUE
    private var sumLightValue = 0f
    private var numMeasurements = 0
    private lateinit var sensorDataViewModel: SensorDataViewModel
    private lateinit var lightDataViewModel: LightDataViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var interstitialAdManager: InterstitialAdManager
    private lateinit var paywallViewModel: PaywallViewModel2

    private lateinit var binding: FragmentRecommendedLightBinding


    private val fragmentList = listOf(
        CustomProgress1Fragment(),
        CustomProgress2Fragment(),
        CustomProgress3Fragment(),
        CustomProgress4Fragment(),
        CustomProgress5Fragment()

    )
    private var currentFragmentIndex = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecommendedLightBinding.inflate(inflater, container, false)
        chart = binding.lineChartRecommended // chart bileşenini bağlayın
        interstitialAdManager = InterstitialAdManager(requireContext())
        initializeChart() // Grafik bileşenlerini başlatın
        sensorDataViewModel = ViewModelProvider(requireActivity()).get(SensorDataViewModel::class.java)
        sensorManager =
            requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor == null) {
            Log.e("sensormanager", "light sensor not found")
        }
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // shared preferences başlatılıyor
        paywallViewModel = ViewModelProvider(requireActivity()).get(PaywallViewModel2::class.java)
        paywallViewModel.booleanLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { newValue ->
            Log.e("paywalviewmodelll","${newValue}")
            if (newValue) {
                // Paywall gösterilsin
                replacepaywalFragment(PaywallFragment())
                binding.recommendIn.visibility = View.VISIBLE // LightScreenIn'ı gizle
                binding.recommendInIn.visibility = View.GONE

            }
            else if(!newValue){
                binding.recommendIn.visibility = View.GONE // LightScreenIn'ı göster
                binding.recommendInIn.visibility = View.VISIBLE

            }
            else {
                binding.recommendIn.visibility = View.GONE // LightScreenIn'ı göster
                binding.recommendInIn.visibility = View.VISIBLE

            }
        })
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lightDataViewModel =
            ViewModelProvider(this).get(LightDataViewModel::class.java)

        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings= savedUnit
        binding.titleRecommended.text = RecommendationName.recommendation_name
        binding.buttonsaveRecommended.setOnClickListener {
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
            showInputDialog()
        }

        binding.imageButtonNext.setOnClickListener {
            if (currentFragmentIndex < fragmentList.size - 1) {
                currentFragmentIndex++
                if (currentFragmentIndex >=2 && !IsPremium.is_premium ){
                    replaceFragment(BlurIspremiumFragment2())
                } else {
                    replaceFragment(fragmentList[currentFragmentIndex])
                }
                if (currentFragmentIndex == 4) {
                }
                // Değişikliği objeye kaydediyoruz
                CurrentIndex.setCurrentIndex(requireContext(), currentFragmentIndex)
            }
            Addisplay.number_of_ad_impressions ++
            showInterstitialAdOnClick()


        }

        binding.imageButtonBack.setOnClickListener {

            if (currentFragmentIndex > 0) {
                currentFragmentIndex--
                Log.e("FRAGMENT","${currentFragmentIndex}")
                if (currentFragmentIndex >= 2 && !IsPremium.is_premium) {
                    replaceFragment(BlurIspremiumFragment2())
                } else {
                    replaceFragment(fragmentList[currentFragmentIndex])
                }

                if (currentFragmentIndex <= 0) {
                }
                // Değişikliği objeye kaydediyoruz
                CurrentIndex.setCurrentIndex(requireContext(), currentFragmentIndex)
            }
            Addisplay.number_of_ad_impressions ++
            showInterstitialAdOnClick()
        }
        replaceFragment(fragmentList[currentFragmentIndex])


        binding.imageButtonBackRecommendFragment.setOnClickListener {
            (parentFragment as? RecommendFragment)?.removeRecommendFragment()

        }



    }
    private fun replacepaywalFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.recommend_in, fragment)
            .commit()
    }

    private fun showInputDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.input_dialog, null)
        val input = dialogView.findViewById<TextInputEditText>(R.id.input_name)
        val buttonSave = dialogView.findViewById<TextView>(R.id.save_input_text)
        val buttonCancel = dialogView.findViewById<TextView>(R.id.cancel_text_input)

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            input.imeOptions = EditorInfo.IME_ACTION_DONE
            input.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
                    input.clearFocus()
                    true
                } else {
                    false
                }
            }
        }

        buttonSave.setOnClickListener {
            val userInput = input.text.toString()
            if (userInput.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter text", Toast.LENGTH_SHORT).show()
            } else {
                val newId = System.currentTimeMillis()

                // Klavyeyi kapatma işlemi
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
                input.clearFocus()

                saveInputToSharedPreferences(userInput, newId)

                if (!IsPremium.is_premium) {
                    showUpgradeOrWatchAdDialog(newId)
                } else {
                    saveLightData(newId)
                }
                dialog.dismiss()
            }
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }




    private fun saveInputToSharedPreferences(input: String, id: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("shared_$id", input)
        editor.apply()
    }


    @SuppressLint("MissingInflatedId")
    private fun showUpgradeOrWatchAdDialog(id: Long) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.input_premium_popup, null)
        val buttonWatchAd = dialogView.findViewById<Button>(R.id.watch_ad_button)
        val buttonUpgrade = dialogView.findViewById<Button>(R.id.uprage_text_popup)
        val button_close_popup = dialogView.findViewById<ImageButton>(R.id.close_popup_input)

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        buttonWatchAd.setOnClickListener {
            showAd(id)
            dialog.dismiss()
        }

        buttonUpgrade.setOnClickListener {
            paywallViewModel.setBooleanValue(true)
            dialog.dismiss()
        }

        button_close_popup.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAd(id: Long) {
        interstitialAdManager.loadInterstitialAd()
        interstitialAdManager.showInterstitialAdWithCallback(
            onSuccess = {
                saveLightData(id) // Save data only if ad was successfully shown
            },
            onFailure = {
                Toast.makeText(requireContext(),getString(R.string.the_Ad_failed),Toast.LENGTH_SHORT).show()
            }
        )
    }
    private fun saveLightData(id: Long) {
        val dateTime = DateTimeUtil.getCurrentDateTime()

        val lightData = LightData(
            id = id,
            minLightValue = minLightValue,
            maxLightValue = maxLightValue,
            avgLightValue = if (numMeasurements != 0) sumLightValue / numMeasurements else 0.0f,
            timestamp = System.currentTimeMillis(), // Opsiyonel, zaman damgası eklemek istemiyorsanız burayı değiştirin
            recordingDate = dateTime
        )
        lightDataViewModel.insert(lightData)
        Toast.makeText(requireContext(), getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
        Addisplay.number_of_ad_impressions++
        showInterstitialAdOnClick()
    }

    private fun startMeasurement() {
        startTime = System.currentTimeMillis()
        sensorManager.registerListener(
            this,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun stopMeasurement() {
        sensorManager.unregisterListener(this)
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container_recommended_light, fragment)
            .commit()
    }

    private fun initializeChart() {
        chart?.description?.isEnabled = false
        chart?.setTouchEnabled(true)
        chart?.isDragEnabled = true
        chart?.setScaleEnabled(true)
        chart?.setDrawGridBackground(false)
        chart?.extraBottomOffset = 10f

        val xAxis = chart?.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.setDrawGridLines(false) // Dikey ızgara çizgilerini kapat
        xAxis?.textColor = Color.WHITE // X eksenindeki yazıların rengi
        xAxis?.axisMaximum = 120f
        xAxis?.granularity = 10f // 1 birim aralıklar

        // Geçen süreyi 10 saniye aralıklarla yazdırmak için özel bir biçimlendirici oluşturun
        xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() % 10 == 0) {
                    "${(value * interval).toInt() / 10000} s"
                } else {
                    ""
                }
            }
        }

        // Tablonun solunda değerleri göstermek için özellikleri ayarlayalım
        val leftAxis = chart?.axisLeft
        leftAxis?.setDrawGridLines(true) // Y eksenindeki ızgara çizgilerini etkinleştir
        leftAxis?.enableGridDashedLine(60f, 30f, 30f)
        leftAxis?.gridLineWidth = 0.5f
        leftAxis?.gridColor = Color.rgb(55, 59, 84)
        leftAxis?.textColor = Color.WHITE // Sol y eksenindeki yazıların rengi
        leftAxis?.granularity = 50f // 50 birim aralıklarla
        leftAxis?.axisMinimum = 0f // Minimum değeri 0 olarak ayarla
        leftAxis?.axisMaximum = 500f // Maksimum değeri 500 olarak ayarla

        val rightAxis = chart?.axisRight
        rightAxis?.isEnabled = false

        val legend = chart?.legend
        legend?.isEnabled = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Işık sensörü doğruluk değişiklikleriyle ilgilenmiyoruz
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_LIGHT) {
                val elapsedTime = (System.currentTimeMillis() - startTime) / 1000
                val lightValue = it.values[0].toInt() * Lightvalue.light_value_calibration *Unit.unitsettings
                sensorDataViewModel.updateSensorData(lightValue.toFloat())

                minLightValue = minOf(minLightValue, lightValue.toFloat())
                maxLightValue = maxOf(maxLightValue, lightValue.toFloat())
                sumLightValue += lightValue
                numMeasurements++

                if (lightValue > 500) {
                    val leftAxis = chart?.axisLeft
                    leftAxis?.axisMaximum = lightValue + 100f
                }

                entries.add(Entry(elapsedTime.toFloat(), lightValue.toInt().toFloat()))
                updateChart()
                Lightvalue.lightValue = lightValue.toFloat()
                binding.textView19.text ="${lightValue.toInt()} ${
                    when (Unit.unitsettings) {
                        0.0929f -> {
                            "FC"
                        }
                        1f -> {
                            "Lux"

                        }
                        else -> {
                            "Lux"
                        }
                    }
                }"

                val (conclusionText, textColor) = evaluateConclusion(RecommendationName.recommendation_name,lightValue)
                binding.textViewConclusion.text = conclusionText
                binding.textViewConclusion.setTextColor(textColor)




            }
        }
    }

    fun evaluateConclusion(name: String, lightValue: Float): Pair<String, Int> {
        var conclusionText = ""
        var textColor = Color.BLACK // Varsayılan renk

        when (name) {
           getString(R.string.study_desk) -> {
                when (lightValue.toFloat()) {
                    in 220*Unit.unitsettings..349*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 351*Unit.unitsettings..499*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            // Diğer durumlar buraya eklenebilir
             getString(R.string.living_room) -> {
                 when (lightValue.toFloat()) {
                     in 200*Unit.unitsettings..300*Unit.unitsettings -> {
                         conclusionText = getString(R.string.light_good_now)
                         textColor = Color.GREEN
                     }
                     in 301*Unit.unitsettings..500*Unit.unitsettings -> {
                         conclusionText = getString(R.string.light_normal)
                         textColor = Color.YELLOW
                     }
                     else -> {
                         conclusionText = getString(R.string.light_bad)
                         textColor = Color.RED
                     }
                 }
             }
            getString(R.string.study_desk) + " 2" ->{
                when (lightValue.toFloat()) {
                    in 300*Unit.unitsettings..450*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 451*Unit.unitsettings..700*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }

            getString(R.string.library) ->{
                when (lightValue.toFloat()) {
                    in 300*Unit.unitsettings..450*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 451*Unit.unitsettings..750*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.library) + " 2" ->{
                when (lightValue) {
                    in 300*Unit.unitsettings..450*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 451*Unit.unitsettings..750*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.hotel) ->{
                when (lightValue) {
                    in 250*Unit.unitsettings..300*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 301*Unit.unitsettings..400*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.bathroom) ->{
                when (lightValue) {
                    in 100*Unit.unitsettings..150*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 151*Unit.unitsettings..250*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.kitchen) ->{
                when (lightValue) {
                    in 300*Unit.unitsettings..400*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 401*Unit.unitsettings..500*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.cafetaria) ->{
                when (lightValue) {
                    in 200*Unit.unitsettings..350*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 351*Unit.unitsettings..500*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.locker_room) ->{
                when (lightValue) {
                    in 150*Unit.unitsettings..200*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 201*Unit.unitsettings..300*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.dinning_room) ->{
                when (lightValue) {
                    in 150*Unit.unitsettings..300*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 301*Unit.unitsettings..500*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.classroom) ->{
                when (lightValue) {
                    in 300*Unit.unitsettings..450*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 451*Unit.unitsettings..750*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            getString(R.string.gym) ->{
                when (lightValue) {
                    in 200*Unit.unitsettings..320*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_good_now)
                        textColor = Color.GREEN
                    }
                    in 321*Unit.unitsettings..500*Unit.unitsettings -> {
                        conclusionText = getString(R.string.light_normal)
                        textColor = Color.YELLOW
                    }
                    else -> {
                        conclusionText = getString(R.string.light_bad)
                        textColor = Color.RED
                    }
                }
            }
            // Diğer durumlar için koşulları burada tanımlayın
            else -> {
                conclusionText = getString(R.string.light_normal)
                textColor = Color.BLACK
            }
        }

        return Pair(conclusionText, textColor)
    }


    private fun updateChart() {
        if (chart?.data != null) {
            val lineData = chart?.data as LineData
            dataSet = lineData.getDataSetByIndex(0) as LineDataSet
            dataSet?.values = entries
            lineData.notifyDataChanged()
            chart?.notifyDataSetChanged()
            chart?.invalidate()
        } else {
            dataSet = LineDataSet(entries, "Işık Değeri")
            dataSet?.color = Color.parseColor("#EA580C")
            dataSet?.valueTextSize = 9f
            dataSet?.lineWidth = 2f
            dataSet?.mode = LineDataSet.Mode.LINEAR
            dataSet?.cubicIntensity = 0.4f
            dataSet?.setDrawCircles(false)
            dataSet?.circleColors = listOf(Color.YELLOW)
            dataSet?.valueTextColor = Color.TRANSPARENT

            // Degrade dolguyu ayarla
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient)
            dataSet?.setDrawFilled(true)
            dataSet?.fillDrawable = drawable

            val lineData = LineData(dataSet)
            chart?.data = lineData
        }
    }

    private fun showInterstitialAdOnClick() {
        if (Addisplay.number_of_ad_impressions%3 ==1){
            interstitialAdManager.loadInterstitialAd()

        }


        interstitialAdManager.showInterstitialAd() // Tabloyu sıfırla


    }

    override fun onResume() {
        super.onResume()
        startMeasurement()
    }

    override fun onPause() {
        super.onPause()
        stopMeasurement()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
    }

}
