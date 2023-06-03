package com.hm.currencyexercisexml.utils

import android.util.Log

/**
 * Print all logs with this helper class, make it easier to collect/send all the log to our
 * tracking/debug system in the future.
 */
class LogHelper(
  private val tag: String,
  private val prefix: String? = null
) {

  companion object {

    fun build(tag: String, prefix: String? = null): LogHelper {
      return LogHelper(tag, prefix)
    }

  }

  fun d(log: String) {
    val builder = StringBuilder()
    prefix?.let {
      builder.append(prefix)
      builder.append("] ")
    }
    builder.append(log)
    Log.d(tag, builder.toString())
  }

  fun w(log: String) {
    val builder = StringBuilder()
    prefix?.let {
      builder.append(prefix)
      builder.append("] ")
    }
    builder.append(log)
    Log.w(tag, builder.toString())
  }

}
