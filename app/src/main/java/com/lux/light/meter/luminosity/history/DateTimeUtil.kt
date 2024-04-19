package com.lux.light.meter.luminosity.history

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateTimeUtil {
    companion object {
        fun getCurrentDateTime(): String {
            val cal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy h:mm:ss a", Locale.ENGLISH)
            val formattedDate = dateFormat.format(cal.time)
            return " $formattedDate"
        }
    }
}
