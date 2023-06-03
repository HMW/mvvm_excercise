package com.hm.currencyexercisexml.repository

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.FetchRateResult
import com.hm.currencyexercisexml.datasource.LocalDataSource
import com.hm.currencyexercisexml.datasource.RemoteDataSource
import com.hm.currencyexercisexml.utils.Constants
import com.hm.currencyexercisexml.utils.GeneralException
import com.hm.currencyexercisexml.utils.LogHelper
import com.hm.currencyexercisexml.utils.PrefManager
import kotlinx.coroutines.delay

class ApiLayerRepositoryImpl(
  private val localDataSource: LocalDataSource,
  private val remoteDataSource: RemoteDataSource
) : ApiLayerRepository {

  companion object {
    private const val TAG = "CurrencyRepositoryImpl"
    private const val RATE_EXPIRE_DURATION = 1000L * 60L * 30L // 30 minutes
    private const val DEBOUNCE = 100L
  }

  private val log: LogHelper = LogHelper.build(TAG, "Repo")

  override suspend fun getCurrencyList(): List<Currency> {
    delay(DEBOUNCE)
    return localDataSource.getCurrencyListFromDb() ?: kotlin
      .runCatching {
        log.d( "Local cache null, get currency from API")
        remoteDataSource.getCurrencyList()
      }
      .onSuccess {
        log.d("Get currency from API success, save to DB")
        localDataSource.saveCurrencies(it)
      }
      .onFailure {
        throw it
      }.getOrThrow()
  }

  override suspend fun getRateListByCurrency(currency: String): FetchRateResult {
    delay(DEBOUNCE)
    return if (isLocalRateExpired()) {
      runCatching {
        FetchRateResult(
          currency,
          remoteDataSource.getRateListByCurrency(currency)
        )
      }
        .onSuccess {
          log.d( "Get rate list of $currency success, receive rate list with ${it.rateList.size} items")
          updateCache(it)
        }
        .getOrThrow()
    } else {
      log.d("Reach bandwidth limitation, use local rate data instead. Desire currency is $currency")
      var resultCurrency = currency
      var rateList = localDataSource.getRatesByCurrency(currency)
      if (rateList == null) {
        resultCurrency = Constants.DEFAULT_CURRENCY
        rateList = localDataSource.getRatesByCurrency(Constants.DEFAULT_CURRENCY)
      }
      if (rateList == null) throw GeneralException("No cached rate data")
      FetchRateResult(resultCurrency, rateList)
    }
  }

  suspend fun updateCache(fetchResult: FetchRateResult) {
    log.d( "Update rate cache of ${fetchResult.currency}")
    localDataSource.saveRatesByCurrency(fetchResult.currency, fetchResult.rateList)
    PrefManager.putRateDataUpdateTime(System.currentTimeMillis())
  }

  fun isLocalRateExpired(): Boolean {
    PrefManager.getRateDataUpdateTime().let { lastUpdateTime ->
      return if (PrefManager.DEFAULT_LONG_VALUE == lastUpdateTime) {
        true
      } else {
        System.currentTimeMillis() > lastUpdateTime + RATE_EXPIRE_DURATION
      }
    }
  }

}
