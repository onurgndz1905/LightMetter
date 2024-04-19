package com.lux.light.meter.luminosity.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lux.light.meter.luminosity.MainActivity
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ObjectAnimatorBinding")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            if (onBoardingFinished()) {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }, 2000)

        // Resmin büyüklüğüne göre uygun değerler ayarlayın
        val scaleX = ObjectAnimator.ofFloat(binding.splachimagview, "scaleX", 1.0f, 5.0f)
        val scaleY = ObjectAnimator.ofFloat(binding.splachimagview, "scaleY", 1.0f, 5.0f)
        val alpha = ObjectAnimator.ofFloat(binding.splachimagview, "alpha", 1.0f, 0.5f)

        // Blur efekti için yeni bir ObjectAnimator ekleyin
        val blurAnimator = ObjectAnimator.ofFloat(binding.splachimagview, "blur", 0f, 25f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha, blurAnimator)
        animatorSet.duration = 2000 // Toplam animasyon süresi: 3 saniye
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.start()
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}