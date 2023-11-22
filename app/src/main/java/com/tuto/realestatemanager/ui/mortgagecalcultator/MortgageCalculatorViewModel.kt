package com.tuto.realestatemanager.ui.mortgagecalcultator

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository.MortgageCalculatorRepository
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MortgageCalculatorViewModel @Inject constructor(
    private val mortgageCalculatorRepository: MortgageCalculatorRepository,
    private val isDollarFlowUseCase: IsDollarFlowUseCase
) : ViewModel() {

    val getMonthlyPayment: LiveData<String> = liveData {
        combine(
            mortgageCalculatorRepository.getAmount(),
            mortgageCalculatorRepository.getRate(),
            mortgageCalculatorRepository.getDuration(),
            isDollarFlowUseCase.invoke()

        ) { amount, rate, duration, isDollar ->

            val currentRate = (rate / 100) / 12
            val time = duration * 12

            if ((amount == 0.0) || (rate == 0.0) || (duration == 0)) {
                emit("0")
            } else {
                if (isDollar)
                    emit("${(amount * currentRate / (1 - (1 + currentRate).pow(-time.toDouble()))).toInt()} $")
                else
                    emit("${(amount * currentRate / (1 - (1 + currentRate).pow(-time.toDouble()))).toInt()} €")
            }
        }.collect()
    }

    fun setAmount(amount: String) {
        if (amount == "")
            mortgageCalculatorRepository.setAmount(0.0)
        else
            mortgageCalculatorRepository.setAmount(amount.toDouble())
    }

    fun setRate(rate: String) {
        if (rate == "")
            mortgageCalculatorRepository.setRate(0.0.toString())
        else
            mortgageCalculatorRepository.setRate(rate)
    }

    fun setDuration(duration: String) {
        if (duration == "")
            mortgageCalculatorRepository.setDuration(0)
        else
            mortgageCalculatorRepository.setDuration(duration.toInt())

    }

}