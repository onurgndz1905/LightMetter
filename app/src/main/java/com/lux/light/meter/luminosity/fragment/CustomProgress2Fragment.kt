package com.lux.light.meter.luminosity.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentCustomProgress2Binding
import com.lux.light.meter.luminosity.viewmodel.SensorDataViewModel


class CustomProgress2Fragment : Fragment() {

    private lateinit var binding: FragmentCustomProgress2Binding
    private lateinit var sensorDataViewModel: SensorDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomProgress2Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize sensorDataViewModel
        sensorDataViewModel = ViewModelProvider(requireActivity()).get(SensorDataViewModel::class.java)

        // Observe changes in sensor data and update UI
        sensorDataViewModel.sensorData.observe(viewLifecycleOwner) { lightValue ->
            binding.customprogress2.setProgress(lightValue.toInt())
        }
    }
}
