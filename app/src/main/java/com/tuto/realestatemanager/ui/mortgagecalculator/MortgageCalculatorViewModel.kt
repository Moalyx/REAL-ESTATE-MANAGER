package com.tuto.realestatemanager.ui.mortgagecalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository.MortgageCalculatorRepository
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MortgageCalculatorViewModel @Inject constructor(
    private val mortgageCalculatorRepository: MortgageCalculatorRepository,
    private val isDollarFlowUseCase: IsDollarFlowUseCase
) : ViewModel() {

    val loanAmount: StateFlow<String> = combine(
        mortgageCalculatorRepository.getHousePrice(),
        mortgageCalculatorRepository.getDownPayment(),
        isDollarFlowUseCase.invoke()
    ) { housePrice, downPayment, isDollar ->

        val loanAmount = housePrice - downPayment
        val safeLoanAmount = loanAmount.coerceAtLeast(0.0)
        val currency = if (isDollar) "$" else "€"

        "${safeLoanAmount.toInt()} $currency"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "0 €"
    )

    val monthlyPayment: StateFlow<String> = combine(
        mortgageCalculatorRepository.getHousePrice(),
        mortgageCalculatorRepository.getDownPayment(),
        mortgageCalculatorRepository.getRate(),
        mortgageCalculatorRepository.getDuration(),
        isDollarFlowUseCase.invoke()
    ) { housePrice, downPayment, rate, duration, isDollar ->

        val loanAmount = housePrice - downPayment
        val safeLoanAmount = loanAmount.coerceAtLeast(0.0)

        val monthlyRate = (rate / 100) / 12
        val numberOfPayments = duration * 12
        val currency = if (isDollar) "$" else "€"

        if (
            safeLoanAmount == 0.0 ||
            rate == 0.0 ||
            duration == 0
        ) {
            "0 $currency"
        } else {
            val payment = safeLoanAmount * monthlyRate /
                    (1 - (1 + monthlyRate).pow(-numberOfPayments.toDouble()))

            "${payment.toInt()} $currency"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "0 €"
    )

    private val _viewAction = MutableSharedFlow<MortgageViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

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

    fun onNavigateToMainActivity() {
        _viewAction.tryEmit(
            MortgageViewAction.NavigateToMainActivity
        )
    }
}