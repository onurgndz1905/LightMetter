package com.lux.light.meter.luminosity.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.lux.light.meter.luminosity.MainActivity
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentOnboardingfinishBinding


class OnboardingfinishFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingfinishBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingfinishBinding.inflate(inflater, container, false)
        val viewpager = activity?.findViewById<ViewPager2>(R.id.viewpager)

        binding.buttoncontionfinish.setOnClickListener {

            val intent = Intent(requireActivity(),MainActivity::class.java)
            startActivity(intent)
            onBoardingFinished()
        }
        return binding.root
    }

    private fun onBoardingFinished(){
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished",true) // onboarding kontrolü için false çevirdik false olduğunda onboarding her uygulama açıldığında çalışır !!
        editor.apply()
    }

}