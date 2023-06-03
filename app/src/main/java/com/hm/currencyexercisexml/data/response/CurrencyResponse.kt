package com.hm.currencyexercisexml.data.response

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
  @SerializedName("success")
  val isSuccess: Boolean? = false,
  val currencies: Map<String, String>? = null
)
