package com.hm.currencyexercisexml.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.hm.currencyexercisexml.MyApplication

/**
 * Use Constants.DEFAULT_CURRENCY as KEY for better scalability.
 */
object PrefManager {
  const val DEFAULT_LONG_VALUE = Long.MIN_VALUE

  private val pref: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(MyApplication.getApplicationContext())
  }

  fun putRateDataUpdateTime(time: Long) {
    pref.edit().putLong(Constants.DEFAULT_CURRENCY, time).apply()
  }

  fun getRateDataUpdateTime(): Long {
    return pref.getLong(Constants.DEFAULT_CURRENCY, DEFAULT_LONG_VALUE)
  }

}
