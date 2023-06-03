package com.hm.currencyexercisexml.viewmodel

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.FetchRateResult
import com.hm.currencyexercisexml.data.Rate
import com.hm.currencyexercisexml.rules.MainCoroutineRule
import com.hm.currencyexercisexml.usecase.GetCurrenciesUseCase
import com.hm.currencyexercisexml.usecase.GetRatesUseCase
import com.hm.currencyexercisexml.utils.Constants
import com.hm.currencyexercisexml.utils.GeneralException
import com.hm.currencyexercisexml.utils.LogHelper
import com.hm.currencyexercisexml.view.viewdata.RateItemViewData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.apache.log4j.BasicConfigurator
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

  private lateinit var mockGetCurrenciesUseCase: GetCurrenciesUseCase
  private lateinit var mockGetRatesUseCase: GetRatesUseCase
  private lateinit var mockLog: LogHelper
  private lateinit var vm: MainViewModel

  @ExperimentalCoroutinesApi
  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    BasicConfigurator.configure()
    mockGetCurrenciesUseCase = mockk<GetCurrenciesUseCase>()
    mockGetRatesUseCase = mockk<GetRatesUseCase>()
    vm = spyk(MainViewModel(mockGetCurrenciesUseCase, mockGetRatesUseCase))

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
  fun test_get_currencies() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      val mockCurrencyList = mockk<List<Currency>>()
      coEvery { mockGetCurrenciesUseCase.fetchCurrencyList() } returns mockCurrencyList
      vm.currencyList.collect()

      vm.getCurrencies()
      assertEquals(mockCurrencyList, vm.currencyList.value)
    }
  }

  @Test
  fun test_set_selected_currency() {
    vm.setSelectedCurrency("test.currency")
    verify(exactly = 1) { vm.calculateCurrencyWithRate() }
  }

  @Test
  fun test_set_amount() {
    vm.setAmount(1)
    verify(exactly = 1) { vm.calculateCurrencyWithRate() }
  }

  @Test
  fun test_calculate_currency_with_rate() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      val testCurrency = "test.currency"
      val testCurrencyCode = "CODE"
      val testCurrencyRate = 5.0
      val testRateList = mutableListOf(
        Rate(testCurrencyCode, testCurrencyRate)
      )
      val testFetchRateResult = FetchRateResult(testCurrency, testRateList)
      coEvery { mockGetRatesUseCase.fetchRateByCurrency(testCurrency) } returns testFetchRateResult
      vm.rateItemViewDataList.collect()

      vm.setSelectedCurrency(testCurrency)
      vm.setAmount(1)

      coVerify(exactly = 1) { mockGetRatesUseCase.fetchRateByCurrency(testCurrency) }
      assertEquals(
        mutableListOf(
          RateItemViewData(
            testCurrencyCode,
            vm.composeConvertResult(1, testCurrencyRate)
          )
        ),
        vm.rateItemViewDataList.value
      )

      // change value
      val testAmount = 5L
      vm.setAmount(testAmount)

      coVerify(exactly = 1) { mockGetRatesUseCase.fetchRateByCurrency(testCurrency) }
      assertEquals(
        mutableListOf(
          RateItemViewData(
            testCurrencyCode,
            vm.composeConvertResult(testAmount, testCurrencyRate)
          )
        ),
        vm.rateItemViewDataList.value
      )
    }
  }

  @Test
  fun test_compose_convert_result() {
    assertEquals(
      "100 * 0.5 = 50.0",
      vm.composeConvertResult(100, 0.5)
    )
    assertEquals(
      "10 * 10.0 = 100.0",
      vm.composeConvertResult(10, 10.0)
    )
    assertEquals(
      "100 / 0.5(to ${Constants.DEFAULT_CURRENCY}) * 10.0 = 2000.0",
      vm.composeConvertResult(100, 10.0, 0.5)
    )
  }

  @Test
  fun test_get_rate_to_default_currency() {
    val testRateList = mutableListOf(
      Rate("JPY", 1.0),
      Rate("TWD", 0.5)
    )
    assertEquals(1.0, vm.getRateToDefaultCurrency("JPY", testRateList))
    assertEquals(0.5, vm.getRateToDefaultCurrency("TWD", testRateList))

    val testException = GeneralException("Selected currency not found in cached rate list")
    try {
      vm.getRateToDefaultCurrency("USD", testRateList)
    } catch (e: Exception) {
      assertEquals(testException, e)
    }
  }

}
