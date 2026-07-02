package com.tuto.realestatemanager.ui.propertymap

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetAllPropertiesWithPhotosUseCase
import com.tuto.realestatemanager.getOrAwaitValue
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.map.MapViewAction
import com.tuto.realestatemanager.ui.map.MapViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUserLocationFlowUseCase: GetUserLocationFlowUseCase = mockk()
    private val getParametersFlowUseCase: GetParametersFlowUseCase = mockk()
    private val getAllPropertiesWithPhotosUseCase: GetAllPropertiesWithPhotosUseCase = mockk()
    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk(relaxed = true)

    private val propertiesFlow = MutableStateFlow<List<PropertyWithPhotosEntity>>(emptyList())
    private val searchParametersFlow = MutableStateFlow<SearchParameters?>(null)
    private val userLocationFlow = MutableStateFlow<Location?>(null)
    private val currentIdFlow = MutableStateFlow<Long?>(null)

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        every { getAllPropertiesWithPhotosUseCase.invoke() } returns propertiesFlow
        every { getParametersFlowUseCase.invoke() } returns searchParametersFlow
        every { getUserLocationFlowUseCase.invoke() } returns userLocationFlow
        every { currentPropertyIdRepository.currentIdFlow } returns currentIdFlow

        viewModel = MapViewModel(
            getUserLocationFlowUseCase = getUserLocationFlowUseCase,
            getParametersFlowUseCase = getParametersFlowUseCase,
            getAllPropertiesWithPhotosUseCase = getAllPropertiesWithPhotosUseCase,
            currentPropertyIdRepository = currentPropertyIdRepository
        )
    }

    @Test
    fun `should expose all markers when no search parameters`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, city = "Paris", lat = 48.8566, lng = 2.3522),
            createProperty(id = 2L, city = "Lyon", lat = 45.7640, lng = 4.8357)
        )

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(2, result.markers.size)
        assertEquals(1L, result.markers[0].id)
        assertEquals(2L, result.markers[1].id)
    }

    @Test
    fun `should filter markers by city`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, city = "Paris", lat = 48.8566, lng = 2.3522),
            createProperty(id = 2L, city = "Lyon", lat = 45.7640, lng = 4.8357)
        )

        searchParametersFlow.value = createSearchParameters(city = "Paris")

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(1, result.markers.size)
        assertEquals(1L, result.markers.first().id)
        assertEquals("Paris", result.markers.first().address)
    }

    @Test
    fun `should filter markers by type`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, type = "House", lat = 48.8566, lng = 2.3522),
            createProperty(id = 2L, type = "Flat", lat = 45.7640, lng = 4.8357)
        )

        searchParametersFlow.value = createSearchParameters(type = "Flat")

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(1, result.markers.size)
        assertEquals(2L, result.markers.first().id)
    }

    @Test
    fun `should filter markers by price`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, price = 100000, lat = 48.8566, lng = 2.3522),
            createProperty(id = 2L, price = 300000, lat = 45.7640, lng = 4.8357)
        )

        searchParametersFlow.value = createSearchParameters(
            priceMinimum = 200000,
            priceMaximum = 400000
        )

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(1, result.markers.size)
        assertEquals(2L, result.markers.first().id)
    }

    @Test
    fun `should ignore property without coordinates`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, lat = 48.8566, lng = 2.3522),
            createProperty(id = 2L, lat = null, lng = null)
        )

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(1, result.markers.size)
        assertEquals(1L, result.markers.first().id)
    }

    @Test
    fun `should use user location when available`() = runTest {
        val location: Location = mockk()
        every { location.latitude } returns 43.2965
        every { location.longitude } returns 5.3698

        userLocationFlow.value = location
        propertiesFlow.value = listOf(createProperty(id = 1L))

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(43.2965, result.lat, 0.0)
        assertEquals(5.3698, result.lng, 0.0)
    }

    @Test
    fun `should use default location when user location is null`() = runTest {
        userLocationFlow.value = null
        propertiesFlow.value = listOf(createProperty(id = 1L))

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(48.8566, result.lat, 0.0)
        assertEquals(2.3522, result.lng, 0.0)
    }

    @Test
    fun `should expose selected marker id`() = runTest {
        currentIdFlow.value = 2L

        propertiesFlow.value = listOf(
            createProperty(id = 1L),
            createProperty(id = 2L)
        )

        val result = viewModel.getMapViewState.getOrAwaitValue()

        assertEquals(2L, result.selectedMarkerId)
    }

    @Test
    fun `setMarkerId should update current property id and navigate on phone`() {
        viewModel.setMarkerId(1L)

        verify {
            currentPropertyIdRepository.setCurrentId(1L)
        }

        assertEquals(
            MapViewAction.NavigateToDetailActivity,
            viewModel.navigateSingleLiveEvent.getOrAwaitValue()
        )
    }

    @Test
    fun `setMarkerId should update current property id without navigation on tablet`() {
        viewModel.onConfigurationChanged(true)

        viewModel.setMarkerId(1L)

        verify {
            currentPropertyIdRepository.setCurrentId(1L)
        }

        assertNull(viewModel.navigateSingleLiveEvent.value)
    }

    private fun createProperty(
        id: Long = 1L,
        type: String = "House",
        price: Int = 300000,
        address: String = "Paris",
        city: String = "Paris",
        state: String = "France",
        zipCode: Int = 75000,
        country: String = "France",
        surface: Int = 80,
        lat: Double? = 48.8566,
        lng: Double? = 2.3522,
        description: String = "description",
        room: Int = 4,
        bedroom: Int = 2,
        bathroom: Int = 1,
        agent: String = "Agent",
        propertySold: Boolean = false,
        propertyOnSaleSince: String = "01/01/2024",
        propertyDateOfSale: String = "",
        poiTrain: Boolean = false,
        poiAirport: Boolean = false,
        poiResto: Boolean = false,
        poiSchool: Boolean = false,
        poiBus: Boolean = false,
        poiPark: Boolean = false,
        photos: List<PhotoEntity> = emptyList()
    ): PropertyWithPhotosEntity {
        return PropertyWithPhotosEntity(
            propertyEntity = PropertyEntity(
                id = id,
                type = type,
                price = price,
                address = address,
                city = city,
                state = state,
                zipCode = zipCode,
                country = country,
                surface = surface,
                lat = lat,
                lng = lng,
                description = description,
                room = room,
                bedroom = bedroom,
                bathroom = bathroom,
                agent = agent,
                propertySold = propertySold,
                propertyOnSaleSince = propertyOnSaleSince,
                propertyDateOfSale = propertyDateOfSale,
                poiTrain = poiTrain,
                poiAirport = poiAirport,
                poiResto = poiResto,
                poiSchool = poiSchool,
                poiBus = poiBus,
                poiPark = poiPark
            ),
            photos = photos
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
        poiPark: Boolean = false
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
            poiPark = poiPark
        )
    }
}