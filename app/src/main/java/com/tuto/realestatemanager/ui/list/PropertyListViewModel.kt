package com.tuto.realestatemanager.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bumptech.glide.util.Util
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.search.SearchRepository
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor(
    propertyRepository: PropertyRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val priceConverterRepository: PriceConverterRepository,
    searchRepository: SearchRepository
) : ViewModel() {

    private val propertyListLiveData: LiveData<List<PropertyWithPhotosEntity>> =
        propertyRepository.getAllPropertiesWithPhotosEntity()
            .asLiveData(Dispatchers.IO)

    private val currentMoneyLiveData: LiveData<Boolean> =
        priceConverterRepository.getCurrentMoneyLiveData.asLiveData(Dispatchers.IO)

    private val searchParametersLiveData: LiveData<SearchParameters?> =
        searchRepository.getParametersFlow().asLiveData(Dispatchers.IO)

    private val propertyListMediatorLiveData = MediatorLiveData<List<PropertyViewState>>().apply {
        addSource(propertyListLiveData) { propertiesWithPhotoEntity ->
            combine(propertiesWithPhotoEntity, searchParametersLiveData.value, currentMoneyLiveData.value)
        }
        addSource(searchParametersLiveData) { searchParameters ->
            combine(propertyListLiveData.value, searchParameters, currentMoneyLiveData.value)
        }
        addSource(currentMoneyLiveData) { currentMoney ->
            combine(propertyListLiveData.value, searchParametersLiveData.value, currentMoney)
        }

    }

    val getPropertiesLiveData: LiveData<List<PropertyViewState>> = propertyListMediatorLiveData

    private fun combine(
        propertiesWithPhotosEntity: List<PropertyWithPhotosEntity>?,
        searchParameters: SearchParameters?,
        currentMoney: Boolean?
    ) {

        if (propertiesWithPhotosEntity == null) return

        if (searchParameters == null) {
            propertyListMediatorLiveData.value =
                propertiesWithPhotosEntity.map { propertyWithPhotosEntity ->
                    PropertyViewState(
                        id = propertyWithPhotosEntity.propertyEntity.id,
                        type = propertyWithPhotosEntity.propertyEntity.type,
                        price = convertMoney("${propertyWithPhotosEntity.propertyEntity.price}", currentMoney!!),
                        photoList = propertyWithPhotosEntity.photos.map { it },
                        city = propertyWithPhotosEntity.propertyEntity.city,
                        onItemClicked = {
                            currentPropertyIdRepository.setCurrentId(propertyWithPhotosEntity.propertyEntity.id)
                        }
                    )
                }
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
            propertyListMediatorLiveData.value = filteredList.map { propertyWithPhotosEntity ->
                PropertyViewState(
                    id = propertyWithPhotosEntity.propertyEntity.id,
                    type = propertyWithPhotosEntity.propertyEntity.type,
                    price = propertyWithPhotosEntity.propertyEntity.price.toString(),
                    photoList = propertyWithPhotosEntity.photos.map { it },
                    city = propertyWithPhotosEntity.propertyEntity.city,
                    onItemClicked = {
                        currentPropertyIdRepository.setCurrentId(propertyWithPhotosEntity.propertyEntity.id)
                    }
                )
            }
        }
    }

    private fun convertMoney(price: String, moneyStatus: Boolean): String{
        val decimalFormat = DecimalFormat("#,###.#")
        val formatPrice: String = decimalFormat.format(price.toInt()).toString()
        var convertPrice = ""
        if(moneyStatus){
            convertPrice = "$formatPrice $"
        }else{
            convertPrice = decimalFormat.format(Utils.convertDollarToEuro(price.toInt())).toString() + " €"
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
        val propertyPrice = property.propertyEntity.price

        if (searchSurfaceMini == null || searchSurfaceMaxi == null || propertyPrice in searchSurfaceMini..searchSurfaceMaxi) {
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

        if (searchCity == null || searchCity == propertyCity) {
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

        if (searchPoiTrain == true && propertyPoiTrain == true) {
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

        if (searchPoiAirport == true && propertyPoiAirport == true) {
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

        if (searchPoiResto == true && propertyPoiResto == true) {
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

        if (searchPoiSchool == true && propertyPoiSchool == true) {
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

        if (searchPoiBus == true && propertyPoiBus == true) {
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

        if (searchPoiPark == true && propertyPoiPark == true) {
            isMatching = true
        }

        return isMatching
    }


//    val propertyListLiveData: LiveData<List<PropertyViewState>> = //todo ligne 23 a 37 le code fonctionne bien pour une liste
//        propertyRepository.getAllPropertiesWithPhotosEntity().map { propertyEntities -> //todo mais ici on veut une liste trier
//            propertyEntities.map { propertyWithPhotosEntity ->    //todo du coup utilisation mediator pour trier la liste
//                PropertyViewState(
//                    id = propertyWithPhotosEntity.propertyEntity.id,
//                    type = propertyWithPhotosEntity.propertyEntity.type,
//                    price = propertyWithPhotosEntity.propertyEntity.price,
//                    photoList = propertyWithPhotosEntity.photos.map { it },
//                    city = propertyWithPhotosEntity.propertyEntity.city,
//                    onItemClicked = {
//                        currentPropertyIdRepository.setCurrentId(propertyWithPhotosEntity.propertyEntity.id)
//                    }
//                )
//            }
//        }.asLiveData(Dispatchers.IO)

}