package com.hm.currencyexercisexml.datasource

import com.hm.currencyexercisexml.data.Currency
import com.hm.currencyexercisexml.data.Rate
import com.hm.currencyexercisexml.extension.formatToRate
import com.hm.currencyexercisexml.network.ApiLayerService
import com.hm.currencyexercisexml.utils.ApiErrorCode.INVALID_ACCESS_KEY
import com.hm.currencyexercisexml.utils.ApiErrorCode.INVALID_AUTH_CREDENTIALS
import com.hm.currencyexercisexml.utils.ApiErrorCode.LIMITATION_REACHED
import com.hm.currencyexercisexml.utils.GeneralException
import com.hm.currencyexercisexml.utils.InvalidAccessKeyException
import com.hm.currencyexercisexml.utils.InvalidAuthCredentialsException
import com.hm.currencyexercisexml.utils.LimitationReachedException
import com.hm.currencyexercisexml.utils.LogHelper
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException

class RemoteDataSourceImpl(
  private val apiLayerService: ApiLayerService
) : RemoteDataSource {

  companion object {
    private const val TAG = "RemoteDataSourceImpl"
    private const val ERR_MSG_CURRENCY_GENERAL = "Currency fetch failed"
    private const val ERR_MSG_RATE_GENERAL = "Rate fetch failed"
    private const val KEY_JSON_OBJ_MSG = "message"
  }

  private val log: LogHelper by lazy {
    LogHelper(TAG, "RemoteDS")
  }

  override suspend fun getCurrencyList(): List<Currency> {
    try {
      val response = apiLayerService
        .getCurrencies()
        .execute()
      if (response.body()?.isSuccess == true) {
        val result = response.body()?.currencies
        log.d("Result size = ${result?.size}")
        return result?.map {
          Currency(it.key)
        } ?: throw GeneralException(ERR_MSG_CURRENCY_GENERAL)
      } else {
        throw parseErrorResponse(response, ERR_MSG_CURRENCY_GENERAL)
      }
    } catch (e: SocketTimeoutException) {
      throw SocketTimeoutException(e.message)
    }
  }

  override suspend fun getRateListByCurrency(currency: String): List<Rate> {
    try {
      val response = apiLayerService
        .getRateListByCurrency(currency)
        .execute()
      if (response.body()?.isSuccess == true) {
        return response.body()?.quotes?.map {
          Rate(code = it.key.substringAfter(currency), rate = it.value.formatToRate())
        } ?: throw GeneralException(ERR_MSG_RATE_GENERAL)
      } else {
        throw parseErrorResponse(response, ERR_MSG_RATE_GENERAL)
      }
    } catch (e: SocketTimeoutException) {
      throw SocketTimeoutException(e.message)
    }
  }

  private fun <T> parseErrorResponse(response: Response<T>, generalMsg: String): Exception {
    val errorMsg = response.errorBody()?.let {
      val obj = JSONObject(it.string())
      obj.get(KEY_JSON_OBJ_MSG).toString()
    } ?: generalMsg
    log.w("ERROR, ${response.code()} - $errorMsg")
    return when (response.code()) {
      INVALID_ACCESS_KEY -> InvalidAccessKeyException(errorMsg)
      LIMITATION_REACHED -> LimitationReachedException(errorMsg)
      INVALID_AUTH_CREDENTIALS -> InvalidAuthCredentialsException(errorMsg)
      else -> GeneralException(errorMsg)
    }
  }

}
