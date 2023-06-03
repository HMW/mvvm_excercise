package com.hm.currencyexercisexml.datasource

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.Rate
import com.hm.currencyexercisexml.data.db.CurrencyDao
import com.hm.currencyexercisexml.data.db.CurrencyRateEntity
import com.hm.currencyexercisexml.data.db.CurrencyRoomEntity
import com.hm.currencyexercisexml.data.db.RateDao
import com.hm.currencyexercisexml.data.db.RateEntity
import com.hm.currencyexercisexml.utils.LogHelper

class LocalDataSourceImpl(
  private val currencyDao: CurrencyDao,
  private val rateDao: RateDao
) : LocalDataSource {

  companion object {
    private const val TAG = "LocalDS"
  }

  private val log: LogHelper by lazy { LogHelper(TAG, "LocalDS") }

  override suspend fun getCurrencyListFromDb(): List<Currency>? {
    log.d("Get currency data from DB")
    val currencies = currencyDao.getAll()
    return if (currencies.isEmpty()) {
      null
    } else currencies.map {
      Currency(it.code)
    }
  }

  override suspend fun saveCurrencies(currencies: List<Currency>) {
    log.d("Insert ${currencies.size} currencies to DB")
    currencyDao.insert(
      currencies.map {
        CurrencyRoomEntity(it.code)
      }
    )
  }

  override suspend fun getRatesByCurrency(currency: String): List<Rate>? {
    log.d("get rate data of $currency from DB")
    val rates = rateDao.getAllFromCurrency(currency) ?: return null
    return rates.rates.map {
      Rate(it.currencyCode, it.rate)
    }
  }

  override suspend fun saveRatesByCurrency(currency: String, rates: List<Rate>) {
    log.d("Insert rate data of $currency to DB")
    rateDao.insert(
      CurrencyRateEntity(
        currency = currency,
        rates = rates.map {
          RateEntity(
            it.code,
            it.rate
          )
        }
      )
    )
  }

}
