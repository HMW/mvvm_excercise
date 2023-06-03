package com.hm.currencyexercisexml.usecase

import com.hm.currencyexercisexml.data.FetchRateResult
import com.hm.currencyexercisexml.repository.ApiLayerRepository
import com.hm.currencyexercisexml.utils.LogHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetRatesUseCase(
  private val apiLayerRepository: ApiLayerRepository,
  private val dispatcher: CoroutineDispatcher
) {
  companion object {
    private const val TAG = "GetRatesUseCase"
  }

  private val log: LogHelper = LogHelper.build(TAG, "Repo")

  suspend fun fetchRateByCurrency(currency: String): FetchRateResult = withContext(dispatcher) {
    log.d("Start fetch rate of $currency")
    val result = apiLayerRepository.getRateListByCurrency(currency)
    result
  }

}
