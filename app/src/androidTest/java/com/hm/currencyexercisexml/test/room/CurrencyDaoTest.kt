package com.hm.currencyexercisexml.test.room

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hm.currencyexercisexml.base.DbTest
import com.hm.currencyexercisexml.data.db.CurrencyRoomEntity
import com.hm.currencyexercisexml.rules.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
open class CurrencyDaoTest : DbTest() {

  @get:Rule
  val coroutineTestRule = MainCoroutineRule()

  @Test
  fun saveCurrencyAndGetAllTest() = runTest {
    val currencyList = listOf(
      CurrencyRoomEntity("AED"),
      CurrencyRoomEntity("BZD"),
      CurrencyRoomEntity("CAD"),
      CurrencyRoomEntity("CLF")
    )
    appDatabase.currencyDao().insert(currencyList)
    val quotesSize = appDatabase.currencyDao().getAll().size
    assertEquals(quotesSize, 4)
  }

  @Test
  fun getEmptyCurrencyListTest() = runTest {
    val quotesSize = appDatabase.currencyDao().getAll().size
    assertEquals(quotesSize, 0)
  }

}
