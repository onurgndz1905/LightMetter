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
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentCustomProgress3Binding
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.viewmodel.SensorDataViewModel

class CustomProgress3Fragment : Fragment() {

    private lateinit var binding: FragmentCustomProgress3Binding
    private lateinit var sensorDataViewModel: SensorDataViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomProgress3Binding.inflate(layoutInflater,container,false)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // shared preferences başlatılıyor

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorDataViewModel = ViewModelProvider(requireActivity()).get(SensorDataViewModel::class.java)
        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings= savedUnit
        sensorDataViewModel.sensorData.observe(viewLifecycleOwner) { lightValue ->

            binding.lightValueProgress3.text = lightValue.toString() + when (Unit.unitsettings) {
                0.0929f -> {
                    " FC"
                }
                1f -> {
                    " Lux"

                }
                else -> {
                    "Lux"
                }
            }
            if (lightValue > 500) {
                binding.progressCustom3.setMaxProgress(1000)
                binding.progressCustom3.setProgress(lightValue.toInt())

            } else if (lightValue > 100) {
                binding.progressCustom3.setMaxProgress(1500)
                binding.progressCustom3.setProgress(lightValue.toInt())
            }
            binding.progressCustom3.setProgress(lightValue.toInt())

        }




    }

}