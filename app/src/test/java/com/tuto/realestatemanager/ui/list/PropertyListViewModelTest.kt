package com.tuto.realestatemanager.ui.list

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.geocode.GetLatLngPropertyLocationUseCase
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetAllPropertiesWithPhotosUseCase
import com.tuto.realestatemanager.domain.usecase.property.UpdatePropertyUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.getOrAwaitValue
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.DecimalFormat

@OptIn(ExperimentalCoroutinesApi::class)
class PropertyListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: Application = mockk(relaxed = true)
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase = mockk()
    private val getAllPropertiesWithPhotosUseCase: GetAllPropertiesWithPhotosUseCase = mockk()
    private val updatePropertyUseCase: UpdatePropertyUseCase = mockk(relaxed = true)
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase = mockk(relaxed = true)
    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk(relaxed = true)
    private val isDollarFlowUseCase: IsDollarFlowUseCase = mockk()
    private val getParametersFlowUseCase: GetParametersFlowUseCase = mockk()
    private val getLatLngPropertyLocationUseCase: GetLatLngPropertyLocationUseCase = mockk(relaxed = true)

    private val propertiesFlow = MutableStateFlow<List<PropertyWithPhotosEntity>>(emptyList())
    private val searchParametersFlow = MutableStateFlow<SearchParameters?>(null)
    private val isDollarFlow = MutableStateFlow(true)
    private val internetFlow = MutableStateFlow(true)
    private val currentIdFlow = MutableStateFlow<Long?>(null)

    private lateinit var viewModel: PropertyListViewModel

    @Before
    fun setUp() {
        every { getAllPropertiesWithPhotosUseCase.invoke() } returns propertiesFlow
        every { getParametersFlowUseCase.invoke() } returns searchParametersFlow
        every { isDollarFlowUseCase.invoke() } returns isDollarFlow
        every { isInternetAvailableUseCase.invoke() } returns internetFlow
        every { currentPropertyIdRepository.currentIdFlow } returns currentIdFlow

        viewModel = PropertyListViewModel(
            mainApplication = application,
            isInternetAvailableUseCase = isInternetAvailableUseCase,
            getAllPropertiesWithPhotosUseCase = getAllPropertiesWithPhotosUseCase,
            updatePropertyUseCase = updatePropertyUseCase,
            onDeleteTemporaryPhotoUseCase = onDeleteTemporaryPhotoUseCase,
            currentPropertyIdRepository = currentPropertyIdRepository,
            isDollarFlowUseCase = isDollarFlowUseCase,
            getParametersFlowUseCase = getParametersFlowUseCase,
            getLatLngPropertyLocationUseCase = getLatLngPropertyLocationUseCase
        )
    }

    @Test
    fun `nominal case - should expose all properties when no search parameters`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, type = "House", city = "Paris", price = 300000),
            createProperty(id = 2L, type = "Flat", city = "Lyon", price = 200000)
        )

        val result = viewModel.propertyListLiveData.getOrAwaitValue()

        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals(2L, result[1].id)
    }

    @Test
    fun `should filter properties by price`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, price = 100000),
            createProperty(id = 2L, price = 300000)
        )

        searchParametersFlow.value = createSearchParameters(
            priceMinimum = 200000,
            priceMaximum = 400000
        )

        val result = viewModel.propertyListLiveData.getOrAwaitValue()

        assertEquals(1, result.size)
        assertEquals(2L, result.first().id)
    }

    @Test
    fun `should filter properties by city ignoring case`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, city = "Marseille"),
            createProperty(id = 2L, city = "Paris")
        )

        searchParametersFlow.value = createSearchParameters(city = "marseille")

        val result = viewModel.propertyListLiveData.getOrAwaitValue()

        assertEquals(1, result.size)
        assertEquals(1L, result.first().id)
    }

    @Test
    fun `should filter properties by type`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, type = "House"),
            createProperty(id = 2L, type = "Flat")
        )

        searchParametersFlow.value = createSearchParameters(type = "Flat")

        val result = viewModel.propertyListLiveData.getOrAwaitValue()

        assertEquals(1, result.size)
        assertEquals(2L, result.first().id)
    }

    @Test
    fun `should filter properties by train poi`() = runTest {
        propertiesFlow.value = listOf(
            createProperty(id = 1L, poiTrain = false),
            createProperty(id = 2L, poiTrain = true)
        )

        searchParametersFlow.value = createSearchParameters(poiTrain = true)

        val result = viewModel.propertyListLiveData.getOrAwaitValue()

        assertEquals(1, result.size)
        assertEquals(2L, result.first().id)
    }

    @Test
    fun `should display dollar price when dollar mode is enabled`() = runTest {
        isDollarFlow.value = true
        propertiesFlow.value = listOf(createProperty(price = 300000))

        val result = viewModel.propertyListLiveData.getOrAwaitValue()
        val expected = DecimalFormat("#,###.#").format(300000) + " $"

        assertEquals(expected, result.first().price)

        assertEquals(expected, result.first().price)
    }

    @Test
    fun `should navigate to detail when item clicked in phone mode`() = runTest {
        propertiesFlow.value = listOf(createProperty(id = 1L))

        val result = viewModel.propertyListLiveData.getOrAwaitValue()

        result.first().onItemClicked.invoke()

        assertEquals(
            ListViewAction.NavigateToDetailActivity,
            viewModel.navigateSingleLiveEvent.getOrAwaitValue()
        )

        verify {
            currentPropertyIdRepository.setCurrentId(1L)
        }
    }

    @Test
    fun `should not navigate to detail when item clicked in tablet mode`() = runTest {
        viewModel.onConfigurationChanged(true)

        propertiesFlow.value = listOf(createProperty(id = 1L))

        val result = viewModel.propertyListLiveData.getOrAwaitValue()

        result.first().onItemClicked.invoke()

        verify {
            currentPropertyIdRepository.setCurrentId(1L)
        }

        assertNull(viewModel.navigateSingleLiveEvent.value)
    }

    @Test
    fun `tablet mode should select first filtered property`() = runTest {
        viewModel.onConfigurationChanged(true)

        propertiesFlow.value = listOf(
            createProperty(id = 1L, city = "Paris"),
            createProperty(id = 2L, city = "Lyon")
        )

        searchParametersFlow.value = createSearchParameters(city = "Lyon")

        viewModel.propertyListLiveData.getOrAwaitValue()

        verify {
            currentPropertyIdRepository.setCurrentId(2L)
        }
    }

    @Test
    fun `onNavigateToCreateActivity should emit create navigation event`() {
        viewModel.onNavigateToCreateActivity()

        assertEquals(
            ListViewAction.NavigateToCreateActvity,
            viewModel.navigateSingleLiveEvent.getOrAwaitValue()
        )
    }

    @Test
    fun `onDeleteTemporaryPhotoRepository should call use case`() {
        viewModel.onDeleteTemporaryPhotoRepository()

        verify {
            onDeleteTemporaryPhotoUseCase.invoke()
        }
    }

    private fun createProperty(
        id: Long = 1L,
        type: String = "House",
        price: Int = 300000,
        address: String = "10 rue test",
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