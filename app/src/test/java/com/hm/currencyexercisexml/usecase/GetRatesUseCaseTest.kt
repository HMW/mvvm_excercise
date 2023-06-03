package com.hm.currencyexercisexml.usecase

import com.hm.currencyexercisexml.data.FetchRateResult
import com.hm.currencyexercisexml.data.Rate
import com.hm.currencyexercisexml.repository.ApiLayerRepository
import com.hm.currencyexercisexml.utils.LimitationReachedException
import com.hm.currencyexercisexml.utils.LogHelper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.apache.log4j.BasicConfigurator
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetRatesUseCaseTest {

  private lateinit var testCurrency: String
  private lateinit var mockApiRepo: ApiLayerRepository
  private lateinit var mockLog: LogHelper

  @Before
  fun setup() {
    BasicConfigurator.configure()
    testCurrency = "test.currency"
    mockApiRepo = mockk<ApiLayerRepository>()

    mockkObject(LogHelper)
    mockLog = mockk<LogHelper>()
    every { LogHelper.build(any(), any()) } returns mockLog
    justRun { mockLog.d(any()) }
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun test_fetch_rates_success() = runTest {
    val testRate = Rate("code", 1.0)
    val testRateList = mutableListOf(testRate)
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testFetchRateResult = FetchRateResult(testCurrency, testRateList)
    coEvery { mockApiRepo.getRateListByCurrency(testCurrency) } returns testFetchRateResult

    val useCase = GetRatesUseCase(mockApiRepo, testDispatcher)
    assertEquals(testFetchRateResult, useCase.fetchRateByCurrency(testCurrency))
  }

  @Test
  fun test_fetch_rates_throws_exception() = runTest {
    val testErrorMsg = "test.error.msg"
    val testException = LimitationReachedException(testErrorMsg)
    val testDispatcher = StandardTestDispatcher(testScheduler)
    coEvery {
      mockApiRepo.getRateListByCurrency(testCurrency)
    } throws testException

    try {
      val useCase = GetRatesUseCase(mockApiRepo, testDispatcher)
      useCase.fetchRateByCurrency(currency = testCurrency)
    } catch (e: Exception) {
      assertEquals(testException, e)
    }
  }

}
