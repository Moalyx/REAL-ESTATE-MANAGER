package com.tuto.realestatemanager.data.repository.priceconverterrepository

import kotlinx.coroutines.flow.StateFlow

interface PriceConverterRepositoryInterface {

    val isDollarStateFlow: StateFlow<Boolean>

    fun convertPrice()

}