package com.tuto.realestatemanager.ui.editproperty

import com.tuto.realestatemanager.MainDispatcherRule
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.domain.autocomplete.GetPredictionsUseCase
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeletePhotoByIdUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.photo.InsertPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.GetTemporaryPhotoListUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.utils.Utils
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditPropertyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val propertyRepository: PropertyRepository = mockk(relaxed = true)
    private val photoRepository: PhotoRepository = mockk()
    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk(relaxed = true)
    private val deletePhotoByIdUseCase: DeletePhotoByIdUseCase = mockk(relaxed = true)
    private val insertPhotoUseCase: InsertPhotoUseCase = mockk(relaxed = true)
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase = mockk(relaxed = true)
    private val getTemporaryPhotoListUseCase: GetTemporaryPhotoListUseCase = mockk()
    private val deleteTemporaryPhotoUseCase: DeleteTemporaryPhotoUseCase = mockk(relaxed = true)
    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase = mockk()
    private val getPredictionsUseCase: GetPredictionsUseCase = mockk()
    private val getUserLocationFlowUseCase: GetUserLocationFlowUseCase = mockk()
    private val coroutineDispatchersProvider: CoroutineDispatchersProvider = mockk()

    private val currentIdFlow = MutableStateFlow<Long?>(null)
    private val photoFlow = MutableStateFlow<List<PhotoEntity>>(emptyList())
    private val temporaryPhotoFlow = MutableStateFlow<List<TemporaryPhoto>>(emptyList())

    private lateinit var viewModel: EditPropertyViewModel

    @Before
    fun setUp() {
        currentIdFlow.value = null
        photoFlow.value = emptyList()
        temporaryPhotoFlow.value = emptyList()

        every {
            currentPropertyIdRepository.currentIdFlow
        } returns currentIdFlow

        every {
            photoRepository.getAllPhoto()
        } returns photoFlow

        every {
            getTemporaryPhotoListUseCase.invoke()
        } returns temporaryPhotoFlow

        every {
            propertyRepository.getPropertyById(1L)
        } returns flowOf(createProperty(id = 1L))

        every {
            getUserLocationFlowUseCase.invoke()
        } returns flowOf(null)

        every {
            coroutineDispatchersProvider.io
        } returns testDispatcher

        viewModel = EditPropertyViewModel(
            propertyRepository = propertyRepository,
            photoRepository = photoRepository,
            currentPropertyIdRepository = currentPropertyIdRepository,
            deletePhotoByIdUseCase = deletePhotoByIdUseCase,
            insertPhotoUseCase = insertPhotoUseCase,
            onDeleteTemporaryPhotoUseCase = onDeleteTemporaryPhotoUseCase,
            getTemporaryPhotoListUseCase = getTemporaryPhotoListUseCase,
            deleteTemporaryPhotoUseCase = deleteTemporaryPhotoUseCase,
            getPlaceAddressComponentsUseCase = getPlaceAddressComponentsUseCase,
            getPredictionsUseCase = getPredictionsUseCase,
            getUserLocationFlowUseCase = getUserLocationFlowUseCase,
            coroutineDispatchersProvider = coroutineDispatchersProvider
        )
    }

    @Test
    fun `setPropertyId should update current property id`() {
        viewModel.setPropertyId(1L)

        verify {
            currentPropertyIdRepository.setCurrentId(1L)
        }
    }

    @Test
    fun `detailPropertyStateFlow should expose property detail`() = runTest {
        currentIdFlow.value = 1L

        val result = viewModel.detailPropertyStateFlow.first { state ->
            state?.id == 1L
        }!!

        assertEquals(1L, result.id)
        assertEquals("House", result.type)
        assertEquals(300000, result.price)
        assertEquals("10 rue test", result.address)
        assertEquals("Paris", result.city)
        assertEquals(75000, result.zipcode)
        assertEquals("France", result.country)
        assertEquals(80, result.surface)
        assertEquals("description", result.description)
        assertEquals("Agent", result.agent)
        assertEquals(4, result.room)
        assertEquals(1, result.bathroom)
        assertEquals(2, result.bedroom)
        assertEquals("01/01/2024", result.saleSince)
        assertEquals("Not yet sold", result.dateOfSale)
        assertFalse(result.isSold)
    }

    @Test
    fun `allPhotoStateFlow should expose saved photos for current property only`() = runTest {
        currentIdFlow.value = 1L

        photoFlow.value = listOf(
            createPhoto(id = 1L, propertyId = 1L, photoUri = "uri_1", photoTitle = "Salon"),
            createPhoto(id = 2L, propertyId = 1L, photoUri = "uri_2", photoTitle = "Cuisine"),
            createPhoto(id = 3L, propertyId = 2L, photoUri = "uri_3", photoTitle = "Other")
        )

        val result = viewModel.allPhotoStateFlow.first { photos ->
            photos.size == 2
        }

        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("Salon", result[0].photoTitle)
        assertEquals("uri_1", result[0].photoUri)
        assertFalse(result[0].isTemporary)
    }

    @Test
    fun `allPhotoStateFlow should expose temporary photos`() = runTest {
        currentIdFlow.value = 1L

        temporaryPhotoFlow.value = listOf(
            TemporaryPhoto(id = 10L, title = "Temp Salon", uri = "temp_uri_1"),
            TemporaryPhoto(id = 11L, title = "Temp Cuisine", uri = "temp_uri_2")
        )

        val result = viewModel.allPhotoStateFlow.first { photos ->
            photos.size == 2
        }

        assertEquals(2, result.size)
        assertEquals(10L, result[0].id)
        assertEquals("Temp Salon", result[0].photoTitle)
        assertEquals("temp_uri_1", result[0].photoUri)
        assertTrue(result[0].isTemporary)
    }

    @Test
    fun `markPhotoAsDeleted should remove saved photo from displayed list`() = runTest {
        currentIdFlow.value = 1L

        photoFlow.value = listOf(
            createPhoto(id = 1L, propertyId = 1L, photoUri = "uri_1", photoTitle = "Salon"),
            createPhoto(id = 2L, propertyId = 1L, photoUri = "uri_2", photoTitle = "Cuisine")
        )

        viewModel.allPhotoStateFlow.first { photos ->
            photos.size == 2
        }

        viewModel.markPhotoAsDeleted(1L)

        val result = viewModel.allPhotoStateFlow.first { photos ->
            photos.size == 1 && photos.first().id == 2L
        }

        assertEquals(1, result.size)
        assertEquals(2L, result.first().id)
    }

    @Test
    fun `onDeleteEditPhoto should delete temporary photo immediately`() {
        val photo = EditPropertyPhotoViewState(
            id = 10L,
            photoTitle = "Temp Salon",
            photoUri = "temp_uri_1",
            isTemporary = true
        )

        viewModel.onDeleteEditPhoto(photo)

        verify {
            deleteTemporaryPhotoUseCase.invoke(
                TemporaryPhoto(
                    id = 10L,
                    title = "Temp Salon",
                    uri = "temp_uri_1"
                )
            )
        }
    }

    @Test
    fun `onDeleteEditPhoto should mark saved photo as deleted`() = runTest {
        currentIdFlow.value = 1L

        photoFlow.value = listOf(
            createPhoto(id = 1L, propertyId = 1L, photoUri = "uri_1", photoTitle = "Salon")
        )

        viewModel.allPhotoStateFlow.first { photos ->
            photos.size == 1
        }

        val photo = EditPropertyPhotoViewState(
            id = 1L,
            photoTitle = "Salon",
            photoUri = "uri_1",
            isTemporary = false
        )

        viewModel.onDeleteEditPhoto(photo)

        val result = viewModel.allPhotoStateFlow.first { photos ->
            photos.isEmpty()
        }

        assertTrue(result.isEmpty())
    }

    @Test
    fun `updateProperty should update property`() = runTest {
        updatePropertyThroughViewModel(
            type = "Flat",
            price = 250000,
            address = "20 rue update",
            city = "Lyon",
            zipcode = 69000,
            surface = 60,
            lat = 45.7640,
            lng = 4.8357,
            description = "updated description",
            room = 3,
            bedroom = 1,
            agent = "Updated Agent"
        )


        coVerify {
            propertyRepository.updateProperty(
                match<PropertyEntity> {
                    it.id == 1L &&
                            it.type == "Flat" &&
                            it.price == 250000 &&
                            it.address == "20 rue update" &&
                            it.city == "Lyon" &&
                            it.zipCode == 69000 &&
                            !it.propertySold &&
                            it.propertyDateOfSale == "Not yet sold"
                }
            )
        }
    }

    @Test
    fun `updateProperty should accept null location`() = runTest {
        updatePropertyThroughViewModel(
            lat = null,
            lng = null
        )


        coVerify {
            propertyRepository.updateProperty(
                match<PropertyEntity> {
                    it.lat == null && it.lng == null
                }
            )
        }
    }

    @Test
    fun `updateProperty should set sale date to today when property becomes sold`() = runTest {
        updatePropertyThroughViewModel(
            isSold = true,
            wasSold = false,
            previousDateOfSale = "Not yet sold"
        )


        coVerify {
            propertyRepository.updateProperty(
                match<PropertyEntity> {
                    it.propertySold &&
                            it.propertyDateOfSale == Utils.todayDate()
                }
            )
        }
    }

    @Test
    fun `updateProperty should keep previous sale date when property was already sold`() = runTest {
        updatePropertyThroughViewModel(
            isSold = true,
            wasSold = true,
            previousDateOfSale = "10/06/2026"
        )


        coVerify {
            propertyRepository.updateProperty(
                match<PropertyEntity> {
                    it.propertySold &&
                            it.propertyDateOfSale == "10/06/2026"
                }
            )
        }
    }

    @Test
    fun `updateProperty should set date of sale to not yet sold when property is available`() = runTest {
        updatePropertyThroughViewModel(
            isSold = false,
            wasSold = true,
            previousDateOfSale = "10/06/2026"
        )


        coVerify {
            propertyRepository.updateProperty(
                match<PropertyEntity> {
                    !it.propertySold &&
                            it.propertyDateOfSale == "Not yet sold"
                }
            )
        }
    }

    @Test
    fun `updateProperty should delete marked saved photos`() = runTest {
        viewModel.markPhotoAsDeleted(1L)
        viewModel.markPhotoAsDeleted(2L)

        updatePropertyThroughViewModel()


        coVerify {
            deletePhotoByIdUseCase.invoke(1L)
            deletePhotoByIdUseCase.invoke(2L)
        }
    }

    @Test
    fun `updateProperty should insert temporary photos`() = runTest {
        temporaryPhotoFlow.value = listOf(
            TemporaryPhoto(id = 10L, title = "Temp Salon", uri = "temp_uri_1"),
            TemporaryPhoto(id = 11L, title = "Temp Cuisine", uri = "temp_uri_2")
        )

        updatePropertyThroughViewModel()


        coVerify {
            insertPhotoUseCase.invoke(
                PhotoEntity(
                    propertyId = 1L,
                    photoUri = "temp_uri_1",
                    photoTitle = "Temp Salon"
                )
            )
        }

        coVerify {
            insertPhotoUseCase.invoke(
                PhotoEntity(
                    propertyId = 1L,
                    photoUri = "temp_uri_2",
                    photoTitle = "Temp Cuisine"
                )
            )
        }
    }

    @Test
    fun `updateProperty should clear temporary photos`() = runTest {
        updatePropertyThroughViewModel()


        verify {
            onDeleteTemporaryPhotoUseCase.invoke()
        }
    }

    @Test
    fun `updateProperty should emit navigation event after update`() = runTest {
        val event = async(
            start = CoroutineStart.UNDISPATCHED
        ) {
            viewModel.viewAction.first()
        }

        updatePropertyThroughViewModel()


        assertEquals(
            EditViewAction.NavigateFromEditToDetailActivity,
            event.await()
        )
    }

    @Test
    fun `clearTemporaryPhotos should call use case`() {
        viewModel.clearTemporaryPhotos()

        verify {
            onDeleteTemporaryPhotoUseCase.invoke()
        }
    }

    private fun updatePropertyThroughViewModel(
        id: Long = 1L,
        type: String = "House",
        price: Int = 300000,
        address: String = "10 rue test",
        city: String = "Paris",
        state: String = "France",
        zipcode: Int = 75000,
        country: String = "France",
        surface: Int = 80,
        lat: Double? = 48.8566,
        lng: Double? = 2.3522,
        description: String = "description",
        room: Int = 4,
        bedroom: Int = 2,
        bathroom: Int = 1,
        agent: String = "Agent",
        isSold: Boolean = false,
        wasSold: Boolean = false,
        previousDateOfSale: String = "Not yet sold",
        poiTrain: Boolean = false,
        saleSince: String = "01/01/2024",
        poiAirport: Boolean = false,
        poiResto: Boolean = false,
        poiSchool: Boolean = false,
        poiBus: Boolean = false,
        poiPark: Boolean = false
    ) {
        viewModel.updateProperty(
            id = id,
            type = type,
            price = price,
            address = address,
            city = city,
            state = state,
            zipcode = zipcode,
            country = country,
            surface = surface,
            lat = lat,
            lng = lng,
            description = description,
            room = room,
            bedroom = bedroom,
            bathroom = bathroom,
            agent = agent,
            isSold = isSold,
            wasSold = wasSold,
            previousDateOfSale = previousDateOfSale,
            poiTrain = poiTrain,
            saleSince = saleSince,
            poiAirport = poiAirport,
            poiResto = poiResto,
            poiSchool = poiSchool,
            poiBus = poiBus,
            poiPark = poiPark
        )
    }

    private fun createPhoto(
        id: Long = 1L,
        propertyId: Long = 1L,
        photoUri: String = "uri",
        photoTitle: String = "title"
    ): PhotoEntity {
        return PhotoEntity(
            id = id,
            propertyId = propertyId,
            photoUri = photoUri,
            photoTitle = photoTitle
        )
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
        propertyDateOfSale: String = "Not yet sold",
        poiTrain: Boolean = false,
        poiAirport: Boolean = false,
        poiResto: Boolean = false,
        poiSchool: Boolean = false,
        poiBus: Boolean = false,
        poiPark: Boolean = false
    ): PropertyEntity {
        return PropertyEntity(
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
        )
    }
}