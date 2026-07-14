package com.tuto.realestatemanager.ui.detailproperty

import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.domain.usecase.currentproperty.CurrentIdFlowUseCase
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetPropertyWithPhotosByIdUseCase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.ui.detail.DetailPropertyViewModel
import com.tuto.realestatemanager.ui.detail.DetailViewAction
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailPropertyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val currentIdFlowUseCase: CurrentIdFlowUseCase = mockk()
    private val isDollarFlowUseCase: IsDollarFlowUseCase = mockk()

    private val getPropertyWithPhotosByIdUseCase:
            GetPropertyWithPhotosByIdUseCase = mockk()

    private val isInternetAvailableUseCase:
            IsInternetAvailableUseCase = mockk()

    private val currentIdFlow = MutableStateFlow<Long?>(null)
    private val isDollarFlow = MutableStateFlow(true)
    private val internetFlow = MutableStateFlow(true)

    private lateinit var viewModel: DetailPropertyViewModel

    @Before
    fun setUp() {
        currentIdFlow.value = null
        isDollarFlow.value = true
        internetFlow.value = true

        every {
            currentIdFlowUseCase.invoke()
        } returns currentIdFlow

        every {
            isDollarFlowUseCase.invoke()
        } returns isDollarFlow

        every {
            isInternetAvailableUseCase.invoke()
        } returns internetFlow

        every {
            getPropertyWithPhotosByIdUseCase.invoke(1L)
        } returns flowOf(
            createPropertyWithPhotos(id = 1L)
        )

        viewModel = DetailPropertyViewModel(
            currentIdFlowUseCase = currentIdFlowUseCase,
            isDollarFlowUseCase = isDollarFlowUseCase,
            getPropertyWithPhotosByIdUseCase =
                getPropertyWithPhotosByIdUseCase,
            isInternetAvailableUseCase =
                isInternetAvailableUseCase
        )
    }

    @Test
    fun `detailPropertyStateFlow should return null when current id is null`() =
        runTest {
            currentIdFlow.value = null

            val result = viewModel.detailPropertyStateFlow.first()

            assertNull(result)
        }

    @Test
    fun `detailPropertyStateFlow should expose property detail`() = runTest {
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyStateFlow.first { state ->
            state?.id == 1L
        }

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
    fun `detailPropertyStateFlow should expose first photo uri by default`() =
        runTest {
            currentIdFlow.value = 1L

            val result = viewModel.detailPropertyStateFlow.first { state ->
                state?.photoUri == "uri_1"
            }

            assertEquals("uri_1", result?.photoUri)
        }

    @Test
    fun `setUri should update displayed photo uri`() = runTest {
        currentIdFlow.value = 1L

        viewModel.detailPropertyStateFlow.first { state ->
            state?.id == 1L
        }

        val updatedState = async(
            start = CoroutineStart.UNDISPATCHED
        ) {
            viewModel.detailPropertyStateFlow.first { state ->
                state?.photoUri == "custom_uri"
            }
        }

        viewModel.setUri("custom_uri")

        assertEquals(
            "custom_uri",
            updatedState.await()?.photoUri
        )
    }

    @Test
    fun `selectedPhotoUri should expose selected uri`() {
        viewModel.setUri("selected_uri")

        assertEquals(
            "selected_uri",
            viewModel.selectedPhotoUri.value
        )
    }

    @Test
    fun `detailPropertyStateFlow should display dollar price when dollar mode is enabled`() =
        runTest {
            isDollarFlow.value = true
            currentIdFlow.value = 1L

            val result = viewModel.detailPropertyStateFlow.first { state ->
                state?.id == 1L
            }

            assertEquals(
                "300000",
                result?.price?.filter { character ->
                    character.isDigit()
                }
            )
            assertTrue(result?.price?.endsWith("$") == true)
        }

    @Test
    fun `detailPropertyStateFlow should display euro price when dollar mode is disabled`() =
        runTest {
            isDollarFlow.value = false
            currentIdFlow.value = 1L

            val result = viewModel.detailPropertyStateFlow.first { state ->
                state?.price?.endsWith("€") == true
            }

            assertTrue(result?.price?.endsWith("€") == true)
        }

    @Test
    fun `detailPropertyStateFlow should expose internet state`() = runTest {
        internetFlow.value = false
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyStateFlow.first { state ->
            state?.id == 1L && !state.hasInternet
        }

        assertEquals(false, result?.hasInternet)
    }

    @Test
    fun `detailPropertyStateFlow should expose not yet sold date when property is not sold`() =
        runTest {
            currentIdFlow.value = 1L

            val result = viewModel.detailPropertyStateFlow.first { state ->
                state?.id == 1L
            }

            assertEquals(false, result?.isSold)
            assertEquals("Not yet sold", result?.saleDate)
        }

    @Test
    fun `onNavigateToEditActivity should emit navigation event`() = runTest {
        val event = async(
            start = CoroutineStart.UNDISPATCHED
        ) {
            viewModel.viewAction.first()
        }

        viewModel.onNavigateToEditActivity()

        assertEquals(
            DetailViewAction.NavigateToEditActivity,
            event.await()
        )
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