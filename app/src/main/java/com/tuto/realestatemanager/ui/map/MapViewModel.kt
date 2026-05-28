package com.tuto.realestatemanager.ui.map

import android.util.Log
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
    val currentPropertyIdRepository: CurrentPropertyIdRepository,
) : ViewModel() {

    private var isTablet = false
    private val defaultLat = 48.8566
    private val defaultLng = 2.3522

    val getMapViewState: LiveData<MapViewState> = liveData {
        combine(
            getAllPropertiesWithPhotosUseCase.invoke(),
            getParametersFlowUseCase.invoke(),
            getUserLocationFlowUseCase.invoke()

        ) { propertiesWithPhotosEntity, searchParameters, userLocation ->

            //val markerPlaceList = mutableListOf<MarkerPlace>()

//            if (userLocation == null) return
//
//            if (propertiesWithPhotosEntity == null) return

            if (searchParameters == null) {
                val markerPlaceList = mutableListOf<MarkerPlace>()
                for (property in propertiesWithPhotosEntity) {
                    if (property.propertyEntity.lat != null && property.propertyEntity.lng != null) {
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
                }

//                propertyListMediatorLiveData.value =
                emit(
                    MapViewState(
                        lat = userLocation?.latitude ?: defaultLat,
                        lng = userLocation?.longitude ?: defaultLng,
                        markers = markerPlaceList
                    )
                )

            } else {
                val markerPlaceListFiltered = mutableListOf<MarkerPlace>()
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
                    if (property.propertyEntity.lat != null && property.propertyEntity.lng != null) {
                        markerPlaceListFiltered.add(
                            MarkerPlace(
                                property.propertyEntity.id,
                                property.propertyEntity.description,
                                property.propertyEntity.address,
                                property.propertyEntity.lat,
                                property.propertyEntity.lng
                            )
                        )
                    }
                }

//                propertyListMediatorLiveData.value =

                emit(
                    MapViewState(
                        userLocation?.latitude ?: defaultLat,
                        userLocation?.longitude ?: defaultLng,
                        markerPlaceListFiltered
                    )
                )
            }


        }.collect()
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

        //return searchPriceMini == null || searchPriceMaxi == null || propertyPrice in searchPriceMini..searchPriceMaxi
        return (searchPriceMini == null || propertyPrice >= searchPriceMini) &&
                (searchPriceMaxi == null || propertyPrice <= searchPriceMaxi)
    }

    private fun compareSurface(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {

        val searchSurfaceMini = searchParameters.surfaceMinimum
        val searchSurfaceMaxi = searchParameters.surfaceMaximum
        val propertySurface = property.propertyEntity.surface

        return (searchSurfaceMini == null || propertySurface >= searchSurfaceMini) &&
                (searchSurfaceMaxi == null || propertySurface <= searchSurfaceMaxi)
    }

    private fun compareCity(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {

        val searchCity = searchParameters.city
        val propertyCity = property.propertyEntity.city

        return searchCity.isNullOrBlank() ||
                propertyCity.equals(searchCity.trim(), ignoreCase = true)
    }


    private fun compareParameters(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity,
    ): Boolean {

        return true
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