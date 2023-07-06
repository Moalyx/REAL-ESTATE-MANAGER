package com.tuto.realestatemanager.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.geocoding.GeocodingRepository
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.search.SearchRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository,
    propertyRepository: PropertyRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val priceConverterRepository: PriceConverterRepository,
    searchRepository: SearchRepository,
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {

    private var isTablet = false

    val propertyListLiveData: LiveData<List<PropertyViewState>> = liveData {
        combine(
            propertyRepository.getAllPropertiesWithPhotosEntity(),
            searchRepository.getParametersFlow(),
            priceConverterRepository.isDollarStateFlow
        ) { propertiesWithPhotosEntity, searchParameters, isDollar ->

            for (property in propertiesWithPhotosEntity){
                if(property.propertyEntity.lat == null || property.propertyEntity.lng == null || property.propertyEntity.lat == 0.0 || property.propertyEntity.lng == 0.0 ){
                    val latLng = geocodingRepository.getLatLngLocation(
                        "${property.propertyEntity.address} ${property.propertyEntity.city} ${property.propertyEntity.zipCode} ${property.propertyEntity.state} ${property.propertyEntity.country}")
                    property.propertyEntity.lat = latLng.results.get(0).geometry?.location?.lat
                    property.propertyEntity.lng = latLng.results.get(0).geometry?.location?.lng
                    propertyRepository.updateProperty(property.propertyEntity)
                }
            }

            if (searchParameters == null) {
                emit(mapPropertiesIntoViewState(propertiesWithPhotosEntity, isDollar))
            } else {
                val filteredList : List<PropertyWithPhotosEntity> = propertiesWithPhotosEntity.filter { property ->
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
                }

                emit(mapPropertiesIntoViewState(filteredList, isDollar))
            }
        }.collect()
    }

    fun onDeleteTemporaryPhotoRepository(){
        temporaryPhotoRepository.onDeleteTemporaryPhotoRepo()
    }

    private fun mapPropertiesIntoViewState(
        propertiesWithPhotosEntity: List<PropertyWithPhotosEntity>,
        isDollar: Boolean
    ): List<PropertyViewState> = propertiesWithPhotosEntity.map { propertyWithPhotosEntity ->
        PropertyViewState(
            id = propertyWithPhotosEntity.propertyEntity.id,
            type = propertyWithPhotosEntity.propertyEntity.type,
            price = convertMoney(
                "${propertyWithPhotosEntity.propertyEntity.price}",
                isDollar
            ),
            photoList = propertyWithPhotosEntity.photos.map { it },
            city = propertyWithPhotosEntity.propertyEntity.city,
            propertyWithPhotosEntity.propertyEntity.propertySold,
            onItemClicked = {
                if (!isTablet){
                navigateSingleLiveEvent.setValue(ListViewAction.NavigateToDetailActivity)}
                currentPropertyIdRepository.setCurrentId(propertyWithPhotosEntity.propertyEntity.id)
            }
        )
    }

    private fun convertMoney(price: String, isDollar: Boolean): String {
        val decimalFormat = DecimalFormat("#,###.#")
        val formatPrice: String = decimalFormat.format(price.toInt()).toString()
        val convertPrice: String = if (isDollar) {
            "$formatPrice $"
        } else {
            decimalFormat.format(Utils.convertDollarToEuro(price.toInt())).toString() + " €"
        }
        return convertPrice
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
        val searchPriceMini = searchParameters.priceMinimum
        val searchPriceMaxi = searchParameters.priceMaximum
        val propertyPrice = property.propertyEntity.price

        return searchPriceMini == null || searchPriceMaxi == null || propertyPrice in searchPriceMini..searchPriceMaxi
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
    ): Boolean = !searchParameters.poiTrain || property.propertyEntity.poiTrain

    private fun comparePoiAirport(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchPoiAirport = searchParameters.poiAirport
        val propertyPoiAirport = property.propertyEntity.poiAirport

        return !searchPoiAirport || propertyPoiAirport
    }

    private fun comparePoiResto(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchPoiResto = searchParameters.poiResto
        val propertyPoiResto = property.propertyEntity.poiResto

        return !searchPoiResto || propertyPoiResto
    }

    private fun comparePoiSchool(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchPoiSchool = searchParameters.poiSchool
        val propertyPoiSchool = property.propertyEntity.poiSchool

        return !searchPoiSchool || propertyPoiSchool
    }

    private fun comparePoiBus(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchPoiBus = searchParameters.poiBus
        val propertyPoiBus = property.propertyEntity.poiBus

        return !searchPoiBus || propertyPoiBus
    }

    private fun comparePoiPark(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchPoiPark = searchParameters.poiPark
        val propertyPoiPark = property.propertyEntity.poiPark

        return !searchPoiPark || propertyPoiPark
    }

    val navigateSingleLiveEvent: SingleLiveEvent<ListViewAction> = SingleLiveEvent()

    fun onNavigateToCreateActivity(){
        navigateSingleLiveEvent.setValue(ListViewAction.NavigateToCreateActvity)
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }


}