package com.hm.currencyexercisexml.usecase

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.repository.ApiLayerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetCurrenciesUseCase(
  private val apiLayerRepository: ApiLayerRepository,
  private val dispatcher: CoroutineDispatcher
) {

  suspend fun fetchCurrencyList(): List<Currency> = withContext(dispatcher) {
    val result = apiLayerRepository.getCurrencyList()
    result
  }

}
