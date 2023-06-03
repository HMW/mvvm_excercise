package com.hm.currencyexercisexml.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hm.currencyexercisexml.databinding.ItemRateBinding
import com.hm.currencyexercisexml.view.viewdata.RateItemViewData

class CurrencyRateRecyclerViewAdapter(
  diffCallback: DiffUtil.ItemCallback<RateItemViewData>
): ListAdapter<RateItemViewData, CurrencyRateViewHolder>(diffCallback) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRateViewHolder {
    return CurrencyRateViewHolder(
      ItemRateBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun onBindViewHolder(holder: CurrencyRateViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}
