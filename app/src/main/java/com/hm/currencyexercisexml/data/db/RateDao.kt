package com.hm.currencyexercisexml.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RateDao {

  @Query("SELECT * FROM currency_rate_table WHERE currency = :currency ORDER BY currency ASC")
  suspend fun getAllFromCurrency(currency: String): CurrencyRateEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(currencyRateEntity: CurrencyRateEntity)
}
