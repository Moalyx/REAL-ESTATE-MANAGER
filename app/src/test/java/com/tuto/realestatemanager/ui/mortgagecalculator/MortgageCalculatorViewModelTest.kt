package com.tuto.realestatemanager.ui.mortgagecalculator

import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository.MortgageCalculatorRepository
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MortgageCalculatorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mortgageCalculatorRepository:
            MortgageCalculatorRepository = mockk(relaxed = true)

    private val isDollarFlowUseCase:
            IsDollarFlowUseCase = mockk()

    private val housePriceFlow = MutableStateFlow(0.0)
    private val downPaymentFlow = MutableStateFlow(0.0)
    private val rateFlow = MutableStateFlow(0.0)
    private val durationFlow = MutableStateFlow(0)
    private val isDollarFlow = MutableStateFlow(true)

    private lateinit var viewModel: MortgageCalculatorViewModel

    @Before
    fun setUp() {
        housePriceFlow.value = 0.0
        downPaymentFlow.value = 0.0
        rateFlow.value = 0.0
        durationFlow.value = 0
        isDollarFlow.value = true

        every {
            mortgageCalculatorRepository.getHousePrice()
        } returns housePriceFlow

        every {
            mortgageCalculatorRepository.getDownPayment()
        } returns downPaymentFlow

        every {
            mortgageCalculatorRepository.getRate()
        } returns rateFlow

        every {
            mortgageCalculatorRepository.getDuration()
        } returns durationFlow

        every {
            isDollarFlowUseCase.invoke()
        } returns isDollarFlow

        viewModel = MortgageCalculatorViewModel(
            mortgageCalculatorRepository = mortgageCalculatorRepository,
            isDollarFlowUseCase = isDollarFlowUseCase
        )
    }

    @Test
    fun `loanAmount should calculate loan amount in dollar`() {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        isDollarFlow.value = true

        assertEquals(
            "250000 $",
            viewModel.loanAmount.value
        )
    }

    @Test
    fun `loanAmount should calculate loan amount in euro`() {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        isDollarFlow.value = false

        assertEquals(
            "250000 €",
            viewModel.loanAmount.value
        )
    }

    @Test
    fun `loanAmount should return zero when down payment is higher than house price`() {
        housePriceFlow.value = 100000.0
        downPaymentFlow.value = 150000.0
        isDollarFlow.value = true

        assertEquals(
            "0 $",
            viewModel.loanAmount.value
        )
    }

    @Test
    fun `monthlyPayment should calculate monthly payment`() {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        rateFlow.value = 3.0
        durationFlow.value = 20
        isDollarFlow.value = true

        assertEquals(
            "1386 $",
            viewModel.monthlyPayment.value
        )
    }

    @Test
    fun `monthlyPayment should return zero when loan amount is zero`() {
        housePriceFlow.value = 100000.0
        downPaymentFlow.value = 100000.0
        rateFlow.value = 3.0
        durationFlow.value = 20
        isDollarFlow.value = true

        assertEquals(
            "0 $",
            viewModel.monthlyPayment.value
        )
    }

    @Test
    fun `monthlyPayment should return zero when rate is zero`() {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        rateFlow.value = 0.0
        durationFlow.value = 20
        isDollarFlow.value = true

        assertEquals(
            "0 $",
            viewModel.monthlyPayment.value
        )
    }

    @Test
    fun `monthlyPayment should return zero when duration is zero`() {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        rateFlow.value = 3.0
        durationFlow.value = 0
        isDollarFlow.value = true

        assertEquals(
            "0 $",
            viewModel.monthlyPayment.value
        )
    }

    @Test
    fun `setHousePrice should send numeric value to repository`() {
        viewModel.setHousePrice("300000")

        verify {
            mortgageCalculatorRepository.setHousePrice(300000.0)
        }
    }

    @Test
    fun `setHousePrice should send zero when value is invalid`() {
        viewModel.setHousePrice("abc")

        verify {
            mortgageCalculatorRepository.setHousePrice(0.0)
        }
    }

    @Test
    fun `setDownPayment should send numeric value to repository`() {
        viewModel.setDownPayment("50000")

        verify {
            mortgageCalculatorRepository.setDownPayment(50000.0)
        }
    }

    @Test
    fun `setRate should send numeric value to repository`() {
        viewModel.setRate("3.5")

        verify {
            mortgageCalculatorRepository.setRate(3.5)
        }
    }

    @Test
    fun `setDuration should send numeric value to repository`() {
        viewModel.setDuration("20")

        verify {
            mortgageCalculatorRepository.setDuration(20)
        }
    }

    @Test
    fun `setDuration should send zero when value is invalid`() {
        viewModel.setDuration("abc")

        verify {
            mortgageCalculatorRepository.setDuration(0)
        }
    }

    @Test
    fun `onNavigateToMainActivity should emit navigation event`() = runTest {
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            viewModel.viewAction.first()
        }

        viewModel.onNavigateToMainActivity()

        assertEquals(
            MortgageViewAction.NavigateToMainActivity,
            event.await()
        )
    }
}