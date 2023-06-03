package com.hm.currencyexercisexml.datasource

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.Rate

interface LocalDataSource {
  suspend fun getCurrencyListFromDb(): List<Currency>?
  suspend fun saveCurrencies(currencies: List<Currency>)
  suspend fun getRatesByCurrency(currency: String): List<Rate>?
  suspend fun saveRatesByCurrency(currency: String, rates: List<Rate>)
}
