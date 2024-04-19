package com.lux.light.meter.luminosity.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lux.light.meter.luminosity.databinding.FragmentCustomProgress1Binding
import com.lux.light.meter.luminosity.`object`.Lightvalue
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.viewmodel.SensorDataViewModel
import java.util.*

class CustomProgress1Fragment : Fragment() {
    private lateinit var sensorDataViewModel: SensorDataViewModel
    private var _binding: FragmentCustomProgress1Binding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCustomProgress1Binding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // shared preferences başlatılıyor

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorDataViewModel = ViewModelProvider(requireActivity()).get(SensorDataViewModel::class.java)
        sensorDataViewModel.sensorData.observe(viewLifecycleOwner) { lightValue ->
            Log.e("lightvaluecustomprogress","${lightValue}")

            if (lightValue > 500) {
                binding.progrescustom.setMaxProgress(1000)
                binding.progrescustom.setProgress(lightValue.toInt())

            } else if (lightValue > 100) {
                binding.progrescustom.setMaxProgress(1500)
                binding.progrescustom.setProgress(lightValue.toInt())
            }
            binding.progrescustom.setProgress(lightValue.toInt()) // Bu satırın gerekli olduğunu düşünmüyorum
        }
        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings= savedUnit

        if (Unit.unitsettings ==0.0929f){
            binding.unitcustom1Text.text ="FC"
        }
        else if (Unit.unitsettings ==1f){
            binding.unitcustom1Text.text ="Lux"

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
