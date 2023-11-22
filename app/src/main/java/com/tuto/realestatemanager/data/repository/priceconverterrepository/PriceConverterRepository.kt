package com.tuto.realestatemanager.data.repository.priceconverterrepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceConverterRepository @Inject constructor() : PriceConverterRepositoryInterface {

    private val isDollarMutableStateFlow = MutableStateFlow(true)

    override val isDollarStateFlow: StateFlow<Boolean> = isDollarMutableStateFlow.asStateFlow()

    override fun convertPrice() {
        isDollarMutableStateFlow.update { !it }
    }
}