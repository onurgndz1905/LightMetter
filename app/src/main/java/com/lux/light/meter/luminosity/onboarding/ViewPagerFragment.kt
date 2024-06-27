package com.lux.light.meter.luminosity.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.adapter.ViewPagerAdapter
import com.lux.light.meter.luminosity.databinding.FragmentViewPagerBinding
import com.lux.light.meter.luminosity.paywall.PaywallFragment


class ViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)
    val fragmentList = arrayListOf<Fragment>(
        OnboardingoneFragment(),
        OnboardingtwoFragment(),
        OnboardingfinishFragment(),
    )

     val adapter = ViewPagerAdapter(
         fragmentList,
         childFragmentManager,
         lifecycle
     )
        binding.viewpager.adapter = adapter



    return binding.root
    }

}