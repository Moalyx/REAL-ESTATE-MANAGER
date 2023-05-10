package com.tuto.realestatemanager.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuto.realestatemanager.TestCoroutineRule
import com.tuto.realestatemanager.data.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyViewModel
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset


class CreatePropertyViewModelTest {

    companion object {
        //FOR PROPERTYREPOSITORY
        private const val PROPERTY_ID = 0L
        private const val TYPE = "type"
        private const val PRICE = 0
        private const val ADDRESS = "ADDRESS"
        private const val CITY = "CITY"
        private const val STATE = "STATE"
        private const val ZIPCODE = 75000
        private const val COUNTRY = "COUNTRY"
        private const val SURFACE = 100
        private const val LAT = 48.0
        private const val LNG = 20.0
        private const val DESCRIPTION = "DESCRIPTION"
        private const val ROOM = 2
        private const val BEDROOM = 2
        private const val BATHROOM = 2
        private const val AGENT = "AGENT"
        private const val IS_PROPERTY_SOLD = true
        private const val SALESINCE = "2023-05-10"
        private const val SOLD_AT = "estate available for sale"
        private const val POITRAIN = true
        private const val POIAIRPORT = true
        private const val POIRESTO = true
        private const val POISCHOOL = true
        private const val POIBUS = true
        private const val POIPARK = true

        private const val NEW_PROPERTY_ID = 42L

        //FOR PLACEDETAIL
        private const val PLACE_ID = "0"

        //FOR PHOTOREPOSITORY
        private const val PHOTO_ID = 0
        private const val PHOTO_URI = "PHOTO_URI"
        private const val PHOTO_TITLE = "PHOTO_TITLE"

        //FOR AUTOCOMPLETEREPOSITORY
        private const val INPUT_AUTOCOMPLETE = "3"
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val temporaryPhotosMutableStateFlow = MutableStateFlow<List<TemporaryPhoto>>(emptyList())

    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase = mockk()
    private val propertyRepository: PropertyRepository = mockk()
    private val photoRepository: PhotoRepository = mockk()
    private val autocompleteRepository: AutocompleteRepository = mockk()
    private val temporaryPhotoRepository: TemporaryPhotoRepository = mockk()

    private lateinit var createPropertyViewModel: CreatePropertyViewModel

    @Before
    fun setUp() {

        every { temporaryPhotoRepository.getTemporaryPhotoList() } returns temporaryPhotosMutableStateFlow

        createPropertyViewModel = CreatePropertyViewModel(
            getPlaceAddressComponentsUseCase = getPlaceAddressComponentsUseCase,
            propertyRepository = propertyRepository,
            photoRepository = photoRepository,
            autocompleteRepository = autocompleteRepository,
            clock = Clock.fixed(
                Instant.ofEpochSecond(1683743909), // 10/05/2023 - 20:38:29
                ZoneOffset.UTC,
            ),
            coroutineDispatchersProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
            temporaryPhotoRepository = temporaryPhotoRepository,
        )
//
//
//        coEvery { getPlaceAddressComponentsUseCase.invoke(PLACE_ID) } returns
//            AddressComponentsEntity(
//                "12",
//                ADDRESS,
//                CITY,
//                STATE,
//                "75000",
//                COUNTRY,
//                LAT,
//                LNG
//            )
//
//        coEvery {
//            photoRepository.insertPhoto(
//                PhotoEntity(
//                    PHOTO_ID.toLong(),
//                    PROPERTY_ID,
//                    PHOTO_URI,
//                    PHOTO_TITLE
//                )
//            )
//        }
//
//        coEvery { autocompleteRepository.getAutocompleteResult(INPUT_AUTOCOMPLETE) } returns PredictionResponse()
    }

    @Test
    fun nominal_case() = testCoroutineRule.runTest {
        // Given
        val propertyEntity = PropertyEntity(
            id = PROPERTY_ID,
            type = TYPE,
            price = PRICE,
            address = ADDRESS,
            city = CITY,
            state = STATE,
            zipCode = ZIPCODE,
            country = COUNTRY,
            surface = SURFACE,
            lat = LAT,
            lng = LNG,
            description = DESCRIPTION,
            room = ROOM,
            bedroom = BEDROOM,
            bathroom = BATHROOM,
            agent = AGENT,
            propertySold = IS_PROPERTY_SOLD,
            propertyOnSaleSince = SOLD_AT,
            propertyDateOfSale = SALESINCE,
            poiTrain = POITRAIN,
            poiAirport = POIAIRPORT,
            poiResto = POIRESTO,
            poiSchool = POISCHOOL,
            poiBus = POIBUS,
            poiPark = POIPARK
        )
        coEvery { propertyRepository.insertProperty(propertyEntity) } returns NEW_PROPERTY_ID

        // When
        createPropertyViewModel.createProperty(
            type = TYPE,
            price = PRICE,
            address = ADDRESS,
            city = CITY,
            state = STATE,
            zipcode = ZIPCODE,
            country = COUNTRY,
            surface = SURFACE,
            lat = LAT,
            lng = LNG,
            description = DESCRIPTION,
            room = ROOM,
            bedroom = BEDROOM,
            bathroom = BATHROOM,
            agent = AGENT,
            isSold = IS_PROPERTY_SOLD,
            poiTrain = POITRAIN,
            poiAirport = POIAIRPORT,
            poiResto = POIRESTO,
            poiSchool = POISCHOOL,
            poiBus = POIBUS,
            poiPark = POIPARK,
        )
        runCurrent()

        // Then
        coVerify(exactly = 1) {
            propertyRepository.insertProperty(propertyEntity)
        }
        confirmVerified(propertyRepository)
    }
}