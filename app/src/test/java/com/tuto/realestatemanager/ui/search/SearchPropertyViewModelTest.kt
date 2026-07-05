package com.tuto.realestatemanager.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.Search.SetParametersUseCase
import com.tuto.realestatemanager.getOrAwaitValue
import com.tuto.realestatemanager.model.SearchParameters
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchPropertyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getParametersFlowUseCase: GetParametersFlowUseCase = mockk()
    private val setParametersUseCase: SetParametersUseCase = mockk(relaxed = true)

    private val searchParametersFlow = MutableStateFlow<SearchParameters?>(null)

    private lateinit var viewModel: SearchPropertyViewModel

    @Before
    fun setUp() {
        every { getParametersFlowUseCase.invoke() } returns searchParametersFlow

        viewModel = SearchPropertyViewModel(
            getParametersFlowUseCase = getParametersFlowUseCase,
            setParametersUseCase = setParametersUseCase
        )
    }

    @Test
    fun `getParametersLiveData should expose current parameters`() = runTest {
        searchParametersFlow.value = createSearchParameters(
            type = "House",
            priceMinimum = 100000,
            priceMaximum = 300000,
            city = "Paris",
            soldStatus = true,
            minimumPhotos = 3
        )

        val result = viewModel.getParametersLiveData().getOrAwaitValue()

        assertEquals("House", result?.type)
        assertEquals(100000, result?.priceMinimum)
        assertEquals(300000, result?.priceMaximum)
        assertEquals("Paris", result?.city)
        assertEquals(true, result?.soldStatus)
        assertEquals(3, result?.minimumPhotos)
    }

    @Test
    fun `setParameters should send search parameters`() {
        viewModel.setParameters(
            type = "House",
            priceMinimum = "100000",
            priceMaximum = "300000",
            surfaceMinimum = "50",
            surfaceMaximum = "120",
            city = "Paris",
            poiTrain = true,
            poiAirport = false,
            poiResto = true,
            poiSchool = false,
            poiBus = true,
            poiPark = false,
            soldStatus = true,
            minimumPhotos = 3
        )

        verify {
            setParametersUseCase.invoke(
                SearchParameters(
                    type = "House",
                    priceMinimum = 100000,
                    priceMaximum = 300000,
                    surfaceMinimum = 50,
                    surfaceMaximum = 120,
                    city = "Paris",
                    poiTrain = true,
                    poiAirport = false,
                    poiResto = true,
                    poiSchool = false,
                    poiBus = true,
                    poiPark = false,
                    soldStatus = true,
                    minimumPhotos = 3
                )
            )
        }
    }

    @Test
    fun `setParameters should convert empty strings to null`() {
        viewModel.setParameters(
            type = "",
            priceMinimum = "",
            priceMaximum = "",
            surfaceMinimum = "",
            surfaceMaximum = "",
            city = "",
            poiTrain = false,
            poiAirport = false,
            poiResto = false,
            poiSchool = false,
            poiBus = false,
            poiPark = false,
            soldStatus = null,
            minimumPhotos = null
        )

        verify {
            setParametersUseCase.invoke(
                SearchParameters(
                    type = null,
                    priceMinimum = null,
                    priceMaximum = null,
                    surfaceMinimum = null,
                    surfaceMaximum = null,
                    city = null,
                    poiTrain = false,
                    poiAirport = false,
                    poiResto = false,
                    poiSchool = false,
                    poiBus = false,
                    poiPark = false,
                    soldStatus = null,
                    minimumPhotos = null
                )
            )
        }
    }

    @Test
    fun `setParameters should convert invalid numbers to null`() {
        viewModel.setParameters(
            type = "Flat",
            priceMinimum = "abc",
            priceMaximum = "300000",
            surfaceMinimum = "wrong",
            surfaceMaximum = "100",
            city = "Lyon",
            poiTrain = false,
            poiAirport = true,
            poiResto = false,
            poiSchool = true,
            poiBus = false,
            poiPark = true,
            soldStatus = false,
            minimumPhotos = null
        )

        verify {
            setParametersUseCase.invoke(
                SearchParameters(
                    type = "Flat",
                    priceMinimum = null,
                    priceMaximum = 300000,
                    surfaceMinimum = null,
                    surfaceMaximum = 100,
                    city = "Lyon",
                    poiTrain = false,
                    poiAirport = true,
                    poiResto = false,
                    poiSchool = true,
                    poiBus = false,
                    poiPark = true,
                    soldStatus = false,
                    minimumPhotos = null
                )
            )
        }
    }

    @Test
    fun `setParameters should keep sold status available`() {
        viewModel.setParameters(
            type = null,
            priceMinimum = null,
            priceMaximum = null,
            surfaceMinimum = null,
            surfaceMaximum = null,
            city = null,
            poiTrain = false,
            poiAirport = false,
            poiResto = false,
            poiSchool = false,
            poiBus = false,
            poiPark = false,
            soldStatus = false,
            minimumPhotos = null
        )

        verify {
            setParametersUseCase.invoke(
                SearchParameters(
                    soldStatus = false
                )
            )
        }
    }

    @Test
    fun `setParameters should keep minimum photos`() {
        viewModel.setParameters(
            type = null,
            priceMinimum = null,
            priceMaximum = null,
            surfaceMinimum = null,
            surfaceMaximum = null,
            city = null,
            poiTrain = false,
            poiAirport = false,
            poiResto = false,
            poiSchool = false,
            poiBus = false,
            poiPark = false,
            soldStatus = null,
            minimumPhotos = 4
        )

        verify {
            setParametersUseCase.invoke(
                SearchParameters(
                    minimumPhotos = 4
                )
            )
        }
    }

    @Test
    fun `clearParameters should set empty search parameters`() {
        viewModel.clearParameters()

        verify {
            setParametersUseCase.invoke(SearchParameters())
        }
    }

    @Test
    fun `onNavigateToMainActivity should emit navigation event`() {
        viewModel.onNavigateToMainActivity()

        assertEquals(
            SearchViewAction.NavigateToMainActivity,
            viewModel.navigateSingleLiveEvent.getOrAwaitValue()
        )
    }

    private fun createSearchParameters(
        type: String? = null,
        priceMinimum: Int? = null,
        priceMaximum: Int? = null,
        surfaceMinimum: Int? = null,
        surfaceMaximum: Int? = null,
        city: String? = null,
        poiTrain: Boolean = false,
        poiAirport: Boolean = false,
        poiResto: Boolean = false,
        poiSchool: Boolean = false,
        poiBus: Boolean = false,
        poiPark: Boolean = false,
        soldStatus: Boolean? = null,
        minimumPhotos: Int? = null
    ): SearchParameters {
        return SearchParameters(
            type = type,
            priceMinimum = priceMinimum,
            priceMaximum = priceMaximum,
            surfaceMinimum = surfaceMinimum,
            surfaceMaximum = surfaceMaximum,
            city = city,
            poiTrain = poiTrain,
            poiAirport = poiAirport,
            poiResto = poiResto,
            poiSchool = poiSchool,
            poiBus = poiBus,
            poiPark = poiPark,
            soldStatus = soldStatus,
            minimumPhotos = minimumPhotos
        )
    }
}