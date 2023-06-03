package com.hm.currencyexercisexml.network

import com.hm.currencyexercisexml.data.response.CurrencyResponse
import com.hm.currencyexercisexml.data.response.RateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiLayerService {

  @GET("list")
  fun getCurrencies(): Call<CurrencyResponse>

  @GET("live")
  fun getRateListByCurrency(
    @Query("source")
    source: String
  ): Call<RateResponse>
}
