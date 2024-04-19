package com.lux.light.meter.luminosity.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentBlurIspremium2Binding
import com.lux.light.meter.luminosity.databinding.FragmentBlurIspremiumBinding
import com.lux.light.meter.luminosity.`object`.CurrentIndex
import com.lux.light.meter.luminosity.paywall.PaywallFragment
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel2


class BlurIspremiumFragment2 : Fragment() {
    private lateinit var binding: FragmentBlurIspremium2Binding
    private var currentIndex = 0
    private lateinit var paywallviemodel: PaywallViewModel2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBlurIspremium2Binding.inflate(inflater, container, false)

        paywallviemodel = ViewModelProvider(requireActivity()).get(PaywallViewModel2::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentIndex = CurrentIndex.getCurrentIndex(requireContext())

        Log.e("currentindex", "$currentIndex")
        if (currentIndex == 2) {
            binding.backgroundIspremium.setBackgroundResource(R.drawable.premiumbackground1)
        } else if (currentIndex == 3) {
            binding.backgroundIspremium.setBackgroundResource(R.drawable.blur2)
        } else if (currentIndex == 4) {
            binding.backgroundIspremium.setBackgroundResource(R.drawable.blur3)
        }


        binding.gopremium2.setOnClickListener {
            paywallviemodel.setBooleanValue(true)
        }
    }
}
