package com.hm.currencyexercisexml.usecase

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.repository.ApiLayerRepository
import com.hm.currencyexercisexml.utils.LimitationReachedException
import io.mockk.coEvery
import io.mockk.mockk
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
class GetCurrenciesUseCaseTest {

  private lateinit var testCurrency: Currency
  private lateinit var testCurrencyList: List<Currency>
  private lateinit var mockApiRepo: ApiLayerRepository

  @Before
  fun setup() {
    BasicConfigurator.configure()
    testCurrency = Currency("test.currency")
    testCurrencyList = mutableListOf(testCurrency)
    mockApiRepo = mockk<ApiLayerRepository>()
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun test_fetch_currencies_success() = runTest {
    val testDispatcher = StandardTestDispatcher(testScheduler)
    coEvery { mockApiRepo.getCurrencyList() } returns testCurrencyList

    val useCase = GetCurrenciesUseCase(mockApiRepo, testDispatcher)
    assertEquals(testCurrencyList, useCase.fetchCurrencyList())
  }

  @Test
  fun test_fetch_currencies_throw_exception() = runTest {
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testErrorMsg = "test.error.msg"
    val testException = LimitationReachedException(testErrorMsg)
    coEvery { mockApiRepo.getCurrencyList() } throws testException

    try {
      val useCase = GetCurrenciesUseCase(mockApiRepo, testDispatcher)
      assertEquals(testCurrencyList, useCase.fetchCurrencyList())
    } catch (e: Exception) {
      assertEquals(testException, e)
    }
  }

}
