package com.hm.currencyexercisexml.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class CurrencyRoomEntity(
  @PrimaryKey val code: String
)
