package com.tuto.realestatemanager.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.location.LocationRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.search.SearchRepository
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    locationRepository: LocationRepository,
    searchRepository: SearchRepository,
    propertyRepository: PropertyRepository,
    val currentPropertyIdRepository: CurrentPropertyIdRepository,
    coroutineDispatchersProvider: CoroutineDispatchersProvider,
) : ViewModel() {

    private var isTablet = false


    private val propertyList: Flow<List<PropertyWithPhotosEntity>> =
        propertyRepository.getAllPropertiesWithPhotosEntity()

    private val propertyListLiveData: LiveData<List<PropertyWithPhotosEntity>> =
        propertyList.filterNotNull().asLiveData(coroutineDispatchersProvider.io)

    private val searchParametersLiveData: LiveData<SearchParameters?> =
        searchRepository.getParametersFlow().asLiveData(coroutineDispatchersProvider.io)

    private val userLocationLivedata: LiveData<Location> =
        locationRepository.getUserLocation().asLiveData(coroutineDispatchersProvider.io)

    private val propertyListMediatorLiveData = MediatorLiveData<MapViewState>().apply {
        addSource(propertyListLiveData) { propertiesWithPhotoEntity ->
            combine(
                propertiesWithPhotoEntity,
                searchParametersLiveData.value,
                userLocationLivedata.value
            )
        }
        addSource(searchParametersLiveData) { searchParameters ->
            combine(
                propertyListLiveData.value,
                searchParameters,
                userLocationLivedata.value
            )
        }
        addSource(userLocationLivedata) { userLocation ->
            combine(
                propertyListLiveData.value,
                searchParametersLiveData.value,
                userLocation
            )
        }

    }

    val getMapViewState = propertyListMediatorLiveData

    private fun combine(
        propertiesWithPhotosEntity: List<PropertyWithPhotosEntity>?,
        searchParameters: SearchParameters?,
        userLocation: Location?,
    ) {

        val markerPlaceList = mutableListOf<MarkerPlace>()

        if (userLocation == null) return

        if (propertiesWithPhotosEntity == null) return

        if (searchParameters == null) {
            for (property in propertiesWithPhotosEntity) {
                markerPlaceList.add(
                    MarkerPlace(
                        property.propertyEntity.id,
                        property.propertyEntity.description,
                        property.propertyEntity.address,
                        property.propertyEntity.lat!!,
                        property.propertyEntity.lng!!
                    )
                )
            }

            propertyListMediatorLiveData.value =
                MapViewState(
                    userLocation.latitude,
                    userLocation.longitude,
                    markerPlaceList
                )

        } else {
            val filteredList = mutableListOf<PropertyWithPhotosEntity>()
            for (property in propertiesWithPhotosEntity) {
                if (
                    comparePrice(searchParameters, property)
                    && compareType(searchParameters, property)
                    && compareSurface(searchParameters, property)
                    && compareCity(searchParameters, property)
                    && comparePoiTrain(searchParameters, property)
                    && comparePoiAirport(searchParameters, property)
                    && comparePoiResto(searchParameters, property)
                    && comparePoiSchool(searchParameters, property)
                    && comparePoiBus(searchParameters, property)
                    && comparePoiPark(searchParameters, property)

                ) {
                    filteredList.add(property)

                }
            }

            for (property in filteredList) {
                markerPlaceList.add(
                    MarkerPlace(
                        property.propertyEntity.id,
                        property.propertyEntity.description,
                        property.propertyEntity.address,
                        property.propertyEntity.lat!!,
                        property.propertyEntity.lng!!
                    )
                )
            }

            propertyListMediatorLiveData.value =
                MapViewState(
                    userLocation.latitude,
                    userLocation.longitude,
                    markerPlaceList
                )
        }
    }


    private fun compareType(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        var isMatching = false
        val searchType = searchParameters.type
        val propertyType = property.propertyEntity.type

        if (searchType == null || searchType == propertyType) {
            isMatching = true
        }
        return isMatching
    }

    private fun comparePrice(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        val searchPriceMini = searchParameters.priceMinimum
        val searchPriceMaxi = searchParameters.priceMaximum
        val propertyPrice = property.propertyEntity.price

        return searchPriceMini == null || searchPriceMaxi == null || propertyPrice in searchPriceMini..searchPriceMaxi
    }

    private fun compareSurface(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        var isMatching = false
        val searchSurfaceMini = searchParameters.surfaceMinimum
        val searchSurfaceMaxi = searchParameters.surfaceMaximum
        val propertySurface = property.propertyEntity.surface

        if (searchSurfaceMini == null || searchSurfaceMaxi == null || propertySurface in searchSurfaceMini..searchSurfaceMaxi) {
            isMatching = true
        }
        return isMatching
    }

    private fun compareCity(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        var isMatching = false
        val searchCity = searchParameters.city
        val propertyCity = property.propertyEntity.city

        if (searchCity == null || searchCity == "" || searchCity == propertyCity) {
            isMatching = true
        }
        return isMatching
    }

    private fun comparePoiTrain(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean = !searchParameters.poiTrain || property.propertyEntity.poiTrain

    private fun comparePoiAirport(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        val searchPoiAirport = searchParameters.poiAirport
        val propertyPoiAirport = property.propertyEntity.poiAirport

        return !searchPoiAirport || propertyPoiAirport
    }

    private fun comparePoiResto(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        val searchPoiResto = searchParameters.poiResto
        val propertyPoiResto = property.propertyEntity.poiResto

        return !searchPoiResto || propertyPoiResto
    }

    private fun comparePoiSchool(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        val searchPoiSchool = searchParameters.poiSchool
        val propertyPoiSchool = property.propertyEntity.poiSchool

        return !searchPoiSchool || propertyPoiSchool
    }

    private fun comparePoiBus(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        val searchPoiBus = searchParameters.poiBus
        val propertyPoiBus = property.propertyEntity.poiBus

        return !searchPoiBus || propertyPoiBus
    }

    private fun comparePoiPark(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {
        val searchPoiPark = searchParameters.poiPark
        val propertyPoiPark = property.propertyEntity.poiPark

        return !searchPoiPark || propertyPoiPark
    }


    val navigateSingleLiveEvent: SingleLiveEvent<MapViewAction> = SingleLiveEvent()


    fun setMarkerId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
        if (!isTablet) {
            navigateSingleLiveEvent.setValue(MapViewAction.NavigateToDetailActivity)
        }
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }

}