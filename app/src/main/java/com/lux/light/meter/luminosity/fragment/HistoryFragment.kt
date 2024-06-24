package com.lux.light.meter.luminosity.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lux.light.meter.luminosity.MainActivity
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.adapter.LightDataAdapter
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.data.LightData
import com.lux.light.meter.luminosity.databinding.FragmentHistoryBinding
import com.lux.light.meter.luminosity.`object`.Addisplay
import com.lux.light.meter.luminosity.viewmodel.LightDataViewModel


class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var lightDataViewModel: LightDataViewModel
    private lateinit var lightDataAdapter: LightDataAdapter
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)

        interstitialAdManager = InterstitialAdManager(requireContext())

        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lightDataViewModel = ViewModelProvider(this).get(LightDataViewModel::class.java)

        lightDataAdapter = LightDataAdapter(lightDataViewModel,requireContext(),getChildFragmentManager())

        binding.rvhistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lightDataAdapter
        }

        lightDataViewModel.getAllLightDataLiveData().observe(viewLifecycleOwner, Observer { lightDataList ->
            lightDataList?.let { lightDataAdapter.submitList(it) }
        })

        lightDataViewModel.getAllLightDataLiveData().observe(viewLifecycleOwner, Observer { lightDataList ->
            if (lightDataList.isEmpty()) {
                // Liste boş ise bir metin göster
                binding.historyImage.visibility = View.VISIBLE
                binding.textHistory.visibility = View.VISIBLE


            } else {
                // Liste dolu ise verileri RecyclerView'e gönder
                binding.rvhistory.visibility = View.VISIBLE
                binding.historyImage.visibility = View.GONE
                binding.textHistory.visibility = View.GONE

                lightDataAdapter.submitList(lightDataList)
            }
        })

        binding.imageButtonDeleteAll.setOnClickListener {
            lightDataViewModel.getAllLightDataLiveData().observe(viewLifecycleOwner, Observer { lightDataList ->
                if (lightDataList.isEmpty()){
                    Toast.makeText(requireContext(), getString(R.string.all_records_delted_list_is_empty), Toast.LENGTH_SHORT).show()
                }
                else{
                    // Silme butonu tıklamasını ele al
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.item_delete_all_popup, null)
                    val builder = AlertDialog.Builder(requireContext())

                    // Layout dosyasını AlertDialog içine yerleştirme
                    builder.setView(dialogView)

                    // Pozitif ve negatif butonları tanımlama
                    var positiveButton = dialogView.findViewById<Button>(R.id.delete_button_popup_all)
                    var negativeButton = dialogView.findViewById<Button>(R.id.popup_button_cancel_all)

                    // AlertDialog'u oluştur ve göster
                    val alertDialog = builder.create()
                    alertDialog.show()

                    // Pozitif butona tıklama işlemi
                    positiveButton.setOnClickListener {
                        // Silme işlemi burada gerçekleştirilebilir
                        lightDataViewModel.deleteAllLightData()
                        alertDialog.dismiss() // Dialog'u kapat

                    }

                    // Negatif butona tıklama işlemi
                    negativeButton.setOnClickListener {
                        alertDialog.dismiss() // Dialog'u kapat
                    }

                }
            })
        }

        showInterstitialAdOnClick()



    }
    private fun showInterstitialAdOnClick() {
        interstitialAdManager.loadInterstitialAd()
        interstitialAdManager.showInterstitialAdone() // Tabloyu sıfırla

    }




}