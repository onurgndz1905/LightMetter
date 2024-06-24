package com.lux.light.meter.luminosity.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.applovin.InterstitialAdManager
import com.lux.light.meter.luminosity.databinding.FragmentRecommendBinding
import com.lux.light.meter.luminosity.`object`.IsPremium
import com.lux.light.meter.luminosity.`object`.RecommendationName
import com.lux.light.meter.luminosity.`object`.Unit
import com.lux.light.meter.luminosity.paywall.PaywallFragment
import com.lux.light.meter.luminosity.viewmodel.PaywallViewModel2
import eightbitlab.com.blurview.RenderScriptBlur


class RecommendFragment : Fragment() {

    private lateinit var binding: FragmentRecommendBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var interstitialAdManager: InterstitialAdManager
    private lateinit var paywallViewModel: PaywallViewModel2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecommendBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // shared preferences başlatılıyor
        interstitialAdManager = InterstitialAdManager(requireContext())

        val savedUnit = sharedPreferences.getFloat("unit_settings", 1f)
        Unit.unitsettings= savedUnit

        val textViews = listOf(

            binding.proText,
            binding.PROtex2,
            binding.PROtex3,
            binding.PROtex4,
            binding.PROtex5,
            binding.PROtext6,
            binding.PROtext6,
            binding.PROtext7,
            binding.PROtext8,
            binding.PROtext9,
            binding.PROtext10,
            binding.PROtext11

        )

        textViews.forEach { textView ->
            applyGradientToTextView(textView)
        }
        setupBlurView()
        if (IsPremium.is_premium){
            binding.blurViewBathroom.visibility = View.GONE
            binding.blurViewCafetiraiPremiumIcon.visibility = View.GONE
            binding.blurViewDiningRoompremiumIcon.visibility = View.GONE
            binding.blurViewKitcheenPremiumIcon.visibility = View.GONE
            binding.blurViewGymPremiumIcon.visibility = View.GONE
            binding.blurViewHotelPremiumIcon.visibility = View.GONE
            binding.blurViewLockerPremiumIcon.visibility = View.GONE
            binding.blurViewStudyDeskPremiumIcon.visibility = View.GONE
            binding.blurViewLibrary3PremiumIcon.visibility = View.GONE
            binding.blurViewlibraryy33DeskPremiumIcon.visibility = View.GONE
            binding.classroom.visibility = View.GONE

        }
        else{
            binding.blurViewBathroom.visibility = View.VISIBLE
            binding.blurViewCafetiraiPremiumIcon.visibility = View.VISIBLE
            binding.blurViewDiningRoompremiumIcon.visibility = View.VISIBLE
            binding.blurViewKitcheenPremiumIcon.visibility = View.VISIBLE
            binding.blurViewGymPremiumIcon.visibility = View.VISIBLE
            binding.blurViewHotelPremiumIcon.visibility = View.VISIBLE
            binding.blurViewLockerPremiumIcon.visibility = View.VISIBLE
            binding.blurViewStudyDeskPremiumIcon.visibility = View.VISIBLE
            binding.blurViewLibrary3PremiumIcon.visibility = View.VISIBLE
            binding.blurViewlibraryy33DeskPremiumIcon.visibility = View.VISIBLE
            binding.classroom.visibility = View.VISIBLE
        }

        showPopupRecommendation()

        click_recommendded()
        showInterstitialAdOnClick()

        paywallViewModel = ViewModelProvider(requireActivity()).get(PaywallViewModel2::class.java)
        paywallViewModel.booleanLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { newValue ->
            Log.e("paywalviewmodelll","${newValue}")
            if (newValue) {
                // Paywall gösterilsin
                replaceFragment2(PaywallFragment())
                binding.paywallScreenRecommend.visibility = View.VISIBLE // LightScreenIn'ı gizle
                binding.recommendHomeIn.visibility = View.GONE

            }
            else if(!newValue){
                binding.paywallScreenRecommend.visibility = View.GONE // LightScreenIn'ı göster
                binding.recommendHomeIn.visibility = View.VISIBLE

            }
            else {
                binding.paywallScreenRecommend.visibility = View.GONE // LightScreenIn'ı göster
                binding.recommendHomeIn.visibility = View.VISIBLE

            }
        })
        return binding.root
    }

    fun click_recommendded(){
        setOnClickListenerForLayout(binding.constraintLayoutStudydesk, getString(R.string.study_desk))
        setOnClickListenerForLayout(binding.constraintLayoutLivingRoom, getString(R.string.living_room))
        setOnClickListenerForLayout(binding.studyDeskLayout, getString(R.string.study_desk) + " 2")
        setOnClickListenerForLayout(binding.libraryLayout1, getString(R.string.library))
        setOnClickListenerForLayout(binding.constraintLayoutotel, getString(R.string.hotel))
        setOnClickListenerForLayout(binding.constraintLayoutlibrary, getString(R.string.library) + " 2")
        setOnClickListenerForLayout(binding.constraintLayoutbathroom, getString(R.string.bathroom))
        setOnClickListenerForLayout(binding.constraintLayoutkitcheen, getString(R.string.kitchen))
        setOnClickListenerForLayout(binding.constraintLayoutcafeteria, getString(R.string.cafetaria))
        setOnClickListenerForLayout(binding.constraintLayoutlockerroom, getString(R.string.locker_room))
        setOnClickListenerForLayout(binding.dinningRoomLayout, getString(R.string.dinning_room))
        setOnClickListenerForLayout(binding.constraintLayoutclassroom, getString(R.string.classroom))
        setOnClickListenerForLayout(binding.constraintLayoutgym, getString(R.string.gym))
    }
    fun setOnClickListenerForLayout(layout: View, recommendationName: String) {
        layout.setOnClickListener {
            if (layout == binding.constraintLayoutLivingRoom || layout == binding.studyDeskLayout) {
                replaceFragment(RecommendedLightFragment())
                RecommendationName.recommendation_name = recommendationName
                binding.recommendHomeIn.visibility = View.GONE
            } else {
                if (!IsPremium.is_premium) {
                    replaceFragment2(PaywallFragment())
                } else {
                    replaceFragment(RecommendedLightFragment())
                    RecommendationName.recommendation_name = recommendationName
                    binding.recommendHomeIn.visibility = View.GONE
                }
            }
            // Close PaywallFragment after handling fragment replacement
            (parentFragment as? RecommendFragment)?.removeRecommendFragment()
        }

    }


    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.recommend_home, fragment)
            .commit()
    }

    private fun replaceFragment2(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.paywall_Screen_recommend, fragment)
            .commit()
        binding.recommendHomeIn.visibility = View.GONE
        binding.paywallScreenRecommend.visibility = View.VISIBLE


    }
    fun removeRecommendFragment() {
        val fragment = childFragmentManager.findFragmentById(R.id.recommend_home)
        if (fragment is RecommendedLightFragment) {
            childFragmentManager.beginTransaction().remove(fragment).commit()
            binding.recommendHomeIn.visibility = View.VISIBLE

        }
    }

    fun showPopup(context: Context, anchorView: View, min: Comparable<*>, max: Float) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_info_dialog, null)

        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        popupWindow.isFocusable = true

        // Popup dışına tıklandığında kapat
        popupWindow.setOnDismissListener {
            // Popup kapatıldığında yapılacak işlemler
        }

        val textViewmax = view.findViewById<TextView>(R.id.textviewmax)
        val textViewmin = view.findViewById<TextView>(R.id.textviemin)

        textViewmax.text = "Max : $max"
        textViewmin.text = "Min : $min"




        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val x = location[0] + anchorView.width / 2 - popupWindow.width / 2
        val y = location[1] - popupWindow.height

        // Popup'ı belirli bir koordinatta göstermek için
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
    }

    fun showPopupRecommendation() {
        val buttonLayoutMap = mapOf(
            binding.bathroomDetails to binding.constraintLayoutbathroom,
            binding.caffeteriaDetails to binding.constraintLayoutcafeteria,
            binding.livingRoomDetails to binding.constraintLayoutLivingRoom,
            binding.studyDeskDetails2 to binding.studyDeskLayout,
            binding.studyDeskDetails to binding.constraintLayoutStudydesk,
            binding.classroomDetails to binding.constraintLayoutclassroom,
            binding.gymDetails to binding.constraintLayoutgym,
            binding.dinningRoomDetails to binding.dinningRoomLayout,
            binding.lockerRoomDetails to binding.constraintLayoutlockerroom,
            binding.kitchenDetails to binding.constraintLayoutkitcheen,
            binding.libraryDetails2 to binding.constraintLayoutlibrary,
            binding.hotelDetails to binding.constraintLayoutotel,
            binding.libraryDetails3 to binding.libraryLayout1
        )

        val buttonPositionMap = mapOf(
            binding.bathroomDetails to Pair(100*Unit.unitsettings, 250*Unit.unitsettings),
            binding.caffeteriaDetails to Pair(200*Unit.unitsettings, 500*Unit.unitsettings),
            binding.livingRoomDetails to Pair(200*Unit.unitsettings, 500*Unit.unitsettings),
            binding.studyDeskDetails2 to Pair(Unit.unitsettings*300, 750*Unit.unitsettings),
            binding.studyDeskDetails to Pair(300*Unit.unitsettings, 750*Unit.unitsettings),
            binding.classroomDetails to Pair(300*Unit.unitsettings, 750*Unit.unitsettings),
            binding.gymDetails to Pair(200*Unit.unitsettings, 500*Unit.unitsettings),
            binding.dinningRoomDetails to Pair(150*Unit.unitsettings, 500*Unit.unitsettings),
            binding.lockerRoomDetails to Pair(150*Unit.unitsettings, 300*Unit.unitsettings),
            binding.kitchenDetails to Pair(300*Unit.unitsettings, 500*Unit.unitsettings),
            binding.libraryDetails2 to Pair(300*Unit.unitsettings, 750*Unit.unitsettings),
            binding.hotelDetails to Pair(250*Unit.unitsettings, 400*Unit.unitsettings),
            binding.libraryDetails3 to Pair(300*Unit.unitsettings, 750*Unit.unitsettings)
        )

        buttonLayoutMap.forEach { (button, layout) ->
            button.setOnClickListener {
                val position = buttonPositionMap[button]
                position?.let { pos ->
                    showPopup(requireContext(), layout, pos.first, pos.second)
                }
            }
        }
    }

    private fun showInterstitialAdOnClick() {
        interstitialAdManager.loadInterstitialAd()
        interstitialAdManager.showInterstitialAdone() // Tabloyu sıfırla

    }
    private fun setupBlurView() {
        val radius = 4f
        binding.blurViewBathroom.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewCafetiraiPremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewGymPremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewDiningRoompremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewLockerPremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewStudyDeskPremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewKitcheenPremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewHotelPremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewBathroom.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.blurViewlibraryy33DeskPremiumIcon.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
        binding.classroom.setupWith(requireActivity().window.decorView.findViewById(android.R.id.content), RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
    }

    private fun applyGradientToTextView(textView: TextView) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())

        val shader = LinearGradient(
            0f, 0f, width, textView.textSize,
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.startColor),
                ContextCompat.getColor(requireContext(), R.color.centerColor),
                ContextCompat.getColor(requireContext(), R.color.endColor)
            ),
            null,
            Shader.TileMode.CLAMP
        )

        textView.paint.shader = shader
    }


}