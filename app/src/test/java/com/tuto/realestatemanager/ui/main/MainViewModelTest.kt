package com.tuto.realestatemanager.ui.main

import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
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
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val priceConverterRepository:
            PriceConverterRepository = mockk(relaxed = true)

    private val isDollarFlow = MutableStateFlow(true)

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        isDollarFlow.value = true

        every {
            priceConverterRepository.isDollarStateFlow
        } returns isDollarFlow

        viewModel = MainViewModel(
            priceConverterRepository = priceConverterRepository
        )
    }

    @Test
    fun `iconStatus should expose currency state from repository`() {
        assertEquals(true, viewModel.iconStatus.value)

        isDollarFlow.value = false

        assertEquals(false, viewModel.iconStatus.value)
    }

    @Test
    fun `converterPrice should call repository`() {
        viewModel.converterPrice()

        verify(exactly = 1) {
            priceConverterRepository.convertPrice()
        }
    }

    @Test
    fun `navigateToSearch should emit search navigation event`() = runTest {
        val event = async(
            start = CoroutineStart.UNDISPATCHED
        ) {
            viewModel.viewAction.first()
        }

        viewModel.navigateToSearch()

        assertEquals(
            MainViewAction.NavigateToSearch,
            event.await()
        )
    }
}