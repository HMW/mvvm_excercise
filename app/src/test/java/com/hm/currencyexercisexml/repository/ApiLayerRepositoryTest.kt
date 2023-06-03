package com.hm.currencyexercisexml.repository

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.FetchRateResult
import com.hm.currencyexercisexml.data.Rate
import com.hm.currencyexercisexml.datasource.LocalDataSource
import com.hm.currencyexercisexml.datasource.RemoteDataSource
import com.hm.currencyexercisexml.utils.Constants
import com.hm.currencyexercisexml.utils.GeneralException
import com.hm.currencyexercisexml.utils.LogHelper
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.apache.log4j.BasicConfigurator
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ApiLayerRepositoryTest {

  private lateinit var mockLocalDataSource: LocalDataSource
  private lateinit var mockRemoteDataSource: RemoteDataSource
  private lateinit var mockLog: LogHelper
  private lateinit var testCurrencyStr: String
  private lateinit var testCurrencyList: List<Currency>
  private lateinit var testRateList: List<Rate>
  private lateinit var testDefaultRateList: List<Rate>
  private lateinit var testFetchRateResult: FetchRateResult
  private lateinit var testFetchDefaultRateResult: FetchRateResult

  @Before
  fun setup() {
    BasicConfigurator.configure()
    mockLocalDataSource = mockk<LocalDataSource>()
    mockRemoteDataSource = mockk<RemoteDataSource>()
    testCurrencyStr = "test.currency"
    testCurrencyList = mutableListOf(
      Currency(testCurrencyStr)
    )
    testRateList = mutableListOf(
      Rate(testCurrencyStr, 1.0)
    )
    testFetchRateResult = FetchRateResult(
      testCurrencyStr,
      testRateList
    )
    testDefaultRateList = mutableListOf(
      Rate(Constants.DEFAULT_CURRENCY, 1.0)
    )
    testFetchDefaultRateResult = FetchRateResult(
      Constants.DEFAULT_CURRENCY,
      testDefaultRateList
    )

    mockkObject(LogHelper.Companion)
    mockLog = mockk<LogHelper>()
    every { LogHelper.build(any(), any()) } returns mockLog
    justRun { mockLog.d(any()) }
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun test_get_currency_list_success_get_local() = runTest {
    coEvery { mockLocalDataSource.getCurrencyListFromDb() } returns testCurrencyList

    val repository = ApiLayerRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)
    assertEquals(testCurrencyList, repository.getCurrencyList())
  }

  @Test
  fun test_get_currency_list_success_get_remote() = runTest {
    coEvery { mockLocalDataSource.getCurrencyListFromDb() } returns null
    coJustRun { mockLocalDataSource.saveCurrencies(any()) }
    coEvery { mockRemoteDataSource.getCurrencyList() } returns testCurrencyList

    val repository = ApiLayerRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)
    assertEquals(testCurrencyList, repository.getCurrencyList())
  }

  @Test
  fun test_get_currency_list_fail_remote_throw_exception() = runTest {
    val exception = GeneralException("test exception")
    coEvery { mockLocalDataSource.getCurrencyListFromDb() } returns null
    coJustRun { mockLocalDataSource.saveCurrencies(any()) }
    coEvery { mockRemoteDataSource.getCurrencyList() } throws exception

    try {
      ApiLayerRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)
    } catch (e: Exception) {
      assertEquals(exception, e)
    }
  }

  @Test
  fun test_get_rate_list_by_currency_local_available() = runTest {
    val mockRepository = spyk(
      ApiLayerRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)
    )
    every { mockRepository.isLocalRateExpired() } returns false
    coEvery { mockLocalDataSource.getRatesByCurrency(testCurrencyStr) } returns testRateList
    assertEquals(testFetchRateResult, mockRepository.getRateListByCurrency(testCurrencyStr))
  }

  @Test
  fun test_get_rate_list_by_currency_local_available_no_desire_currency_rate_list() = runTest {
    val mockRepository = spyk(
      ApiLayerRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)
    )
    every { mockRepository.isLocalRateExpired() } returns false
    coEvery { mockLocalDataSource.getRatesByCurrency(testCurrencyStr) } returns null
    coEvery { mockLocalDataSource.getRatesByCurrency(Constants.DEFAULT_CURRENCY) } returns testDefaultRateList
    assertEquals(testFetchDefaultRateResult, mockRepository.getRateListByCurrency(testCurrencyStr))
  }

  @Test
  fun test_get_rate_list_by_currency_local_expired() = runTest {
    val mockRepository = spyk(
      ApiLayerRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)
    )
    every { mockRepository.isLocalRateExpired() } returns true
    coEvery { mockRepository.updateCache(testFetchRateResult) } returns Unit
    coEvery { mockRemoteDataSource.getRateListByCurrency(testCurrencyStr) } returns testRateList
    coJustRun { mockLocalDataSource.saveRatesByCurrency(any(), any()) }

    assertEquals(testFetchRateResult, mockRepository.getRateListByCurrency(testCurrencyStr))
  }

  @Test
  fun test_get_rate_list_by_currency_local_expired_remote_throw_exception() = runTest {
    val exception = GeneralException("test exception")
    val mockRepository = spyk(
      ApiLayerRepositoryImpl(mockLocalDataSource, mockRemoteDataSource)
    )
    every { mockRepository.isLocalRateExpired() } returns true
    coEvery { mockRepository.updateCache(testFetchRateResult) } returns Unit
    coEvery { mockRemoteDataSource.getRateListByCurrency(testCurrencyStr) } throws exception

    try {
      mockRepository.getRateListByCurrency(testCurrencyStr)
    } catch (e: Exception) {
      assertEquals(exception, e)
    }
  }

}
