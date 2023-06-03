package com.hm.currencyexercisexml.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hm.currencyexercisexml.R
import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.Rate
import com.hm.currencyexercisexml.extension.formatToRate
import com.hm.currencyexercisexml.usecase.GetCurrenciesUseCase
import com.hm.currencyexercisexml.usecase.GetRatesUseCase
import com.hm.currencyexercisexml.utils.Constants
import com.hm.currencyexercisexml.utils.GeneralException
import com.hm.currencyexercisexml.utils.LogHelper
import com.hm.currencyexercisexml.view.viewdata.RateItemViewData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
  private val getCurrenciesUseCase: GetCurrenciesUseCase,
  private val getRatesUseCase: GetRatesUseCase
): ViewModelProvider.NewInstanceFactory() {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return with(modelClass) {
      when {
        isAssignableFrom(MainViewModel::class.java) -> {
          MainViewModel(getCurrenciesUseCase, getRatesUseCase)
        }
        else -> throw IllegalArgumentException("Unknown ViewModel (${modelClass.name}) class")
      }
    } as T
  }
}

class MainViewModel(
  private val getCurrenciesUseCase: GetCurrenciesUseCase,
  private val getRatesUseCase: GetRatesUseCase
): ViewModel() {

  companion object {
    private const val TAG = "MainViewModel"
    private const val DEFAULT_LOADING_STR_RES = -1
  }

  private val log: LogHelper by lazy { LogHelper(TAG, "VM") }

  // Exception handling
  val errorMsg: MutableStateFlow<String?> = MutableStateFlow(null)
  private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    log.d("CoroutineExceptionHandler: ${exception.message}")
    viewModelScope.launch {
      errorMsg.emit(exception.message)
      loadingStrRes.emit(DEFAULT_LOADING_STR_RES)
    }
  }

  val loadingStrRes: MutableStateFlow<Int> = MutableStateFlow(DEFAULT_LOADING_STR_RES)
  val currencyList: MutableStateFlow<List<Currency>?> = MutableStateFlow(null)
  val rateItemViewDataList: MutableStateFlow<List<RateItemViewData>?> = MutableStateFlow(null)
  private var currency: String = Constants.DEFAULT_CURRENCY
  private var amount: Long = Constants.EDIT_TEXT_DEFAULT_VALUE

  fun getCurrencies() {
    viewModelScope.launch(exceptionHandler) {
      loadingStrRes.emit(R.string.loading_currencies)
      log.d("Start fetch currency list")
      getCurrenciesUseCase.fetchCurrencyList().let {
        currencyList.emit(it)
        log.d("Get ${it.size} currencies")
      }
      loadingStrRes.emit(DEFAULT_LOADING_STR_RES)
      if (currencyList.value?.isNotEmpty() == true) {
        setSelectedCurrency(Constants.DEFAULT_CURRENCY)
      }
    }
  }

  fun setSelectedCurrency(c: String) {
    currency = c
    calculateCurrencyWithRate()
  }

  fun setAmount(a: Long) {
    amount = a
    calculateCurrencyWithRate()
  }

  fun calculateCurrencyWithRate() {
    viewModelScope.launch(exceptionHandler) {
      loadingStrRes.emit(R.string.loading_rates)
      val fetchRateResult = getRatesUseCase.fetchRateByCurrency(currency)

      // If desire currency's rate list is not available, convert to default currency first, then
      // we can convert to other currencies.
      val rateToDefaultCurrency =
        if (currency != fetchRateResult.currency) {
          getRateToDefaultCurrency(currency, fetchRateResult.rateList).formatToRate()
        } else null
      log.d("Should convert to default currency first = ${(currency != fetchRateResult.currency)}")

      rateItemViewDataList.emit(
        fetchRateResult
          .rateList
          .map { rateData ->
            RateItemViewData(
              rateData.code,
              composeConvertResult(
                amount,
                rateData.rate,
                if (Constants.DEFAULT_CURRENCY != rateData.code) // check is convert to desire currency
                  rateToDefaultCurrency
                else
                  null // skip if is default currency's view item
              )
            )
          }
      )
      log.d("Amount convert completed")
      loadingStrRes.emit(DEFAULT_LOADING_STR_RES)
    }
  }

  fun composeConvertResult(amount: Long, rate: Double, rateToDefaultCurrency: Double? = null): String {
    // Convert to default currency first if rateToDefaultCurrency is available
    val result = rateToDefaultCurrency?.let {
      amount.div(rateToDefaultCurrency).times(rate)
    } ?: kotlin.run {
      amount.times(rate)
    }
    val strBuilder = StringBuilder()
    strBuilder.append(amount)
    rateToDefaultCurrency?.let {
      strBuilder.append(" / ")
      strBuilder.append(it)
      strBuilder.append("(to ${Constants.DEFAULT_CURRENCY})")
    }
    strBuilder.append(" * ")
    strBuilder.append(rate)
    strBuilder.append(" = ")
    strBuilder.append(result.formatToRate())
    return strBuilder.toString()
  }

  fun getRateToDefaultCurrency(targetCurrency: String, defaultCurrencyRateList: List<Rate>): Double {
    return defaultCurrencyRateList
      .find { targetCurrency == it.code }
      ?.rate
      ?: throw GeneralException("Selected currency not found in cached rate list")
  }

}
