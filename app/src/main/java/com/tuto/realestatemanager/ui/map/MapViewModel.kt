package com.tuto.realestatemanager.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.repository.location.LocationRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.search.SearchRepository
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    locationRepository: LocationRepository,
    searchRepository: SearchRepository,
    propertyRepository: PropertyRepository,
    coroutineDispatchersProvider : CoroutineDispatchersProvider
) : ViewModel() {


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
        userLocation: Location?
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
                        property.propertyEntity.lat,
                        property.propertyEntity.lng
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
                        property.propertyEntity.lat,
                        property.propertyEntity.lng
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
        property: PropertyWithPhotosEntity
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
        property: PropertyWithPhotosEntity
    ): Boolean {
        var isMatching = false
        val searchPriceMini = searchParameters.priceMinimum
        val searchPriceMaxi = searchParameters.priceMaximum
        val propertyPrice = property.propertyEntity.price

        if (searchPriceMini == null || searchPriceMaxi == null || propertyPrice in searchPriceMini..searchPriceMaxi) {
            isMatching = true
        }
        return isMatching
    }

    private fun compareSurface(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
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
        property: PropertyWithPhotosEntity
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
        property: PropertyWithPhotosEntity
    ): Boolean {
        var isMatching = false
        val searchPoiTrain = searchParameters.poiTrain
        val propertyPoiTrain = property.propertyEntity.poiTrain

        if (searchPoiTrain == true && propertyPoiTrain || searchPoiTrain == false && !propertyPoiTrain) {
            isMatching = true
        }

        return isMatching
    }

    private fun comparePoiAirport(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        var isMatching = false
        val searchPoiAirport = searchParameters.poiAirport
        val propertyPoiAirport = property.propertyEntity.poiAirport

        if (searchPoiAirport == true && propertyPoiAirport || searchPoiAirport == false && !propertyPoiAirport) {
            isMatching = true
        }

        return isMatching
    }

    private fun comparePoiResto(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        var isMatching = false
        val searchPoiResto = searchParameters.poiResto
        val propertyPoiResto = property.propertyEntity.poiResto

        if (searchPoiResto == true && propertyPoiResto || searchPoiResto == false && !propertyPoiResto) {
            isMatching = true
        }

        return isMatching
    }

    private fun comparePoiSchool(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        var isMatching = false
        val searchPoiSchool = searchParameters.poiSchool
        val propertyPoiSchool = property.propertyEntity.poiSchool

        if (searchPoiSchool == true && propertyPoiSchool || searchPoiSchool == false && !propertyPoiSchool) {
            isMatching = true
        }

        return isMatching
    }

    private fun comparePoiBus(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        var isMatching = false
        val searchPoiBus = searchParameters.poiBus
        val propertyPoiBus = property.propertyEntity.poiBus

        if (searchPoiBus == true && propertyPoiBus || searchPoiBus == false && !propertyPoiBus) {
            isMatching = true
        }

        return isMatching
    }

    private fun comparePoiPark(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        var isMatching = false
        val searchPoiPark = searchParameters.poiPark
        val propertyPoiPark = property.propertyEntity.poiPark

        if (searchPoiPark == true && propertyPoiPark || searchPoiPark == false && !propertyPoiPark) {
            isMatching = true
        }

        return isMatching
    }


//    private var isTablet: Boolean = false //todo verifier pour-quoi ici cela ne march pas
//
//    val navigateSingleLiveEvent: SingleLiveEvent<MapViewAction> = SingleLiveEvent()
//
//    init {
//        navigateSingleLiveEvent.addSource(currentPropertyIdRepositoryImpl.currentIdFlow.filterNotNull().asLiveData()) {
//            if (!isTablet) {
//                navigateSingleLiveEvent.setValue(MapViewAction.NavigateToDetailActivity)
//            }
//        }
//    }
//
//    fun onConfigurationChanged(isTablet: Boolean) {
//        this.isTablet = isTablet
//    }


}