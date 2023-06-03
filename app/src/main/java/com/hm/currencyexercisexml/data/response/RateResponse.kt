package com.hm.currencyexercisexml.data.response

import com.google.gson.annotations.SerializedName

data class RateResponse(
  @SerializedName("success")
  val isSuccess: Boolean,
  val source: String? = null,
  val quotes: Map<String, Double>? = null
)
