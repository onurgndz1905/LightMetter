package com.lux.light.meter.luminosity.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.data.LightData
import com.lux.light.meter.luminosity.databinding.FragmentLightmeterBinding
import com.lux.light.meter.luminosity.history.DateTimeUtil
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
import com.lux.light.meter.luminosity.`object`.ClickController
import com.lux.light.meter.luminosity.`object`.CurrentIndex
import com.lux.light.meter.luminosity.`object`.IsPremium
import com.lux.light.meter.luminosity.`object`.Lightvalue
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.paywall.PaywallFragment
import com.lux.light.meter.luminosity.viewmodel.LightDataViewModel
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel2
import com.lux.light.meter.luminosity.viewmodel.SensorAvgViewModel
import com.lux.light.meter.luminosity.viewmodel.SensorDataViewModel
import com.lux.light.meter.luminosity.viewmodel.SensorMaxDataViewModel
import com.lux.light.meter.luminosity.viewmodel.SensorMinDataViewModel
import java.util.Timer
import kotlin.concurrent.timerTask


class LightmeterFragment : Fragment(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private lateinit var binding: FragmentLightmeterBinding
    private var chart: LineChart? = null
    private var dataSet: LineDataSet? = null
    private var entries = mutableListOf<Entry>()
    private var startTime = 0L
    private val interval = 10000L // 10 saniye
    private var timer: Timer? = null
    private var minLightValue = Float.MAX_VALUE
    private var maxLightValue = Float.MIN_VALUE
    private var sumLightValue = 0f
    private var numMeasurements = 0
    private lateinit var sensorDataViewModel: SensorDataViewModel
    private lateinit var lightDataViewModel: LightDataViewModel
    private lateinit var sensorMinDataViewModel: SensorMinDataViewModel
    private lateinit var sensorMaxDataViewModel: SensorMaxDataViewModel
    private lateinit var sensorAvgViewModel: SensorAvgViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var interstitialAdManager: InterstitialAdManager
    private lateinit var paywallViewModel: PaywallViewModel
    private lateinit var paywallviemodel2: PaywallViewModel2

    private val fragmentList = listOf(
        CustomProgress1Fragment(),
        CustomProgress2Fragment(),
        CustomProgress3Fragment(),
        CustomProgress4Fragment(),
        CustomProgress5Fragment(),

    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLightmeterBinding.inflate(inflater, container, false)
        // Düğmeye tıklama olayını burada bağladığınızdan emin olun
        binding.buttonstartstop.setOnClickListener {
            Log.d("ButtonClicked", "Button clickesad")

            // İşlemler burada olacak
        }
        interstitialAdManager = InterstitialAdManager(requireContext())
        interstitialAdManager.setBinding(binding)
        Unit.paywallclick =true

        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance( context ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( context ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
        })


        paywallviemodel2 = ViewModelProvider(requireActivity()).get(PaywallViewModel2::class.java)


        // Yanıp sönen noktayı oluştur
        val dotAnimator = ObjectAnimator.ofFloat(binding.textanimation, "alpha", 0f, 1f)
        dotAnimator.duration = 500 // Yanıp sönme süresi (milisaniye cinsinden)
        dotAnimator.repeatCount = ObjectAnimator.INFINITE // Sonsuz tekrar
        dotAnimator.repeatMode = ObjectAnimator.REVERSE // Tekrar modu: ters yönde tekrar et

        // Animasyonu başlat
        dotAnimator.start()
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedCalibration = sharedPreferences.getFloat("calibration", 1f)
        Lightvalue.light_value_calibration = savedCalibration


        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings= savedUnit


        chart = binding.lineChart
        initializeChart()
        sensorDataViewModel = ViewModelProvider(requireActivity()).get(SensorDataViewModel::class.java)
        sensorMinDataViewModel = ViewModelProvider(requireActivity()).get(SensorMinDataViewModel::class.java)
        sensorMaxDataViewModel = ViewModelProvider(requireActivity()).get(SensorMaxDataViewModel::class.java)
        sensorAvgViewModel = ViewModelProvider(requireActivity()).get(SensorAvgViewModel::class.java)




        binding.buttonstartstop.setOnClickListener {
            Log.d("ButtonClicked", "Button clicked")

            if (Unit.isMeasurementRunning) {
                stopMeasurement()
                binding.buttonstartstop.text = getString(R.string.start_test)
                dotAnimator.cancel() // Yanıp sönen animasyonu durdur
                Log.d("ButtonClicked", "Measurement stopped")
            } else {
                startMeasurement()
                binding.buttonstartstop.text = getString(R.string.Pause_test)
                dotAnimator.start() // Yanıp sönen animasyonu başlat
                Log.d("ButtonClicked", "Measurement started")
            }

            Unit.isMeasurementRunning = !Unit.isMeasurementRunning // Durumu tersine çevir
            Log.d("ButtonClicked", "isMeasurementRunning: ${Unit.isMeasurementRunning}")
        }


// Kullanıcı etkileşiminden sonra marquee'nin çalıştığından emin olun
        binding.buttonRestart.setOnClickListener {
            restartMeasurement()
            Addisplay.number_of_ad_impressions++
            showInterstitialAdOnClick()
        }





        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor == null) {
            Log.e("sensormanager","light sensor not found")
        }

         var currentFragmentIndex = CurrentIndex.getCurrentIndex(requireContext())
        currentFragmentIndex = 0

        binding.imageButtonNext.setOnClickListener {
            if (currentFragmentIndex < fragmentList.size - 1) {
                currentFragmentIndex++
                Log.e("FRAGMENT","${currentFragmentIndex}")
                if (currentFragmentIndex >=2 && !IsPremium.is_premium ){
                    replaceFragment(BlurIspremiumFragment())
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
                    replaceFragment(BlurIspremiumFragment())
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




        paywallViewModel = ViewModelProvider(requireActivity()).get(PaywallViewModel::class.java)


        paywallViewModel.booleanLiveData.observe(viewLifecycleOwner, Observer { newValue ->
            if (newValue) {
                // Paywall gösterilsin
                replacepaywalFragment(PaywallFragment())
                binding.fullscrennn.visibility = View.VISIBLE // LightScreenIn'ı gizle
               binding.lightScreenIn.visibility = View.GONE
            } else {
                // Paywall gizlensin
                binding.fullscrennn.visibility = View.GONE // LightScreenIn'ı göster
                binding.lightScreenIn.visibility = View.VISIBLE

            }
        })




        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startUpdatingDateTime()

        lightDataViewModel = ViewModelProvider(this).get(LightDataViewModel::class.java)
        binding.SaveButton.setOnClickListener {
            if (!IsPremium.is_premium){
                paywallViewModel.setBooleanValue(true)

            }
            else{
                val dateTime = DateTimeUtil.getCurrentDateTime()

                val lightData = LightData(
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


        }


    }
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
    private fun replacepaywalFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fullscrennn, fragment)
            .commit()
    }


    private fun startMeasurement() {
        startTime = System.currentTimeMillis()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        restartMeasurement()
    }

    private fun stopMeasurement() {
        sensorManager.unregisterListener(this)
    }


        //reklam Interstitial

    private fun showInterstitialAdOnClick() {
        interstitialAdManager.loadInterstitialAd()
        interstitialAdManager.showInterstitialAd() // Tabloyu sıfırla
        if (Advert.advert){
            restartMeasurement()
        }
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
                // Değerin 10'un katı olup olmadığını kontrol edin
                return if (value.toInt() % 10 == 0) {
                    // Değer 10'un katı ise geçen süreyi yazdırın
                    "${(value * interval).toInt() / 10000} s"
                } else {
                    // Değer 10'un katı değilse boş bir dize döndürün
                    ""
                }
            }
        }

        // Tablonun solunda değerleri göstermek için özellikleri ayarlayalım
        val leftAxis = chart?.axisLeft
        leftAxis?.setDrawGridLines(true) // Y eksenindeki ızgara çizgilerini etkinleştir
        leftAxis?.enableGridDashedLine(60f,30f,30f)
        leftAxis?.gridLineWidth =0.5f
        leftAxis?.gridColor = Color.rgb(55,59,84)
        leftAxis?.textColor = Color.WHITE // Sol y eksenindeki yazıların rengi
        leftAxis?.granularity = 50f // 50 birim aralıklarla
        leftAxis?.axisMinimum = 0f // Minimum değeri 0 olarak ayarla
        leftAxis?.axisMaximum = 500f // Maksimum değeri 500 olarak ayarla


        val rightAxis = chart?.axisRight
        rightAxis?.isEnabled = false

        val legend = chart?.legend
        legend?.isEnabled = false
    }



    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()
        lightSensor?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Işık sensörü doğruluk değişiklikleriyle ilgilenmiyoruz
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val sharedPreferences = context?.getSharedPreferences("recording", Context.MODE_PRIVATE)
        val automaticRecordingEnabled = sharedPreferences?.getBoolean("pref_automatic_recording", true)


        if (automaticRecordingEnabled == true){
            event?.let {
                if (it.sensor.type == Sensor.TYPE_LIGHT) {
                    val elapsedTime = (System.currentTimeMillis() - startTime) / 1000 // saniye cinsinden geçen süre
                    val lightValue = it.values[0].toInt() * Lightvalue.light_value_calibration*Unit.unitsettings
                    sensorDataViewModel.updateSensorData(lightValue.toFloat())
                    sensorMinDataViewModel.updateSensorData(minLightValue)
                    sensorMaxDataViewModel.updateSensorData(maxLightValue)
                    sensorAvgViewModel.updateSensorData(sumLightValue/numMeasurements)

                    binding.textviewTime.text = String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60)
                    // Minimum ve maksimum değerleri güncelle
                    minLightValue = minOf(minLightValue, lightValue.toFloat())
                    maxLightValue = maxOf(maxLightValue, lightValue.toFloat())
                    sumLightValue += lightValue
                    numMeasurements++


                    // Minimum, maksimum ve ortalama değerleri metin alanlarına ata
                    binding.textViewMN.text = String.format("%.2f", minLightValue)
                    binding.TextviewMax.text = String.format("%.2f", maxLightValue)
                    binding.textViewAVG.text = String.format("%.2f", sumLightValue / numMeasurements)

                    // Check if light value exceeds 500
                    if (lightValue > 500) {
                        // Update maximum value of the Y-axis
                        val leftAxis = chart?.axisLeft
                        leftAxis?.axisMaximum = lightValue + 100f // Increase maximum by 100 or adjust as needed
                    }

                    // Veriyi ekleyin
                    entries.add(Entry(elapsedTime.toFloat(), lightValue.toInt().toFloat()))


                    // Dataset'i güncelle
                    updateChart()

                    // Güncel ışık değerini Lightvalue objesine atayın
                    Lightvalue.lightValue = lightValue.toFloat()
                }
            }
        }
        else{
          //  binding.buttonstartstop.text = getString(R.string.start_test)
        }

    }
    private fun restartMeasurement() {
        // Ölçüm değerlerini sıfırla
        minLightValue = Float.MAX_VALUE
        maxLightValue = Float.MIN_VALUE
        sumLightValue = 0f
        numMeasurements = 0
        // Ölçüm başlangıç zamanını güncelle
        startTime = System.currentTimeMillis()
        // Veri girişlerini temizle
        entries.clear()
        // Grafik güncelle
        updateChart()
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
            dataSet?.setDrawCircles(false) // Yuvarlakların çizilmesi
            dataSet?.circleColors = listOf(Color.YELLOW) // Yuvarlağın rengini değiştirme
            dataSet?.valueTextColor = Color.TRANSPARENT // Veri metninin rengini sıfıra ayarla

            // Degrade dolguyu ayarla
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient)
            dataSet?.setDrawFilled(true)
            dataSet?.fillDrawable = drawable

            val lineData = LineData(dataSet)
            chart?.data = lineData
        }
    }

    private fun startUpdatingDateTime() {
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            activity?.runOnUiThread {
                val dateTime = DateTimeUtil.getCurrentDateTime()

                binding.textView7.text = dateTime

            }
        }, 0, 1000) // Her saniyede bir güncelleme yap
    }




    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
    }

}
