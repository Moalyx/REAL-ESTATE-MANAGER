package com.tuto.realestatemanager.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetAllPropertiesWithPhotosUseCase
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getUserLocationFlowUseCase: GetUserLocationFlowUseCase,
    private val getParametersFlowUseCase: GetParametersFlowUseCase,
    private val getAllPropertiesWithPhotosUseCase: GetAllPropertiesWithPhotosUseCase,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

    private var isTablet = false

    private val defaultLat = 40.7128
    private val defaultLng = -74.0060

    val navigateSingleLiveEvent: SingleLiveEvent<MapViewAction> = SingleLiveEvent()

    val getMapViewState: LiveData<MapViewState> = liveData {
        combine(
            getAllPropertiesWithPhotosUseCase.invoke(),
            getParametersFlowUseCase.invoke(),
            getUserLocationFlowUseCase.invoke(),
            currentPropertyIdRepository.currentIdFlow
        ) { propertiesWithPhotosEntity, searchParameters, userLocation, selectedMarkerId ->

            val filteredProperties = if (searchParameters == null) {
                propertiesWithPhotosEntity
            } else {
                propertiesWithPhotosEntity.filter { property ->
                    comparePrice(searchParameters, property) &&
                            compareType(searchParameters, property) &&
                            compareSurface(searchParameters, property) &&
                            compareCity(searchParameters, property) &&
                            comparePoiTrain(searchParameters, property) &&
                            comparePoiAirport(searchParameters, property) &&
                            comparePoiResto(searchParameters, property) &&
                            comparePoiSchool(searchParameters, property) &&
                            comparePoiBus(searchParameters, property) &&
                            comparePoiPark(searchParameters, property) &&
                            compareSoldStatus(searchParameters, property) &&
                            compareMinimumPhotos(searchParameters, property)
                }
            }

            val markerPlaceList = filteredProperties
                .filter { property ->
                    property.propertyEntity.lat != null &&
                            property.propertyEntity.lng != null
                }
                .map { property ->
                    MarkerPlace(
                        id = property.propertyEntity.id,
                        description = property.propertyEntity.description,
                        address = property.propertyEntity.address,
                        lat = property.propertyEntity.lat,
                        lng = property.propertyEntity.lng,
                        isSold = property.propertyEntity.propertySold
                    )
                }

            if (markerPlaceList.isEmpty()) {
                if (selectedMarkerId != null) {
                    currentPropertyIdRepository.setCurrentId(null)
                }
            } else if (selectedMarkerId != null) {
                val selectedMarkerStillVisible = markerPlaceList.any { marker ->
                    marker.id == selectedMarkerId
                }

                if (!selectedMarkerStillVisible) {
                    currentPropertyIdRepository.setCurrentId(
                        markerPlaceList.first().id
                    )
                }
            }

            emit(
                MapViewState(
                    lat = userLocation?.latitude ?: defaultLat,
                    lng = userLocation?.longitude ?: defaultLng,
                    markers = markerPlaceList,
                    selectedMarkerId = selectedMarkerId
                )
            )
        }.collect()
    }

    fun setMarkerId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)

        if (!isTablet) {
            navigateSingleLiveEvent.setValue(MapViewAction.NavigateToDetailActivity)
        }
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }

    private fun compareType(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return searchParameters.type == null ||
                searchParameters.type == property.propertyEntity.type
    }

    private fun comparePrice(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchPriceMinimum = searchParameters.priceMinimum
        val searchPriceMaximum = searchParameters.priceMaximum
        val propertyPrice = property.propertyEntity.price

        return (searchPriceMinimum == null || propertyPrice >= searchPriceMinimum) &&
                (searchPriceMaximum == null || propertyPrice <= searchPriceMaximum)
    }

    private fun compareSurface(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchSurfaceMinimum = searchParameters.surfaceMinimum
        val searchSurfaceMaximum = searchParameters.surfaceMaximum
        val propertySurface = property.propertyEntity.surface

        return (searchSurfaceMinimum == null || propertySurface >= searchSurfaceMinimum) &&
                (searchSurfaceMaximum == null || propertySurface <= searchSurfaceMaximum)
    }

    private fun compareCity(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchCity = searchParameters.city
        val propertyCity = property.propertyEntity.city

        return searchCity.isNullOrBlank() ||
                propertyCity.equals(searchCity.trim(), ignoreCase = true)
    }

    private fun comparePoiTrain(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiTrain || property.propertyEntity.poiTrain
    }

    private fun comparePoiAirport(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiAirport || property.propertyEntity.poiAirport
    }

    private fun comparePoiResto(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiResto || property.propertyEntity.poiResto
    }

    private fun comparePoiSchool(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiSchool || property.propertyEntity.poiSchool
    }

    private fun comparePoiBus(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiBus || property.propertyEntity.poiBus
    }

    private fun comparePoiPark(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiPark || property.propertyEntity.poiPark
    }

    private fun compareSoldStatus(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return searchParameters.soldStatus == null ||
                property.propertyEntity.propertySold == searchParameters.soldStatus
    }

    private fun compareMinimumPhotos(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return searchParameters.minimumPhotos == null ||
                property.photos.size >= searchParameters.minimumPhotos
    }
}