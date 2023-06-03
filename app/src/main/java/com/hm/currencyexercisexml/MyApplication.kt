package com.hm.currencyexercisexml

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

class MyApplication : Application() {

  companion object {
    private lateinit var context: WeakReference<Context>

    fun getApplicationContext(): Context {
      return context.get()!!
    }
  }

  override fun onCreate() {
    super.onCreate()
    context = WeakReference(applicationContext)
  }

}
