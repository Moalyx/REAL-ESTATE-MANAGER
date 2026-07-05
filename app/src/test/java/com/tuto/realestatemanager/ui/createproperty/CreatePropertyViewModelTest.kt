package com.tuto.realestatemanager.ui.createproperty

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.domain.autocomplete.GetPredictionsUseCase
import com.tuto.realestatemanager.domain.autocomplete.model.PredictionAddressEntity
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.photo.InsertPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.GetTemporaryPhotoListUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.getOrAwaitValue
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreatePropertyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase = mockk()
    private val getPredictionsUseCase: GetPredictionsUseCase = mockk()
    private val propertyRepository: PropertyRepository = mockk()
    private val getTemporaryPhotoListUseCase: GetTemporaryPhotoListUseCase = mockk()
    private val coroutineDispatchersProvider: CoroutineDispatchersProvider = mockk()
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase = mockk(relaxed = true)
    private val insertPhotoUseCase: InsertPhotoUseCase = mockk(relaxed = true)
    private val deleteTemporaryPhotoUseCase: DeleteTemporaryPhotoUseCase = mockk(relaxed = true)
    private val getUserLocationFlowUseCase: GetUserLocationFlowUseCase = mockk()
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase = mockk()

    private val temporaryPhotoFlow = MutableStateFlow<List<TemporaryPhoto>>(emptyList())
    private val userLocationFlow = MutableStateFlow<Location?>(null)
    private val internetFlow = MutableStateFlow(true)

    private lateinit var viewModel: CreatePropertyViewModel

    @Before
    fun setUp() {
        every { coroutineDispatchersProvider.io } returns UnconfinedTestDispatcher()

        every { getTemporaryPhotoListUseCase.invoke() } returns temporaryPhotoFlow
        every { getUserLocationFlowUseCase.invoke() } returns userLocationFlow
        every { isInternetAvailableUseCase.invoke() } returns internetFlow

        viewModel = CreatePropertyViewModel(
            getPlaceAddressComponentsUseCase = getPlaceAddressComponentsUseCase,
            getPredictionsUseCase = getPredictionsUseCase,
            propertyRepository = propertyRepository,
            getTemporaryPhotoListUseCase = getTemporaryPhotoListUseCase,
            coroutineDispatchersProvider = coroutineDispatchersProvider,
            onDeleteTemporaryPhotoUseCase = onDeleteTemporaryPhotoUseCase,
            insertPhotoUseCase = insertPhotoUseCase,
            deleteTemporaryPhotoUseCase = deleteTemporaryPhotoUseCase,
            getUserLocationFlowUseCase = getUserLocationFlowUseCase,
            isInternetAvailableUseCase = isInternetAvailableUseCase
        )
    }

    @Test
    fun `createProperty should insert property`() = runTest {
        coEvery { propertyRepository.insertProperty(any()) } returns 42L

        viewModel.createProperty(
            type = "House",
            price = 300000,
            address = "10 rue test",
            city = "Paris",
            state = "France",
            zipcode = 75000,
            country = "France",
            surface = 80,
            lat = 48.8566,
            lng = 2.3522,
            description = "description",
            room = 4,
            bedroom = 2,
            bathroom = 1,
            agent = "Agent",
            isSold = false,
            poiTrain = true,
            poiAirport = false,
            poiResto = true,
            poiSchool = false,
            poiBus = true,
            poiPark = false
        )

        coVerify(exactly = 1) {
            propertyRepository.insertProperty(
                match<PropertyEntity> {
                    it.type == "House" &&
                            it.price == 300000 &&
                            it.address == "10 rue test" &&
                            it.city == "Paris" &&
                            it.zipCode == 75000 &&
                            !it.propertySold &&
                            it.poiTrain &&
                            it.poiResto &&
                            it.poiBus
                }
            )
        }
    }

    @Test
    fun `createProperty should insert temporary photos with new property id`() = runTest {
        temporaryPhotoFlow.value = listOf(
            TemporaryPhoto(title = "Salon", uri = "uri_1"),
            TemporaryPhoto(title = "Cuisine", uri = "uri_2")
        )

        coEvery { propertyRepository.insertProperty(any()) } returns 42L

        viewModel.createProperty(
            type = "House",
            price = 300000,
            address = "10 rue test",
            city = "Paris",
            state = "France",
            zipcode = 75000,
            country = "France",
            surface = 80,
            lat = 48.8566,
            lng = 2.3522,
            description = "description",
            room = 4,
            bedroom = 2,
            bathroom = 1,
            agent = "Agent",
            isSold = false,
            poiTrain = false,
            poiAirport = false,
            poiResto = false,
            poiSchool = false,
            poiBus = false,
            poiPark = false
        )

        coVerify {
            insertPhotoUseCase.invoke(
                PhotoEntity(
                    propertyId = 42L,
                    photoUri = "uri_1",
                    photoTitle = "Salon"
                )
            )
        }

        coVerify {
            insertPhotoUseCase.invoke(
                PhotoEntity(
                    propertyId = 42L,
                    photoUri = "uri_2",
                    photoTitle = "Cuisine"
                )
            )
        }
    }

    @Test
    fun `createProperty should clear temporary photos after creation`() = runTest {
        temporaryPhotoFlow.value = listOf(
            TemporaryPhoto(title = "Salon", uri = "uri_1")
        )

        coEvery { propertyRepository.insertProperty(any()) } returns 42L

        viewModel.createProperty(
            type = "House",
            price = 300000,
            address = "10 rue test",
            city = "Paris",
            state = "France",
            zipcode = 75000,
            country = "France",
            surface = 80,
            lat = 48.8566,
            lng = 2.3522,
            description = "description",
            room = 4,
            bedroom = 2,
            bathroom = 1,
            agent = "Agent",
            isSold = false,
            poiTrain = false,
            poiAirport = false,
            poiResto = false,
            poiSchool = false,
            poiBus = false,
            poiPark = false
        )

        verify {
            onDeleteTemporaryPhotoUseCase.invoke()
        }
    }

    @Test
    fun `temporaryPhotoLiveData should expose temporary photos`() = runTest {
        temporaryPhotoFlow.value = listOf(
            TemporaryPhoto(title = "Photo 1", uri = "uri_1"),
            TemporaryPhoto(title = "Photo 2", uri = "uri_2")
        )

        val result = viewModel.temporaryPhotoLiveData.getOrAwaitValue()

        assertEquals(2, result.size)
        assertEquals("Photo 1", result[0].title)
        assertEquals("uri_1", result[0].uri)
    }

    @Test
    fun `deleteTemporaryPhoto should call use case`() {
        val temporaryPhoto = TemporaryPhoto(
            title = "Photo",
            uri = "uri"
        )

        viewModel.deleteTemporaryPhoto(temporaryPhoto)

        verify {
            deleteTemporaryPhotoUseCase.invoke(temporaryPhoto)
        }
    }

    @Test
    fun `onNavigateToMainActivity should emit navigation event`() {
        viewModel.onNavigateToMainActivity()

        assertEquals(
            CreateViewAction.NavigateToMainActivity,
            viewModel.navigateSingleLiveEvent.getOrAwaitValue()
        )
    }

    @Test
    fun `hasInternetLiveData should expose internet state`() {
        internetFlow.value = false

        val result = viewModel.hasInternetLiveData.getOrAwaitValue()

        assertEquals(false, result)
    }

    @Test
    fun `onAddressSearchChanged should expose prediction list`() = runTest {
        val location: Location = mockk()
        every { location.latitude } returns 48.8566
        every { location.longitude } returns 2.3522

        userLocationFlow.value = location

        coEvery {
            getPredictionsUseCase.invoke("Par", "48.8566,2.3522")
        } returns listOf(
            PredictionAddressEntity(
                prediction = "Paris",
                placeId = "place_id_1"
            )
        )

        viewModel.onAddressSearchChanged("Par")

        val result = viewModel.predictionListViewState.getOrAwaitValue()

        assertEquals(1, result.size)
        assertEquals("Paris", result[0].address)
        assertEquals("place_id_1", result[0].id)
    }

    @Test
    fun `onSetAutocompleteAddressId should expose place detail`() = runTest {
        coEvery {
            getPlaceAddressComponentsUseCase.invoke("place_id_1")
        } returns AddressComponentsEntity(
            streetNumber = "10",
            fullAddress = "10 rue test",
            city = "Paris",
            state = "France",
            zipCode = "75000",
            country = "France",
            lat = 48.8566,
            lng = 2.3522
        )

        viewModel.onSetAutocompleteAddressId("place_id_1")

        val result = viewModel.placeDetailViewState.getOrAwaitValue()

        assertEquals("10", result.number)
        assertEquals("10 rue test", result.address)
        assertEquals("Paris", result.city)
        assertEquals("75000", result.zipCode)
    }
}