package com.lux.light.meter.luminosity.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.lux.light.meter.luminosity.R

class CustomInfoDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.custom_info_dialog, null)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val dialog = builder.create()

        val params = dialog.window?.attributes
        params?.width = convertDpToPx(80) // istediğiniz genişliği burada belirleyebilirsiniz
        params?.height = convertDpToPx(60) // istediğiniz yüksekliği burada belirleyebilirsiniz
        dialog.window?.attributes = params as WindowManager.LayoutParams

        // Opsiyonel: AlertDialog'un arka planını saydam yapın
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    private fun convertDpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
