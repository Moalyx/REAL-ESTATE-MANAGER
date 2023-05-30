package com.tuto.realestatemanager.ui.propertymap

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuto.realestatemanager.TestCoroutineRule
import com.tuto.realestatemanager.data.repository.location.LocationRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.search.SearchRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.map.MapViewModel
import com.tuto.realestatemanager.ui.map.MapViewState
import com.tuto.realestatemanager.ui.map.MarkerPlace
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule

class MapViewModelTest {

    companion object {

        //PROPERTY_1
        private const val PROPERTY_ID_1 = 0L
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

        //PROPERTY_1 PHOTO
        private const val PHOTO_ID = 0L
        private const val PROPERTY_PHOTO_ID = PROPERTY_ID_1
        private const val PHOTO_URI = "PHOTO_URI"
        private const val PHOTO_TITLE = "PHOTO_TITLE"

        //PROPERTY_2
        private const val PROPERTY_ID_2 = 1L
        private const val TYPE_2 = "type"
        private const val PRICE_2 = 0
        private const val ADDRESS_2 = "ADDRESS"
        private const val CITY_2 = "CITY"
        private const val STATE_2 = "STATE"
        private const val ZIPCODE_2 = 75000
        private const val COUNTRY_2 = "COUNTRY"
        private const val SURFACE_2 = 100
        private const val LAT_2 = 48.0
        private const val LNG_2 = 20.0
        private const val DESCRIPTION_2 = "DESCRIPTION"
        private const val ROOM_2 = 2
        private const val BEDROOM_2 = 2
        private const val BATHROOM_2 = 2
        private const val AGENT_2 = "AGENT"
        private const val IS_PROPERTY_SOLD_2 = false
        private const val SALESINCE_2 = "2023-05-10"
        private const val SOLD_AT_2 = "estate available for sale"
        private const val POITRAIN_2 = true
        private const val POIAIRPORT_2 = true
        private const val POIRESTO_2 = true
        private const val POISCHOOL_2 = true
        private const val POIBUS_2 = true
        private const val POIPARK_2 = true

        //PROPERTY_1 PHOTO
        private const val PHOTO_ID_2 = 1L
        private const val PROPERTY_2_PHOTO_ID = PROPERTY_ID_2
        private const val PHOTO_URI_2 = "PHOTO_URI"
        private const val PHOTO_TITLE_2 = "PHOTO_TITLE"

        //USERLOCATION
        private const val USER_LATITUDE = -90.0
        private const val USER_LONGITUDE = -100.0
        private val location: Location = Location("$USER_LATITUDE, $USER_LONGITUDE")

        //SEARCHPARAMETERS
        private const val SEARCHPARAMETER_TYPE = "House"
        private const val SEARCHPARAMETER_PRICE_MINIMUM = 100000
        private const val SEARCHPARAMETER_PRICE_MAXIMUM = 300000
        private const val SEARCHPARAMETER_SURFACE_MINIMUM = 100
        private const val SEARCHPARAMETER_SURFACE_MAXIMUM = 500
        private const val SEARCHPARAMETER_CITY = "Paris"
        private const val SEARCHPARAMETER_POITRAIN = false
        private const val SEARCHPARAMETER_POIAIRPORT = false
        private const val SEARCHPARAMETER_POIRESTO = false
        private const val SEARCHPARAMETER_POISCHOOL = false
        private const val SEARCHPARAMETER_POIBUS = false
        private const val SEARCHPARAMETER_POIPARK = false
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    //REPOSITORY MOCKK
    private val locationRepository: LocationRepository = mockk()
    private val searchRepository: SearchRepository = mockk()
    private val propertyRepository: PropertyRepository = mockk()

    //FLOW FOR LOCATION REPOSITORY
    private val getUserLocation = flowOf(location)

    private lateinit var mapViewModel: MapViewModel

    private val getSearchParameters = SearchParameters(
        SEARCHPARAMETER_TYPE,
        SEARCHPARAMETER_PRICE_MINIMUM,
        SEARCHPARAMETER_PRICE_MAXIMUM,
        SEARCHPARAMETER_SURFACE_MINIMUM,
        SEARCHPARAMETER_SURFACE_MAXIMUM,
        SEARCHPARAMETER_CITY,
        SEARCHPARAMETER_POITRAIN,
        SEARCHPARAMETER_POIAIRPORT,
        SEARCHPARAMETER_POIRESTO,
        SEARCHPARAMETER_POISCHOOL,
        SEARCHPARAMETER_POIBUS,
        SEARCHPARAMETER_POIPARK
    )

    private fun getMarkerList(): List<MarkerPlace> {
        return listOf(
            MarkerPlace(
                PROPERTY_ID_1,
                DESCRIPTION,
                ADDRESS,
                LAT,
                LNG
                ),
            MarkerPlace(
                PROPERTY_ID_2,
                DESCRIPTION_2,
                ADDRESS_2,
                LAT_2,
                LNG_2
            )

        )
    }

    val mapViewState: MapViewState = MapViewState(
        USER_LATITUDE,
        USER_LONGITUDE,
        getMarkerList()
    )


    private val getAllProperties: List<PropertyWithPhotosEntity> = listOf(
        PropertyWithPhotosEntity(
            propertyEntity = PropertyEntity(
                id = PROPERTY_ID_1,
                type = TYPE_2,
                price = PRICE_2,
                address = ADDRESS_2,
                city = CITY_2,
                state = STATE_2,
                zipCode = ZIPCODE_2,
                country = COUNTRY_2,
                surface = SURFACE_2,
                lat = LAT_2,
                lng = LNG_2,
                description = DESCRIPTION_2,
                room = ROOM_2,
                bedroom = BEDROOM_2,
                bathroom = BATHROOM_2,
                agent = AGENT_2,
                propertySold = IS_PROPERTY_SOLD_2,
                propertyOnSaleSince = SALESINCE_2,
                propertyDateOfSale = SOLD_AT_2,
                poiTrain = POITRAIN_2,
                poiAirport = POIAIRPORT_2,
                poiResto = POIRESTO_2,
                poiSchool = POISCHOOL_2,
                poiBus = POIBUS_2,
                poiPark = POIPARK_2
            ),
            photos = listOf(
                PhotoEntity(
                    id = PHOTO_ID,
                    propertyId = PROPERTY_PHOTO_ID,
                    photoUri = PHOTO_URI,
                    photoTitle = PHOTO_TITLE
                )
            )
        ),
        PropertyWithPhotosEntity(
            propertyEntity = PropertyEntity(
                id = PROPERTY_ID_2,
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
                    id = PHOTO_ID_2,
                    propertyId = PROPERTY_2_PHOTO_ID,
                    photoUri = PHOTO_URI_2,
                    photoTitle = PHOTO_TITLE_2
                )
            )
        )

    )

    @Before
    fun setUp() {

        //SET DATA FOR USERLOCATION
        every { locationRepository.getUserLocation() } returns getUserLocation

        //SET DATA FOR PROPERTYREPOSITORY
        every { propertyRepository.getAllPropertiesWithPhotosEntity() } returns flowOf(
            getAllProperties
        )

        //SET DATA FOR RESEARCHREPOSITORY
        every { searchRepository.getParametersFlow() } returns flowOf(getSearchParameters)

        //INSTANTIATE VIEWMODEL
        mapViewModel = MapViewModel(
            locationRepository = locationRepository,
            propertyRepository = propertyRepository,
            searchRepository = searchRepository
        )


    }


}