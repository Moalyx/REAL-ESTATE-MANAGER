package com.tuto.realestatemanager.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetAllPropertiesWithPhotosUseCase
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getUserLocationFlowUseCase: GetUserLocationFlowUseCase,
    private val getParametersFlowUseCase: GetParametersFlowUseCase,
    private val getAllPropertiesWithPhotosUseCase: GetAllPropertiesWithPhotosUseCase,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

    private companion object {
        private const val DEFAULT_LAT = 40.7128
        private const val DEFAULT_LNG = -74.0060
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }

    private var isTablet = false

    private val _cameraPosition =
        MutableStateFlow<CameraPosition?>(null)

    val cameraPosition: StateFlow<CameraPosition?> =
        _cameraPosition.asStateFlow()

    private val _viewAction = MutableSharedFlow<MapViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

    val mapViewState: StateFlow<MapViewState?> = combine(
        getAllPropertiesWithPhotosUseCase.invoke(),
        getParametersFlowUseCase.invoke(),
        getUserLocationFlowUseCase.invoke(),
        currentPropertyIdRepository.currentIdFlow
    ) { propertiesWithPhotosEntity,
        searchParameters,
        userLocation,
        selectedMarkerId ->

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
            val selectedMarkerStillVisible =
                markerPlaceList.any { marker ->
                    marker.id == selectedMarkerId
                }

            if (!selectedMarkerStillVisible) {
                currentPropertyIdRepository.setCurrentId(
                    markerPlaceList.first().id
                )
            }
        }

        MapViewState(
            lat = userLocation?.latitude ?: DEFAULT_LAT,
            lng = userLocation?.longitude ?: DEFAULT_LNG,
            markers = markerPlaceList,
            selectedMarkerId = selectedMarkerId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            STOP_TIMEOUT_MILLIS
        ),
        initialValue = null
    )

    fun saveCameraPosition(cameraPosition: CameraPosition) {
        _cameraPosition.value = cameraPosition
    }

    fun setMarkerId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)

        if (!isTablet) {
            _viewAction.tryEmit(
                MapViewAction.NavigateToDetailActivity
            )
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
                searchParameters.type ==
                property.propertyEntity.type
    }

    private fun comparePrice(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchPriceMinimum =
            searchParameters.priceMinimum

        val searchPriceMaximum =
            searchParameters.priceMaximum

        val propertyPrice =
            property.propertyEntity.price

        return (searchPriceMinimum == null ||
                propertyPrice >= searchPriceMinimum) &&
                (searchPriceMaximum == null ||
                        propertyPrice <= searchPriceMaximum)
    }

    private fun compareSurface(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchSurfaceMinimum =
            searchParameters.surfaceMinimum

        val searchSurfaceMaximum =
            searchParameters.surfaceMaximum

        val propertySurface =
            property.propertyEntity.surface

        return (searchSurfaceMinimum == null ||
                propertySurface >= searchSurfaceMinimum) &&
                (searchSurfaceMaximum == null ||
                        propertySurface <= searchSurfaceMaximum)
    }

    private fun compareCity(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchCity = searchParameters.city
        val propertyCity = property.propertyEntity.city

        return searchCity.isNullOrBlank() ||
                propertyCity.equals(
                    searchCity.trim(),
                    ignoreCase = true
                )
    }

    private fun comparePoiTrain(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiTrain ||
                property.propertyEntity.poiTrain
    }

    private fun comparePoiAirport(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiAirport ||
                property.propertyEntity.poiAirport
    }

    private fun comparePoiResto(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiResto ||
                property.propertyEntity.poiResto
    }

    private fun comparePoiSchool(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiSchool ||
                property.propertyEntity.poiSchool
    }

    private fun comparePoiBus(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiBus ||
                property.propertyEntity.poiBus
    }

    private fun comparePoiPark(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return !searchParameters.poiPark ||
                property.propertyEntity.poiPark
    }

    private fun compareSoldStatus(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return searchParameters.soldStatus == null ||
                property.propertyEntity.propertySold ==
                searchParameters.soldStatus
    }

    private fun compareMinimumPhotos(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        return searchParameters.minimumPhotos == null ||
                property.photos.size >=
                searchParameters.minimumPhotos
    }
}