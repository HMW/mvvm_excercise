package com.hm.currencyexercisexml.data.db

import androidx.room.Room
import com.hm.currencyexercisexml.MyApplication

object RoomHelper {

  private const val DB_NAME = "currency_database"

  val db: AppDatabase by lazy {
    Room
      .databaseBuilder(
        MyApplication.getApplicationContext(),
        AppDatabase::class.java,
        DB_NAME
      )
      .build()
  }

}
