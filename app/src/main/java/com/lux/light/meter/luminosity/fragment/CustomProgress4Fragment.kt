package com.lux.light.meter.luminosity.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentCustomProgress4Binding
import com.lux.light.meter.luminosity.viewmodel.SensorDataViewModel

class CustomProgress4Fragment : Fragment() {
    private lateinit var binding: FragmentCustomProgress4Binding
    private lateinit var sensorDataViewModel: SensorDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomProgress4Binding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sensorDataViewModel = ViewModelProvider(requireActivity()).get(SensorDataViewModel::class.java)
        sensorDataViewModel.sensorData.observe(viewLifecycleOwner) { lightValue ->

            if (lightValue > 500) {
                binding.progressCustom4.setMaxProgress(1000)
                binding.progressCustom4.setProgress(lightValue.toInt())

            } else if (lightValue > 100) {
                binding.progressCustom4.setMaxProgress(1500)
                binding.progressCustom4.setProgress(lightValue.toInt())
            }
            binding.progressCustom4.setProgress(lightValue.toInt())
        }
    }



}