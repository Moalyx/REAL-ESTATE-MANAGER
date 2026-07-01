package com.tuto.realestatemanager.ui.mortgagecalculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.tuto.realestatemanager.data.repository.mortgagecalculatorrepository.MortgageCalculatorRepository
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class MortgageCalculatorViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mortgageCalculatorRepository: MortgageCalculatorRepository = mockk(relaxed = true)
    private val isDollarFlowUseCase: IsDollarFlowUseCase = mockk()

    private val housePriceFlow = MutableStateFlow(0.0)
    private val downPaymentFlow = MutableStateFlow(0.0)
    private val rateFlow = MutableStateFlow(0.0)
    private val durationFlow = MutableStateFlow(0)
    private val isDollarFlow = MutableStateFlow(true)

    private lateinit var viewModel: MortgageCalculatorViewModel

    @Before
    fun setUp() {
        every { mortgageCalculatorRepository.getHousePrice() } returns housePriceFlow
        every { mortgageCalculatorRepository.getDownPayment() } returns downPaymentFlow
        every { mortgageCalculatorRepository.getRate() } returns rateFlow
        every { mortgageCalculatorRepository.getDuration() } returns durationFlow
        every { isDollarFlowUseCase.invoke() } returns isDollarFlow

        viewModel = MortgageCalculatorViewModel(
            mortgageCalculatorRepository = mortgageCalculatorRepository,
            isDollarFlowUseCase = isDollarFlowUseCase
        )
    }

    @Test
    fun `loanAmountLiveData should calculate loan amount in dollar`() = runTest {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        isDollarFlow.value = true

        val result = viewModel.loanAmountLiveData.getOrAwaitValue()

        assertEquals("250000 $", result)
    }

    @Test
    fun `loanAmountLiveData should calculate loan amount in euro`() = runTest {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        isDollarFlow.value = false

        val result = viewModel.loanAmountLiveData.getOrAwaitValue()

        assertEquals("250000 €", result)
    }

    @Test
    fun `loanAmountLiveData should return zero when down payment is higher than house price`() = runTest {
        housePriceFlow.value = 100000.0
        downPaymentFlow.value = 150000.0
        isDollarFlow.value = true

        val result = viewModel.loanAmountLiveData.getOrAwaitValue()

        assertEquals("0 $", result)
    }

    @Test
    fun `getMonthlyPayment should calculate monthly payment`() = runTest {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        rateFlow.value = 3.0
        durationFlow.value = 20
        isDollarFlow.value = true

        val result = viewModel.getMonthlyPayment.getOrAwaitValue()

        assertEquals("1386 $", result)
    }

    @Test
    fun `getMonthlyPayment should return zero when loan amount is zero`() = runTest {
        housePriceFlow.value = 100000.0
        downPaymentFlow.value = 100000.0
        rateFlow.value = 3.0
        durationFlow.value = 20
        isDollarFlow.value = true

        val result = viewModel.getMonthlyPayment.getOrAwaitValue()

        assertEquals("0 $", result)
    }

    @Test
    fun `getMonthlyPayment should return zero when rate is zero`() = runTest {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        rateFlow.value = 0.0
        durationFlow.value = 20
        isDollarFlow.value = true

        val result = viewModel.getMonthlyPayment.getOrAwaitValue()

        assertEquals("0 $", result)
    }

    @Test
    fun `getMonthlyPayment should return zero when duration is zero`() = runTest {
        housePriceFlow.value = 300000.0
        downPaymentFlow.value = 50000.0
        rateFlow.value = 3.0
        durationFlow.value = 0
        isDollarFlow.value = true

        val result = viewModel.getMonthlyPayment.getOrAwaitValue()

        assertEquals("0 $", result)
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
    fun `onNavigateToMainActivity should emit navigation event`() {
        viewModel.onNavigateToMainActivity()

        assertEquals(
            MortgageViewAction.NavigateToMainActivity,
            viewModel.navigateSingleLiveEvent.getOrAwaitValue()
        )
    }

    class MainDispatcherRule(
        private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ) : TestWatcher() {

        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

    private fun <T> LiveData<T>.getOrAwaitValue(): T {
        var data: T? = null
        val latch = CountDownLatch(1)

        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        observeForever(observer)

        if (!latch.await(2, TimeUnit.SECONDS)) {
            removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}