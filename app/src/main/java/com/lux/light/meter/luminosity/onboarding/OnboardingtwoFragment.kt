package com.lux.light.meter.luminosity.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentOnboardingtwoBinding


class OnboardingtwoFragment : Fragment() {
    private lateinit var binding: FragmentOnboardingtwoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentOnboardingtwoBinding.inflate(inflater, container, false)

        val viewpager = activity?.findViewById<ViewPager2>(R.id.viewpager)

        binding.buttoncontiontwo.setOnClickListener {
            viewpager?.currentItem = 2
        }
        binding.buttonskiptwo.setOnClickListener {
            viewpager?.currentItem = 2
        }
        return binding.root

    }

}