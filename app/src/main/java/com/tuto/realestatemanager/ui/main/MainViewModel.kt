package com.tuto.realestatemanager.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdIdRepositoryImpl
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    currentPropertyIdRepositoryImpl: CurrentPropertyIdIdRepositoryImpl,
    private val priceConverterRepository: PriceConverterRepository
) : ViewModel() {

    private var isTablet: Boolean = false

    val navigateSingleLiveEvent: SingleLiveEvent<MainViewAction> = SingleLiveEvent()

    init {
        navigateSingleLiveEvent.addSource(currentPropertyIdRepositoryImpl.currentIdFlow.filterNotNull().asLiveData()) {
            if (!isTablet) {
                navigateSingleLiveEvent.setValue(MainViewAction.NavigateToDetailActivity)
            }
        }
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }

    fun converterPrice(){
        priceConverterRepository.convertPrice()
    }
    val iconStatus: LiveData<Boolean> = priceConverterRepository.isDollarStateFlow.asLiveData(
        Dispatchers.IO)


}