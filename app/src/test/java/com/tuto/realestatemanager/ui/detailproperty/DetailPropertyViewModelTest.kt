package com.tuto.realestatemanager.ui.detailproperty

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tuto.realestatemanager.TestCoroutineRule
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.observeForTesting
import com.tuto.realestatemanager.ui.detail.DetailPropertyViewModel
import com.tuto.realestatemanager.ui.detail.PropertyDetailViewState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailPropertyViewModelTest {

    companion object {
        //for detail property
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
        private const val IS_PROPERTY_SOLD = false
        private const val SALESINCE = "2023-05-10"
        private const val SOLD_AT = "estate available for sale"
        private const val POITRAIN = true
        private const val POIAIRPORT = true
        private const val POIRESTO = true
        private const val POISCHOOL = true
        private const val POIBUS = true
        private const val POIPARK = true

        //for photo
        private const val PHOTO_ID = 0L
        private const val PROPERTY_PHOTO_ID = PROPERTY_ID
        private const val PHOTO_URI = "PHOTO_URI"
        private const val PHOTO_TITLE = "PHOTO_TITLE"


    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val currentIdStateFlow =
        MutableStateFlow<Long>(0)

    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk()
    private val propertyRepository: PropertyRepository = mockk()

    private val propertyDetailViewStateMutableLiveData = MutableLiveData<PropertyDetailViewState>()
//    private val propertyDetailViewStateMutableStateFlow =
//        MutableStateFlow<PropertyDetailViewState>()


    private lateinit var detailPropertyViewModel: DetailPropertyViewModel

    @Before
    fun setUp() {

        //PREPARE DATA FOR PROPERTYREPOSITORY RETURNS
        val getPropertyWithPhoto = PropertyWithPhotosEntity(
            propertyEntity = PropertyEntity(
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
                propertyOnSaleSince = SALESINCE,
                propertyDateOfSale = SOLD_AT,
                poiTrain = POITRAIN,
                poiAirport = POIAIRPORT,
                poiResto = POIRESTO,
                poiSchool = POISCHOOL,
                poiBus = POIBUS,
                poiPark = POIPARK
            ),
            photos = listOf(
                PhotoEntity(
                    id = PHOTO_ID,
                    propertyId = PROPERTY_PHOTO_ID,
                    photoUri = PHOTO_URI,
                    photoTitle = PHOTO_TITLE
                )
            )
        )


        //PREPARE DATA FOR CURRENTPROPERTYIDREPOSITORY RETURNS
        currentIdStateFlow.value = 0L


        //propertyDetailViewStateMutableLiveData. value = PropertyDetailViewState(
        val propertyDetailViewState = PropertyDetailViewState(
            getPropertyWithPhoto.propertyEntity.id,
            getPropertyWithPhoto.propertyEntity.type,
            getPropertyWithPhoto.propertyEntity.price,
            getPropertyWithPhoto.photos,
            getPropertyWithPhoto.propertyEntity.address,
            getPropertyWithPhoto.propertyEntity.city,
            getPropertyWithPhoto.propertyEntity.zipCode,
            getPropertyWithPhoto.propertyEntity.state,
            getPropertyWithPhoto.propertyEntity.country,
            getPropertyWithPhoto.propertyEntity.surface,
            getPropertyWithPhoto.propertyEntity.description,
            getPropertyWithPhoto.propertyEntity.room,
            getPropertyWithPhoto.propertyEntity.bathroom,
            getPropertyWithPhoto.propertyEntity.bedroom,
            getPropertyWithPhoto.propertyEntity.agent,
            getPropertyWithPhoto.propertyEntity.propertySold,
            getPropertyWithPhoto.propertyEntity.propertyOnSaleSince,
            getPropertyWithPhoto.propertyEntity.propertyDateOfSale,
            getPropertyWithPhoto.propertyEntity.poiTrain,
            getPropertyWithPhoto.propertyEntity.poiAirport,
            getPropertyWithPhoto.propertyEntity.poiResto,
            getPropertyWithPhoto.propertyEntity.poiSchool,
            getPropertyWithPhoto.propertyEntity.poiBus,
            getPropertyWithPhoto.propertyEntity.poiPark,
        )
        //SET RETURNS OF CURRENTPROPERTYIDREPOSITORY
        every { currentPropertyIdRepository.currentIdFlow } returns flowOf(currentIdStateFlow.value)

        //SET RETURNS OF PROPERTYREPOSITORY
        every { propertyRepository.getPropertyById(currentIdStateFlow.value) } returns flowOf(
            getPropertyWithPhoto
        )

        //INSTANTIATE VIEWMODEL
        detailPropertyViewModel = DetailPropertyViewModel(
            currentPropertyIdRepository = currentPropertyIdRepository,
            propertyRepository = propertyRepository
        )
        //every { detailPropertyViewModel.detailPropertyLiveData } returns propertyDetailViewStateMutableLiveData as LiveData<PropertyDetailViewState>


    }


    @Test
    fun nominal_case() = testCoroutineRule.runTest {

        //GIVEN
        val propertyDetailViewState = PropertyDetailViewState(
            getPropertyWithPhoto.propertyEntity.id,
            getPropertyWithPhoto.propertyEntity.type,
            getPropertyWithPhoto.propertyEntity.price,
            getPropertyWithPhoto.photos,
            getPropertyWithPhoto.propertyEntity.address,
            getPropertyWithPhoto.propertyEntity.city,
            getPropertyWithPhoto.propertyEntity.zipCode,
            getPropertyWithPhoto.propertyEntity.state,
            getPropertyWithPhoto.propertyEntity.country,
            getPropertyWithPhoto.propertyEntity.surface,
            getPropertyWithPhoto.propertyEntity.description,
            getPropertyWithPhoto.propertyEntity.room,
            getPropertyWithPhoto.propertyEntity.bathroom,
            getPropertyWithPhoto.propertyEntity.bedroom,
            getPropertyWithPhoto.propertyEntity.agent,
            getPropertyWithPhoto.propertyEntity.propertySold,
            getPropertyWithPhoto.propertyEntity.propertyOnSaleSince,
            getPropertyWithPhoto.propertyEntity.propertyDateOfSale,
            getPropertyWithPhoto.propertyEntity.poiTrain,
            getPropertyWithPhoto.propertyEntity.poiAirport,
            getPropertyWithPhoto.propertyEntity.poiResto,
            getPropertyWithPhoto.propertyEntity.poiSchool,
            getPropertyWithPhoto.propertyEntity.poiBus,
            getPropertyWithPhoto.propertyEntity.poiPark,
        )

        detailPropertyViewModel.detailPropertyLiveData.run {
            val result = detailPropertyViewModel.detailPropertyLiveData

            assertEquals(result, propertyDetailViewState)
        }

        coVerify { propertyRepository.getPropertyById(currentIdStateFlow.value) }
        coVerify { currentPropertyIdRepository.currentIdFlow }


    }

    fun getPropertyDetailViewState(): PropertyDetailViewState {
        return PropertyDetailViewState(
            getPropertyWithPhoto.propertyEntity.id,
            getPropertyWithPhoto.propertyEntity.type,
            getPropertyWithPhoto.propertyEntity.price,
            getPropertyWithPhoto.photos,
            getPropertyWithPhoto.propertyEntity.address,
            getPropertyWithPhoto.propertyEntity.city,
            getPropertyWithPhoto.propertyEntity.zipCode,
            getPropertyWithPhoto.propertyEntity.state,
            getPropertyWithPhoto.propertyEntity.country,
            getPropertyWithPhoto.propertyEntity.surface,
            getPropertyWithPhoto.propertyEntity.description,
            getPropertyWithPhoto.propertyEntity.room,
            getPropertyWithPhoto.propertyEntity.bathroom,
            getPropertyWithPhoto.propertyEntity.bedroom,
            getPropertyWithPhoto.propertyEntity.agent,
            getPropertyWithPhoto.propertyEntity.propertySold,
            getPropertyWithPhoto.propertyEntity.propertyOnSaleSince,
            getPropertyWithPhoto.propertyEntity.propertyDateOfSale,
            getPropertyWithPhoto.propertyEntity.poiTrain,
            getPropertyWithPhoto.propertyEntity.poiAirport,
            getPropertyWithPhoto.propertyEntity.poiResto,
            getPropertyWithPhoto.propertyEntity.poiSchool,
            getPropertyWithPhoto.propertyEntity.poiBus,
            getPropertyWithPhoto.propertyEntity.poiPark,
        )
    }

    val getPropertyWithPhoto = PropertyWithPhotosEntity(
        propertyEntity = PropertyEntity(
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
            propertyOnSaleSince = SALESINCE,
            propertyDateOfSale = SOLD_AT,
            poiTrain = POITRAIN,
            poiAirport = POIAIRPORT,
            poiResto = POIRESTO,
            poiSchool = POISCHOOL,
            poiBus = POIBUS,
            poiPark = POIPARK
        ),
        photos = listOf(
            PhotoEntity(
                id = PHOTO_ID,
                propertyId = PROPERTY_PHOTO_ID,
                photoUri = PHOTO_URI,
                photoTitle = PHOTO_TITLE
            )
        )
    )


}