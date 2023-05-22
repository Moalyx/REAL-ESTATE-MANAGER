package com.tuto.realestatemanager.ui.mortgagecalcultator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository.MortgageCalculatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MortgageCalculatorViewModel @Inject constructor(
    private val mortgageCalculatorRepository: MortgageCalculatorRepository
) : ViewModel() {

    private val getAmount: LiveData<Double> =
        mortgageCalculatorRepository.getAmount().asLiveData(Dispatchers.IO)
    private val getRate: LiveData<Double> =
        mortgageCalculatorRepository.getRate().asLiveData(Dispatchers.IO)
    private val getDuration: LiveData<Int> =
        mortgageCalculatorRepository.getDuration().asLiveData(Dispatchers.IO)


    fun getRate(): LiveData<Double> {
        return mortgageCalculatorRepository.getRate().asLiveData(Dispatchers.IO)
    }

    fun getDuration(): LiveData<Int> {
        return mortgageCalculatorRepository.getDuration().asLiveData(Dispatchers.IO)
    }

    private val monthlyPayment = MediatorLiveData<Int>().apply {

        addSource(getAmount) { getAmount ->
            combine(getAmount, getRate.value!!, getDuration.value!!)
        }

        addSource(getRate) { getRate ->
            combine(getAmount.value!!, getRate, getDuration.value!!)
        }

        addSource(getDuration) { getDuration ->
            combine(getAmount.value!!, getRate.value!!, getDuration)
        }
    }

    private fun combine(amount: Double, rate: Double, duration: Int) {

        if (amount == null || rate == null || duration == null) {
            return
        }

        val principal: Double = amount
        val currentRate: Double = (rate / 100) / 12
        val time: Int = duration * 12

        val monthlyFee =
            (principal * currentRate / (1 - (1 + currentRate).pow(-time.toDouble()))).toInt()

        monthlyPayment.value = monthlyFee
    }

    val getMonthlyPayment = monthlyPayment


    fun setAmount(amount: Double) {
        mortgageCalculatorRepository.setAmount(amount)
    }

    fun setRate(rate: String) {
        if (rate == "") {
            mortgageCalculatorRepository.setRate(0.0.toString())
        } else
            mortgageCalculatorRepository.setRate(rate)
    }

    fun setDuration(duration: Int) {
        mortgageCalculatorRepository.setDuration(duration)
    }


//    fun setMortgageParameters(amount: Double, proposedRate: Double, years: Int){
//       mortgageCalculatorRepository.setMortgageParameters(amount, proposedRate, years)
//    }

    val monthlyFee = mortgageCalculatorRepository.monthlyPaymentMortgage().asLiveData()

//    fun monthlyPayment() : LiveData<Int> {
//        return mortgageCalculatorRepository.monthlyPaymentMortgage().asLiveData(Dispatchers.IO)
//    }

    fun getTotalInvestmentCost(monthlyFee: Int, length: Int): Int {
        return mortgageCalculatorRepository.totalInvestmentCost(monthlyFee, length)
    }

}