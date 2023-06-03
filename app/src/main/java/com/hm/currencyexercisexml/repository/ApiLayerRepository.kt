package com.hm.currencyexercisexml.repository

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.FetchRateResult

interface ApiLayerRepository {
  suspend fun getCurrencyList(): List<Currency>
  suspend fun getRateListByCurrency(currency: String): FetchRateResult
}
