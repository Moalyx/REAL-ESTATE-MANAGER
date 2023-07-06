package com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.pow

class MortgageCalculatorRepository @Inject constructor() {

    private val amount = MutableStateFlow(0.0)
    private val rate = MutableStateFlow(0.0)
    private val duration = MutableStateFlow(0)
    private val monthlyFee = MutableStateFlow(0)

    fun setAmount(amount: Double) {
        this.amount.value = amount
    }

    fun setRate(rate: String) {
        this.rate.value = rate.toDouble()
    }

    fun setDuration(duration: Int) {
        this.duration.value = duration
    }

    fun getAmount(): Flow<Double> {
        return amount
    }

    fun getRate(): Flow<Double> {
        return rate
    }

    fun getDuration(): Flow<Int>{
        return duration
    }

    fun monthlyPaymentMortgage(): Flow<Int> {
        val principal: Double = amount.value
        val rate: Double = (rate.value / 100) / 12
        val time: Int = duration.value * 12
        monthlyFee.value = (principal * rate / (1 - (1 + rate).pow(-time.toDouble()))).toInt()
        return monthlyFee
    }

    fun totalInvestmentCost(monthlyFee: Int, mortgageLength: Int): Int {
        return monthlyFee * (mortgageLength * 12)
    }

}