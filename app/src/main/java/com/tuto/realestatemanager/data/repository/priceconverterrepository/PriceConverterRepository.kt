package com.tuto.realestatemanager.data.repository.priceconverterrepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PriceConverterRepository @Inject constructor() {

    private val isDollarMutableStateFlow = MutableStateFlow(true)
    val isDollarStateFlow: StateFlow<Boolean> = isDollarMutableStateFlow.asStateFlow()

    fun convertPrice() {
        isDollarMutableStateFlow.update { !it }
    }
}