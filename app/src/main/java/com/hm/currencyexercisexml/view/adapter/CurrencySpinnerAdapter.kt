package com.hm.currencyexercisexml.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.hm.currencyexercisexml.view.viewdata.CurrencyViewData

class CurrencySpinnerAdapter(
  context: Context,
  resource: Int
): ArrayAdapter<CurrencyViewData>(context, resource) {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val textView = super.getView(position, convertView, parent) as TextView
    val item = getItem(position)
    textView.text = item?.code ?: ""
    return textView
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val textView = super.getDropDownView(position, convertView, parent) as TextView
    val item = getItem(position)
    textView.text = item?.code ?: ""
    return textView
  }

}
