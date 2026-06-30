package com.tuto.realestatemanager.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.tuto.realestatemanager.domain.usecase.currentproperty.CurrentIdFlowUseCase
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetPropertyWithPhotosByIdUseCase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailPropertyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val currentIdFlowUseCase: CurrentIdFlowUseCase = mockk()
    private val isDollarFlowUseCase: IsDollarFlowUseCase = mockk()
    private val getPropertyWithPhotosByIdUseCase: GetPropertyWithPhotosByIdUseCase = mockk()
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase = mockk()

    private val currentIdFlow = MutableStateFlow<Long?>(null)
    private val isDollarFlow = MutableStateFlow(true)
    private val internetFlow = MutableStateFlow(true)

    private lateinit var viewModel: DetailPropertyViewModel

    @Before
    fun setUp() {
        every { currentIdFlowUseCase.invoke() } returns currentIdFlow
        every { isDollarFlowUseCase.invoke() } returns isDollarFlow
        every { isInternetAvailableUseCase.invoke() } returns internetFlow

        every { getPropertyWithPhotosByIdUseCase.invoke(1L) } returns flowOf(
            createPropertyWithPhotos(id = 1L)
        )

        viewModel = DetailPropertyViewModel(
            currentIdFlowUseCase = currentIdFlowUseCase,
            isDollarFlowUseCase = isDollarFlowUseCase,
            getPropertyWithPhotosByIdUseCase = getPropertyWithPhotosByIdUseCase,
            isInternetAvailableUseCase = isInternetAvailableUseCase
        )
    }

    @Test
    fun `detailPropertyLiveData should return null when current id is null`() = runTest {
        currentIdFlow.value = null

        val result = viewModel.detailPropertyLiveData.getOrAwaitValue()

        assertNull(result)
    }

    @Test
    fun `detailPropertyLiveData should expose property detail`() = runTest {
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyLiveData.getOrAwaitValue()

        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("House", result?.type)
        assertEquals("10 rue test", result?.address)
        assertEquals("Paris", result?.city)
        assertEquals(75000, result?.zipcode)
        assertEquals("France", result?.country)
        assertEquals(80, result?.surface)
        assertEquals("description", result?.description)
        assertEquals(4, result?.room)
        assertEquals(2, result?.bedroom)
        assertEquals(1, result?.bathroom)
        assertEquals("Agent", result?.agent)
    }

    @Test
    fun `detailPropertyLiveData should expose first photo uri by default`() = runTest {
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyLiveData.getOrAwaitValue()

        assertEquals("uri_1", result?.photoUri)
    }

    @Test
    fun `setUri should update displayed photo uri`() = runTest {
        currentIdFlow.value = 1L

        viewModel.detailPropertyLiveData.observeForTesting {
            viewModel.setUri("custom_uri")

            assertEquals("custom_uri", viewModel.detailPropertyLiveData.value?.photoUri)
        }
    }

    @Test
    fun `getUri should expose selected uri`() = runTest {
        viewModel.setUri("selected_uri")

        val result = viewModel.getUri().getOrAwaitValue()

        assertEquals("selected_uri", result)
    }

    @Test
    fun `detailPropertyLiveData should display dollar price when dollar mode is enabled`() = runTest {
        isDollarFlow.value = true
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyLiveData.getOrAwaitValue()

        assertEquals("300000", result?.price?.filter { it.isDigit() })
        assertTrue(result?.price?.endsWith("$") == true)
    }

    @Test
    fun `detailPropertyLiveData should display euro price when dollar mode is disabled`() = runTest {
        isDollarFlow.value = false
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyLiveData.getOrAwaitValue()

        assertTrue(result?.price?.endsWith("€") == true)
    }

    @Test
    fun `detailPropertyLiveData should expose internet state`() = runTest {
        internetFlow.value = false
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyLiveData.getOrAwaitValue()

        assertEquals(false, result?.hasInternet)
    }

    @Test
    fun `detailPropertyLiveData should expose not yet sold date when property is not sold`() = runTest {
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyLiveData.getOrAwaitValue()

        assertEquals(false, result?.isSold)
        assertEquals("Not yet sold", result?.saleDate)
    }

    @Test
    fun `onNavigateToEditActivity should emit navigation event`() {
        viewModel.onNavigateToEditActivity()

        assertEquals(
            DetailViewAction.NavigateToEditActivity,
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

    private fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
        val observer = Observer<T> { }

        try {
            observeForever(observer)
            block()
        } finally {
            removeObserver(observer)
        }
    }

    private fun createPropertyWithPhotos(
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
        propertyDateOfSale: String = "Not yet sold",
        poiTrain: Boolean = true,
        poiAirport: Boolean = false,
        poiResto: Boolean = true,
        poiSchool: Boolean = false,
        poiBus: Boolean = true,
        poiPark: Boolean = false
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
            photos = listOf(
                PhotoEntity(
                    id = 1L,
                    propertyId = id,
                    photoUri = "uri_1",
                    photoTitle = "Salon"
                ),
                PhotoEntity(
                    id = 2L,
                    propertyId = id,
                    photoUri = "uri_2",
                    photoTitle = "Cuisine"
                )
            )
        )
    }
}