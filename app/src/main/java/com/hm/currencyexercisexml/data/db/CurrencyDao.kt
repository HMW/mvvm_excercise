package com.hm.currencyexercisexml.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyDao {

  @Query("SELECT * FROM currency_table ORDER BY code ASC")
  suspend fun getAll(): List<CurrencyRoomEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(currency: List<CurrencyRoomEntity>)

}
