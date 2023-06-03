package com.hm.currencyexercisexml

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.hm.currencyexercisexml.data.db.RoomHelper
import com.hm.currencyexercisexml.databinding.ActivityMainBinding
import com.hm.currencyexercisexml.datasource.LocalDataSource
import com.hm.currencyexercisexml.datasource.LocalDataSourceImpl
import com.hm.currencyexercisexml.datasource.RemoteDataSource
import com.hm.currencyexercisexml.datasource.RemoteDataSourceImpl
import com.hm.currencyexercisexml.network.ApiLayerService
import com.hm.currencyexercisexml.repository.ApiLayerRepository
import com.hm.currencyexercisexml.repository.ApiLayerRepositoryImpl
import com.hm.currencyexercisexml.usecase.GetCurrenciesUseCase
import com.hm.currencyexercisexml.usecase.GetRatesUseCase
import com.hm.currencyexercisexml.utils.RetrofitHelper
import com.hm.currencyexercisexml.view.MainFragment
import com.hm.currencyexercisexml.viewmodel.MainViewModel
import com.hm.currencyexercisexml.viewmodel.MainViewModelFactory
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

  // Use ViewBinding instead of DataBinding for shorter build time
  private lateinit var binding: ActivityMainBinding

  private val service: ApiLayerService by lazy {
    RetrofitHelper.getInstance().create(ApiLayerService::class.java)
  }
  private val remoteDataSource: RemoteDataSource by lazy { RemoteDataSourceImpl(service) }
  private val localDataSource: LocalDataSource by lazy {
    LocalDataSourceImpl(
      RoomHelper.db.currencyDao(),
      RoomHelper.db.rateDao()
    )
  }
  private val currencyRepo: ApiLayerRepository by lazy {
    ApiLayerRepositoryImpl(localDataSource, remoteDataSource)
  }
  private val getCurrenciesUseCase: GetCurrenciesUseCase by lazy {
    GetCurrenciesUseCase(currencyRepo, Dispatchers.IO)
  }
  private val getRatesUseCase: GetRatesUseCase by lazy {
    GetRatesUseCase(currencyRepo, Dispatchers.IO)
  }
  private val viewModel: MainViewModel by viewModels {
    MainViewModelFactory(getCurrenciesUseCase, getRatesUseCase)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    showFragment(MainFragment.newInstance())

    viewModel.getCurrencies()
  }

  private fun showFragment(fragment: Fragment) {
    supportFragmentManager.commit {
      replace(R.id.fragment_container_view, fragment)
    }
  }
}
