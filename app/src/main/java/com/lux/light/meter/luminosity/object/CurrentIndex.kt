package com.lux.light.meter.luminosity.`object`
import android.content.Context
import android.content.SharedPreferences

object CurrentIndex {
     const val PREF_NAME = "MyPrefscurrentIndex"
     const val KEY_CURRENT_INDEX = "currentIndex"

     fun setCurrentIndex(context: Context, index: Int) {
          val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
          val editor = sharedPreferences.edit()
          editor.putInt(KEY_CURRENT_INDEX, index)
          editor.apply()
     }

     fun getCurrentIndex(context: Context): Int {
          val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
          return sharedPreferences.getInt(KEY_CURRENT_INDEX, 0)
     }
}
