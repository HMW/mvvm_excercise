package com.hm.currencyexercisexml.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class RetrofitHelperTest {

  @Test
  fun retrofit_instance_base_url_isCorrect() {
    assertEquals(
      Constants.BASE_URL,
      RetrofitHelper.getInstance().baseUrl().toString()
    )
  }

}
