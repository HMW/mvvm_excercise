package com.hm.currencyexercisexml.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rate_table")
//@TypeConverters(CurrencyRateConverter::class)
data class CurrencyRateEntity(
  @PrimaryKey val currency: String,
  @ColumnInfo val rates: List<RateEntity>
)

@Entity
data class RateEntity(
  @ColumnInfo val currencyCode: String,
  @ColumnInfo val rate: Double
)
