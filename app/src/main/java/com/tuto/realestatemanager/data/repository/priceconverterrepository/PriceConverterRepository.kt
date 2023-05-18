package com.tuto.realestatemanager.data.repository.priceconverterrepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class PriceConverterRepository @Inject constructor() {

    private var isDollar: Boolean = true

    private val currentMoneyMutableLiveData = MutableStateFlow(true)

    val getCurrentMoneyLiveData: Flow<Boolean> = currentMoneyMutableLiveData

    fun convertPrice(){
        isDollar = !isDollar
        currentMoneyMutableLiveData.value = isDollar
    }
}