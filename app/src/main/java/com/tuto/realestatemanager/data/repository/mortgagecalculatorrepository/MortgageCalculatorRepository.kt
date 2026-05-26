package com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MortgageCalculatorRepository @Inject constructor() {

    private val housePrice = MutableStateFlow(0.0)
    private val downPayment = MutableStateFlow(0.0)
    private val rate = MutableStateFlow(0.0)
    private val duration = MutableStateFlow(0)

    fun setHousePrice(housePrice: Double) {
        this.housePrice.value = housePrice
    }

    fun setDownPayment(downPayment: Double) {
        this.downPayment.value = downPayment
    }

    fun setRate(rate: Double) {
        this.rate.value = rate
    }

    fun setDuration(duration: Int) {
        this.duration.value = duration
    }

    fun getHousePrice(): Flow<Double> {
        return housePrice
    }

    fun getDownPayment(): Flow<Double> {
        return downPayment
    }

    fun getRate(): Flow<Double> {
        return rate
    }

    fun getDuration(): Flow<Int> {
        return duration
    }
}