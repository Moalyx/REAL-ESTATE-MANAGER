package com.tuto.realestatemanager.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tuto.realestatemanager.TestCoroutineRule
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule

class CreatePropertyViewModelTest {

    companion object {
        //FOR PROPERTYREPOSITORY
        private const val PROPERTY_ID = 0
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
        private const val SALESINCE = "SALESINCE"
        private const val POITRAIN = true
        private const val POIAIRPORT = true
        private const val POIRESTO = true
        private const val POISCHOOL = true
        private const val POIBUS = true
        private const val POIPARK = true

        //FOR PLACEDETAIL
        private const val PLACE_ID = "0"

        //FOR PHOTOREPOSITORY
        private const val PHOTO_ID = 0
        private const val PHOTO_URI = "PHOTO_URI"
        private const val PHOTO_TITLE = "PHOTO_TITLE"

        //FOR AUTOCOMPLETEREPOSITORY
        private const val INPUT_AUTOCOMPLETE = "3"


    }

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase = mockk()

    val propertyRepository: PropertyRepository = mockk()

    val photoRepository: PhotoRepository = mockk()

    val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk()

    val autocompleteRepository: AutocompleteRepository = mockk()

    val temporaryPhotoRepository: TemporaryPhotoRepository = mockk()

    val context: Application = mockk()

    private val createPropertyViewModel = CreatePropertyViewModel(
        getPlaceAddressComponentsUseCase,
        propertyRepository,
        photoRepository,
        autocompleteRepository,
        temporaryPhotoRepository
    )

    @Before
    fun setUp() {

        coEvery {
            propertyRepository.insertProperty(
                PropertyEntity(
                    PROPERTY_ID.toLong(),
                    TYPE,
                    PRICE,
                    ADDRESS,
                    CITY,
                    STATE,
                    ZIPCODE,
                    COUNTRY,
                    SURFACE,
                    LAT,
                    LNG,
                    DESCRIPTION,
                    ROOM,
                    BEDROOM,
                    BATHROOM,
                    SALESINCE,
                    POITRAIN,
                    POIAIRPORT,
                    POIRESTO,
                    POISCHOOL,
                    POIBUS,
                    POIPARK
                )
            )
        }


        coEvery { getPlaceAddressComponentsUseCase.invoke(PLACE_ID) } returns
                AddressComponentsEntity(
            "12",
            ADDRESS,
            CITY,
            STATE,
            "75000",
            COUNTRY,
            LAT,
            LNG
        )

        coEvery {
            photoRepository.insertPhoto(
                PhotoEntity(
                    PHOTO_ID.toLong(),
                    PROPERTY_ID.toLong(),
                    PHOTO_URI,
                    PHOTO_TITLE
                )
            )
        }

        coEvery { autocompleteRepository.getAutocompleteResult(INPUT_AUTOCOMPLETE) } returns PredictionResponse()
    }


}