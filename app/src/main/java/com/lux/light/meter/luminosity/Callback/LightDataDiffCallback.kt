package com.lux.light.meter.luminosity.Callback

import androidx.recyclerview.widget.DiffUtil
import com.lux.light.meter.luminosity.data.LightData

class LightDataDiffCallback : DiffUtil.ItemCallback<LightData>() {
    override fun areItemsTheSame(oldItem: LightData, newItem: LightData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LightData, newItem: LightData): Boolean {
        return oldItem == newItem
    }
}