package com.hm.currencyexercisexml.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.hm.currencyexercisexml.databinding.ItemRateBinding
import com.hm.currencyexercisexml.view.viewdata.RateItemViewData

class CurrencyRateViewHolder(
  private val binding: ItemRateBinding
): RecyclerView.ViewHolder(binding.root) {

  fun bind(data: RateItemViewData?) {
    data?.apply {
      binding.tvCurrency.text = code
      binding.tvRate.text = rate
    }
  }
}
