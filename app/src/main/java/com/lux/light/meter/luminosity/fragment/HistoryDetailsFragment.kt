package com.lux.light.meter.luminosity.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.data.LightData
import com.lux.light.meter.luminosity.databinding.FragmentHistoryDetailsBinding
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.`object`.Advert
import com.lux.light.meter.luminosity.viewmodel.LightDataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryDetailsFragment : Fragment() {
    private lateinit var lightDataViewModel: LightDataViewModel
    private lateinit var interstitialAdManager: InterstitialAdManager

    companion object {
        private const val ARG_LIGHT_DATA = "light_data"

        fun newInstance(lightData: LightData): HistoryDetailsFragment {
            val fragment = HistoryDetailsFragment()
            val args = Bundle()
            args.putSerializable(ARG_LIGHT_DATA, lightData)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: FragmentHistoryDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryDetailsBinding.inflate(inflater, container, false)
        val lightData = arguments?.getSerializable(ARG_LIGHT_DATA) as? LightData
        interstitialAdManager = InterstitialAdManager(requireContext())


        lightDataViewModel = ViewModelProvider(this).get(LightDataViewModel::class.java)
        binding.historyDetailsViewFull.visibility = View.VISIBLE

        lightData?.let {
            binding?.historyDetailsTestname?.text ="Test" + " ${it.id}"
            val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.timestamp))

            binding?.historyDetailsAvglight?.text = it.avgLightValue.toString()
            binding?.historyDetailsDate?.text = it.recordingDate
            binding?.historyDetailsMaxlightt?.text = it.maxLightValue.toString()
            binding?.historyDetailsMainlight?.text = it.minLightValue.toString()
            binding?.historyDetailsDuration?.text =  timestamp
        }
        binding.shareHistoryItem.setOnClickListener {
            lightData?.let {
                val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lightData.timestamp))
                val message = "Light Duration: $timestamp\n" +
                        "Min Light Value: ${it.minLightValue}\n" +
                        "Max Light Value: ${it.maxLightValue}\n" +
                        "Average Light Value: ${it.avgLightValue}\n" +
                        "Recording Date: ${it.recordingDate}"

                shareText(message,requireContext())
            }

        }
        binding.imageButtonBackDetails.setOnClickListener {
            binding.historyDetailsViewFull.visibility = View.GONE

        }
        binding.buttonDeleteHistoryItem.setOnClickListener {
            // Silme butonu tıklamasını ele al
            val dialogView = LayoutInflater.from(context).inflate(R.layout.item_delete_popup, null)
            val builder = AlertDialog.Builder(requireContext())

            // Layout dosyasını AlertDialog içine yerleştirme
            builder.setView(dialogView)

            // Pozitif ve negatif butonları tanımlama
            var positiveButton = dialogView.findViewById<Button>(R.id.delete_button_popup)
            var negativeButton = dialogView.findViewById<Button>(R.id.popup_button_cancel)

            // AlertDialog'u oluştur ve göster
            val alertDialog = builder.create()
            alertDialog.show()

            // Pozitif butona tıklama işlemi
            positiveButton.setOnClickListener {
                // Silme işlemi burada gerçekleştirilebilir
                val lightData2 =lightData
                lightData2?.let { it1 -> onDeleteButtonClicked(it1) }
                alertDialog.dismiss() // Dialog'u kapat
                Toast.makeText(context,
                    ContextCompat.getString(requireContext(), R.string.data_Deleted),Toast.LENGTH_SHORT).show()
                binding.historyDetailsViewFull.visibility = View.GONE


            }

            // Negatif butona tıklama işlemi
            negativeButton.setOnClickListener {
                alertDialog.dismiss() // Dialog'u kapat


            }


        }
        val lineChart = binding.lineChartDetails

    // Veri setini oluştur
        val entries = mutableListOf<Entry>()
        if (lightData != null) {
            entries.add(Entry(1f, lightData.minLightValue ?:0f))
        } // Örnek bir veri noktası
        if (lightData != null) {
            entries.add(Entry(2f, lightData.avgLightValue ?: 0f))
        } // Örnek bir veri noktası
        if (lightData != null) {
            entries.add(Entry(3f, lightData.maxLightValue ?:0f))
        } // Örnek bir veri noktası

        val dataSet = LineDataSet(entries, "Label") // Veri seti oluştur
        val orange = Color.rgb(234, 88, 12) // Mor renk
        dataSet.circleHoleColor= orange
        dataSet.circleColors= mutableListOf(orange)
        dataSet.color = orange
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Veri setini özelleştir
        lineChart?.description?.isEnabled = false
        lineChart?.setTouchEnabled(true)
        lineChart?.isDragEnabled = true
        lineChart?.setScaleEnabled(true)
        lineChart?.setDrawGridBackground(false)
        lineChart?.extraBottomOffset = 10f

        val xAxis = lineChart?.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.setDrawGridLines(false) // Dikey ızgara çizgilerini kapat
        xAxis?.textColor = Color.WHITE // X eksenindeki yazıların rengi

        xAxis?.granularity = 10f // 1 birim aralıklar        dataSet.valueTextColor = Color.RED

    // Grafik için veri oluştur
        val lineData = LineData(dataSet)

    // Grafik ayarları
        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.setDrawGridBackground(false)
        lineChart.animateX(1000) // X eksenini animasyonla göster

    // Grafik güncelle
        lineChart.invalidate()
        // Tablonun solunda değerleri göstermek için özellikleri ayarlayalım
        val leftAxis = lineChart?.axisLeft
        leftAxis?.setDrawGridLines(true) // Y eksenindeki ızgara çizgilerini etkinleştir
        leftAxis?.enableGridDashedLine(60f,30f,30f)
        leftAxis?.gridLineWidth =0.5f
        leftAxis?.gridColor = Color.rgb(55,59,84)
        leftAxis?.textColor = Color.WHITE // Sol y eksenindeki yazıların rengi
        leftAxis?.granularity = 50f // 50 birim aralıklarla
        leftAxis?.axisMinimum = 0f // Minimum değeri 0 olarak ayarla
        leftAxis?.axisMaximum = 800f // Maksimum değeri 500 olarak ayarla


        val rightAxis = lineChart?.axisRight
        rightAxis?.isEnabled = false

        val legend = lineChart?.legend
        legend?.isEnabled = false
        showInterstitialAdOnClick()
        return binding.root
    }
    private fun showInterstitialAdOnClick() {
        interstitialAdManager.loadInterstitialAd()
        interstitialAdManager.showInterstitialAdone() // Tabloyu sıfırla

    }





    private fun onDeleteButtonClicked(lightData: LightData) {
        lightDataViewModel.delete(lightData)
    }
    private fun shareText(text: String, context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }



}
