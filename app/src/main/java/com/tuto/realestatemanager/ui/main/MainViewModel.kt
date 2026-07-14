package com.tuto.realestatemanager.ui.main

import androidx.lifecycle.ViewModel
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val priceConverterRepository: PriceConverterRepository,
) : ViewModel() {

    private val _viewAction = MutableSharedFlow<MainViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

    val iconStatus: StateFlow<Boolean> =
        priceConverterRepository.isDollarStateFlow

    fun converterPrice() {
        priceConverterRepository.convertPrice()
    }

    fun navigateToSearch() {
        _viewAction.tryEmit(MainViewAction.NavigateToSearch)
    }
}