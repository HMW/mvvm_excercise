package com.hm.currencyexercisexml.utils

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

  private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
      .baseUrl(Constants.BASE_URL)
      .client(
        OkHttpClient.Builder()
          .addInterceptor(
            HttpLoggingInterceptor().apply {
              level = HttpLoggingInterceptor.Level.BODY

            })
          .addInterceptor(Interceptor {
            val newRequest: Request = it.request().newBuilder()
              .addHeader(Constants.API_KEY, Constants.API_KEY_VALUE)
              .build()
            it.proceed(newRequest)
          })
          .connectTimeout(Constants.TIMEOUT_IN_SEC, TimeUnit.SECONDS)
          .readTimeout(Constants.TIMEOUT_IN_SEC, TimeUnit.SECONDS)
          .build()
      )
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  fun getInstance(): Retrofit {
    return retrofit
  }

}
