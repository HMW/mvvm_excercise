package com.hm.currencyexercisexml.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson

/**
 * Convert RateEntity list to JSON String then store to SQLite
 */
class CurrencyRateConverter {
  private var gson = Gson()

  @TypeConverter
  fun rateStringToList(jsonStr: String?) =
    Gson()
      .fromJson(jsonStr, Array<RateEntity>::class.java)
      .toList()

  @TypeConverter
  fun rateListToString(rateEntityList: List<RateEntity?>?): String =
    if (rateEntityList.isNullOrEmpty()) ""
    else gson.toJson(rateEntityList)
}
