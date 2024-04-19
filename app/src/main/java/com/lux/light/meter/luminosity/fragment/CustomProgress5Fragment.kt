package com.lux.light.meter.luminosity.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentCustomProgress5Binding
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.viewmodel.SensorAvgViewModel
import com.lux.light.meter.luminosity.viewmodel.SensorDataViewModel
import com.lux.light.meter.luminosity.viewmodel.SensorMaxDataViewModel
import com.lux.light.meter.luminosity.viewmodel.SensorMinDataViewModel

class CustomProgress5Fragment : Fragment() {

    private lateinit var binding: FragmentCustomProgress5Binding
    private lateinit var sensorDataViewModel: SensorDataViewModel
    private lateinit var sensorMinDataViewModel: SensorMinDataViewModel
    private lateinit var sensorMaxDataViewModel: SensorMaxDataViewModel
    private lateinit var sensorAvgViewModel: SensorAvgViewModel
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomProgress5Binding.inflate(layoutInflater,container,false)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // shared preferences başlatılıyor

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings= savedUnit
        sensorDataViewModel = ViewModelProvider(requireActivity()).get(SensorDataViewModel::class.java)
        sensorDataViewModel.sensorData.observe(viewLifecycleOwner) { lightValue ->
            binding.CustomProgress5.setProgressValue(lightValue.toInt())
            binding.textView32.text = "$lightValue ${
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
        }
        binding.CustomProgress5.setMaxProgress(1000)

        sensorMinDataViewModel = ViewModelProvider(requireActivity()).get(SensorMinDataViewModel::class.java)
        sensorMinDataViewModel.sensorData.observe(viewLifecycleOwner) { lightminValue ->
            binding.minlightprogress5.text = " ${lightminValue} min;"
        }

        sensorMaxDataViewModel = ViewModelProvider(requireActivity()).get(SensorMaxDataViewModel::class.java)
        sensorMaxDataViewModel.sensorData.observe(viewLifecycleOwner) { lightmaxValue ->
            binding.maxligtprogress5.text = " ${lightmaxValue} max"
        }

        sensorAvgViewModel = ViewModelProvider(requireActivity()).get(SensorAvgViewModel::class.java)
        sensorAvgViewModel.sensorData.observe(viewLifecycleOwner) { lightavgValue ->
            val formattedValue = String.format("%.1f", lightavgValue)
            binding.avglightprogress5.text = "$formattedValue avg;"
        }




        if (Unit.unitsettings ==0.0929f){
            binding.textView32.text ="FC"
        }
        else if (Unit.unitsettings ==1f){
            binding.textView32.text ="Lux"

        }

    }

}