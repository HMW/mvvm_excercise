package com.hm.currencyexercisexml.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.hm.currencyexercisexml.MyApplication
import com.hm.currencyexercisexml.R
import com.hm.currencyexercisexml.databinding.FragmentMainBinding
import com.hm.currencyexercisexml.extension.textChanges
import com.hm.currencyexercisexml.utils.Constants
import com.hm.currencyexercisexml.utils.LogHelper
import com.hm.currencyexercisexml.view.adapter.CurrencyRateRecyclerViewAdapter
import com.hm.currencyexercisexml.view.adapter.CurrencySpinnerAdapter
import com.hm.currencyexercisexml.view.viewdata.CurrencyViewData
import com.hm.currencyexercisexml.view.viewdata.RateItemViewData
import com.hm.currencyexercisexml.viewmodel.MainViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainFragment: Fragment(R.layout.fragment_main) {

  companion object {
    private const val TAG = "MainFragment"

    fun newInstance(): Fragment {
      return MainFragment()
    }
  }

  private val log: LogHelper by lazy {
    LogHelper(TAG, "MF")
  }

  private val viewModel: MainViewModel by activityViewModels()

  // This property is only valid between onCreateView and
  // onDestroyView.
  private var _binding: FragmentMainBinding? = null
  private val binding get() = _binding!!

  private val currencyRateRecyclerViewAdapter: CurrencyRateRecyclerViewAdapter by lazy {
    CurrencyRateRecyclerViewAdapter(object : DiffUtil.ItemCallback<RateItemViewData>() {
      override fun areItemsTheSame(
        oldItem: RateItemViewData,
        newItem: RateItemViewData
      ): Boolean {
        return oldItem.code == newItem.code
      }

      override fun areContentsTheSame(
        oldItem: RateItemViewData,
        newItem: RateItemViewData
      ): Boolean {
        return oldItem.rate == newItem.rate
      }
    })
  }

  private val currencySpinnerAdapter: CurrencySpinnerAdapter by lazy {
    CurrencySpinnerAdapter(
      MyApplication.getApplicationContext(),
      R.layout.item_currency
    )
  }

  @OptIn(FlowPreview::class)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentMainBinding.inflate(inflater, container, false)
    binding.apply {
      etInput
        .textChanges()
        .debounce(Constants.EDIT_TEXT_CHANGE_EVENT_DEBOUNCE)
        .onEach {
          viewModel.setAmount(
            if (it.isNullOrEmpty()) Constants.EDIT_TEXT_DEFAULT_VALUE
            else it.toString().toLong()
          )
        }
        .launchIn(lifecycleScope)
      acsCurrencies.apply {
        adapter = currencySpinnerAdapter
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
          override fun onNothingSelected(p0: AdapterView<*>?) {}

          override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, p3: Long) {
            log.d((parent.selectedItem as CurrencyViewData).code)
            viewModel.setSelectedCurrency((parent.selectedItem as CurrencyViewData).code)
          }
        }
      }
      rvCurrenciesRate.apply {
        adapter = currencyRateRecyclerViewAdapter
        layoutManager = LinearLayoutManager(this@MainFragment.context)
        itemAnimator = DefaultItemAnimator()
        setHasFixedSize(true)
      }
    }
    viewLifecycleOwner.lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          viewModel.loadingStrRes.collect {
            if (it > 0) showLoading(it) else hideLoading()
          }
        }
        launch {
          viewModel.currencyList.collect {
            it?.map { currency ->
              CurrencyViewData(currency.code)
            }?.let { currencyViewDataList ->
              currencySpinnerAdapter.addAll(currencyViewDataList)
              setupInitView()
            }
          }
        }
        launch {
          viewModel.rateItemViewDataList.collect { rateItemViewDataList ->
            if (rateItemViewDataList?.isNotEmpty() == true) {
              log.d("Submit ${rateItemViewDataList.size} items to recycler view")
              currencyRateRecyclerViewAdapter.submitList(rateItemViewDataList)
            }
          }
        }
        launch {
          viewModel.errorMsg.collect {
            if (it.isNullOrEmpty()) hideError() else showError(it)
          }
        }
      }
    }
    return binding.root
  }

  private fun showLoading(strRes: Int) {
    binding.actvLoading.setText(strRes)
    binding.llLoadingParent.visibility = View.VISIBLE
  }

  private fun hideLoading() {
    binding.actvLoading.text = ""
    binding.llLoadingParent.visibility = View.GONE
  }

  private fun showError(errorMsg: String) {
    binding.actError.apply {
      text = errorMsg
      visibility = View.VISIBLE
    }
  }

  private fun hideError() {
    binding.actError.apply {
      text = ""
      visibility = View.GONE
    }
  }

  private fun setupInitView() {
    binding.apply {
      acsCurrencies.setSelection(
        currencySpinnerAdapter.getPosition(
          CurrencyViewData(Constants.DEFAULT_CURRENCY)
        )
      )
    }
    viewModel.setSelectedCurrency(Constants.DEFAULT_CURRENCY)
  }

}
