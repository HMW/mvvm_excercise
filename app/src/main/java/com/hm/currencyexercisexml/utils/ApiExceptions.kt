package com.hm.currencyexercisexml.utils

data class GeneralException(override val message: String? = null) : Exception()
data class InvalidAccessKeyException(override val message: String? = null) : Exception()
data class LimitationReachedException(override val message: String? = null) : Exception()
data class InvalidAuthCredentialsException(override val message: String? = null) : Exception()
