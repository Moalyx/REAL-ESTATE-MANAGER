package com.tuto.realestatemanager.ui.mortgagecalcultator

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository.MortgageCalculatorRepository
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
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

    val loanAmountLiveData: LiveData<String> = liveData {
        combine(
            mortgageCalculatorRepository.getHousePrice(),
            mortgageCalculatorRepository.getDownPayment(),
            isDollarFlowUseCase.invoke()
        ) { housePrice, downPayment, isDollar ->

            val loanAmount = housePrice - downPayment
            val safeLoanAmount = loanAmount.coerceAtLeast(0.0)

            val currency = if (isDollar) "$" else "€"

            emit("${safeLoanAmount.toInt()} $currency")

        }.collect()
    }

    val getMonthlyPayment: LiveData<String> = liveData {
        combine(
            mortgageCalculatorRepository.getHousePrice(),
            mortgageCalculatorRepository.getDownPayment(),
            mortgageCalculatorRepository.getRate(),
            mortgageCalculatorRepository.getDuration(),
            isDollarFlowUseCase.invoke()
        ) { housePrice, downPayment, rate, duration, isDollar ->

            val loanAmount = housePrice - downPayment
            val safeLoanAmount = loanAmount.coerceAtLeast(0.0)

            val currentRate = (rate / 100) / 12
            val time = duration * 12
            val currency = if (isDollar) "$" else "€"

            if (safeLoanAmount == 0.0 || rate == 0.0 || duration == 0) {
                emit("0 $currency")
            } else {
                val monthlyPayment =
                    safeLoanAmount * currentRate / (1 - (1 + currentRate).pow(-time.toDouble()))

                emit("${monthlyPayment.toInt()} $currency")
            }

        }.collect()
    }

    fun setHousePrice(housePrice: String) {
        mortgageCalculatorRepository.setHousePrice(
            housePrice.toDoubleOrNull() ?: 0.0
        )
    }

    fun setDownPayment(downPayment: String) {
        mortgageCalculatorRepository.setDownPayment(
            downPayment.toDoubleOrNull() ?: 0.0
        )
    }

    fun setRate(rate: String) {
        mortgageCalculatorRepository.setRate(
            rate.toDoubleOrNull() ?: 0.0
        )
    }

    fun setDuration(duration: String) {
        mortgageCalculatorRepository.setDuration(
            duration.toIntOrNull() ?: 0
        )
    }

    val navigateSingleLiveEvent: SingleLiveEvent<MortgageViewAction> = SingleLiveEvent()

    fun onNavigateToMainActivity() {
        navigateSingleLiveEvent.setValue(MortgageViewAction.NavigateToMainActivity)
    }
}