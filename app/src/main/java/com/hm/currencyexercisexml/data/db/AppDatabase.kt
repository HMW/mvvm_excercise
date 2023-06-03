package com.hm.currencyexercisexml.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [CurrencyRoomEntity::class, CurrencyRateEntity::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(CurrencyRateConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun currencyDao(): CurrencyDao
  abstract fun rateDao(): RateDao
}
