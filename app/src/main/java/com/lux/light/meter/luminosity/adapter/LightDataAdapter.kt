package com.lux.light.meter.luminosity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lux.light.meter.luminosity.Callback.LightDataDiffCallback
import com.lux.light.meter.luminosity.R
import com.lux.light.meter.luminosity.data.LightData
import com.lux.light.meter.luminosity.fragment.HistoryDetailsFragment
import com.lux.light.meter.luminosity.viewmodel.LightDataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LightDataAdapter(private val lightDataViewModel: LightDataViewModel, private val context: Context,    private val fragmentManager: FragmentManager
) : ListAdapter<LightData, LightDataAdapter.LightDataViewHolder>(
    LightDataDiffCallback()
) {

    inner class LightDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLightDuration: TextView = itemView.findViewById(R.id.duration_item)
        val textViewMin : TextView = itemView.findViewById(R.id.textViewMİNitem)
        val textViewMax : TextView = itemView.findViewById(R.id.TextviewMax_item)
        val textViewAvg : TextView = itemView.findViewById(R.id.textViewAVGitem)
        val textViewHistory : TextView = itemView.findViewById(R.id.textView_history_item)
        val textViewName : TextView = itemView.findViewById(R.id.test_Name)
        val buttonShare : Button = itemView.findViewById(R.id.share_history_item)
        val buttonDelete : Button = itemView.findViewById(R.id.buttonDelete_history_item)

        fun bind(lightData: LightData) {
            val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lightData.timestamp))

            textViewLightDuration.text = timestamp
            textViewAvg.text = lightData.avgLightValue.toString()
            textViewMax.text = lightData.maxLightValue.toString()
            textViewMin.text = lightData.minLightValue.toString()
            textViewHistory.text = lightData.recordingDate
            textViewName.text = "Test ${lightData.id}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.historyitem, parent, false)
        return LightDataViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LightDataViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

            holder.itemView.setOnClickListener {
                val selectedLightData = getItem(holder.adapterPosition)
                val fragment = HistoryDetailsFragment.newInstance(selectedLightData)
                fragmentManager.beginTransaction()
                    .replace(R.id.history_view, fragment)
                    .addToBackStack(null)
                    .commit()
            }



        holder.buttonDelete.setOnClickListener {
            // Silme butonu tıklamasını ele al
            val dialogView = LayoutInflater.from(context).inflate(R.layout.item_delete_popup, null)
            val builder = AlertDialog.Builder(context)

            // Layout dosyasını AlertDialog içine yerleştirme
            builder.setView(dialogView)

            // Pozitif ve negatif butonları tanımlama
            var positiveButton = dialogView.findViewById<Button>(R.id.delete_button_popup)
            var negativeButton = dialogView.findViewById<Button>(R.id.popup_button_cancel)

            // AlertDialog'u oluştur ve göster
            val alertDialog = builder.create()
            alertDialog.show()
    
            // Pozitif butona tıklama işlemi
            positiveButton.setOnClickListener {
                // Silme işlemi burada gerçekleştirilebilir
                val lightData = getItem(holder.adapterPosition)
                onDeleteButtonClicked(lightData)
                alertDialog.dismiss() // Dialog'u kapat
                Toast.makeText(context, getString(context,R.string.data_Deleted),Toast.LENGTH_SHORT).show()
            }

            // Negatif butona tıklama işlemi
            negativeButton.setOnClickListener {
                alertDialog.dismiss() // Dialog'u kapat
            }

        }


        holder.buttonShare.setOnClickListener {
            // Paylaşma butonu tıklamasını ele al
            val lightData = getItem(holder.adapterPosition)

            val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lightData.timestamp))
            val message = "Light Duration: $timestamp\n" +
                    "Min Light Value: ${lightData.minLightValue}\n" +
                    "Max Light Value: ${lightData.maxLightValue}\n" +
                    "Average Light Value: ${lightData.avgLightValue}\n" +
                    "Recording Date: ${lightData.recordingDate}"

            shareText(message,context)
        }
    }

    private fun onDeleteButtonClicked(lightData: LightData) {
        lightDataViewModel.delete(lightData)
    }
    private fun shareText(text: String, context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }


}