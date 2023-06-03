package com.hm.currencyexercisexml.datasource

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.Rate

interface RemoteDataSource {
  suspend fun getCurrencyList(): List<Currency>
  suspend fun getRateListByCurrency(currency: String): List<Rate>
}
