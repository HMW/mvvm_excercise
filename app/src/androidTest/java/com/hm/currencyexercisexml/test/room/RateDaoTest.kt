package com.hm.currencyexercisexml.test.room

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hm.currencyexercisexml.base.DbTest
import com.hm.currencyexercisexml.data.Rate
import com.hm.currencyexercisexml.data.db.CurrencyRateEntity
import com.hm.currencyexercisexml.data.db.RateEntity
import com.hm.currencyexercisexml.rules.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
open class RateDaoTest : DbTest() {

  @get:Rule
  val coroutineTestRule = MainCoroutineRule()

  @Test
  fun saveRateAndGetAllByCodeTest() = runTest {
    val sourceCode = "USD"
    val rateList = listOf(
      Rate("CAD", 1.273555),
      Rate("AED", 3.673042),
      Rate("XCD", 2.70255),
      Rate("THB", 29.997504)
    )

    val rateRoomEntity = CurrencyRateEntity(sourceCode, rateList.map {
      RateEntity(
        it.code,
        it.rate
      )
    })

    appDatabase.rateDao().insert(rateRoomEntity)
    val rateListSize = appDatabase.rateDao().getAllFromCurrency(sourceCode)?.rates?.size
    assertEquals(rateListSize, 4)
  }

  @Test
  fun saveCurrencyAndGetByNonExistCodeTest() = runTest {
    val sourceCode = "USD"
    val rateList = listOf(
      Rate("CAD", 1.273555),
      Rate("AED", 3.673042),
      Rate("XCD", 2.70255),
      Rate("THB", 29.997504)
    )

    val rateRoomEntity = CurrencyRateEntity(sourceCode, rateList.map {
      RateEntity(
        it.code,
        it.rate
      )
    })

    appDatabase.rateDao().insert(rateRoomEntity)
    val result = appDatabase.rateDao().getAllFromCurrency("AED")?.rates
    assertEquals(result, null)
  }

  @Test
  fun getEmptyRateListTest() = runTest {
    val rateList = appDatabase.rateDao().getAllFromCurrency("USDAED")?.rates
    assertEquals(rateList, null)
  }
}
