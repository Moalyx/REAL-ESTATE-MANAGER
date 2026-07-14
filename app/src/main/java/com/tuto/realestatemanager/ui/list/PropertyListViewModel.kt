package com.tuto.realestatemanager.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.geocode.GetLatLngPropertyLocationUseCase
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetAllPropertiesWithPhotosUseCase
import com.tuto.realestatemanager.domain.usecase.property.UpdatePropertyUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor(
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val getAllPropertiesWithPhotosUseCase: GetAllPropertiesWithPhotosUseCase,
    private val updatePropertyUseCase: UpdatePropertyUseCase,
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val isDollarFlowUseCase: IsDollarFlowUseCase,
    private val getParametersFlowUseCase: GetParametersFlowUseCase,
    private val getLatLngPropertyLocationUseCase: GetLatLngPropertyLocationUseCase
) : ViewModel() {

    private val isTablet = MutableStateFlow(false)

    private val selectionFlow = combine(
        currentPropertyIdRepository.currentIdFlow,
        isTablet
    ) { currentPropertyId, isTablet ->
        Pair(currentPropertyId, isTablet)
    }

    private val _viewAction = MutableSharedFlow<ListViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

    val propertyListStateFlow: StateFlow<List<PropertyViewState>> = combine(
        getAllPropertiesWithPhotosUseCase.invoke(),
        getParametersFlowUseCase.invoke(),
        isDollarFlowUseCase.invoke(),
        isInternetAvailableUseCase.invoke(),
        selectionFlow
    ) { propertiesWithPhotosEntity,
        searchParameters,
        isDollar,
        isInternetAvailable,
        selection ->

        val currentPropertyId = selection.first
        val isTablet = selection.second

        val propertiesWithoutLocation = propertiesWithPhotosEntity.filter { property ->
            property.propertyEntity.lat == null ||
                    property.propertyEntity.lng == null ||
                    property.propertyEntity.lat == 0.0 ||
                    property.propertyEntity.lng == 0.0
        }

        if (isInternetAvailable && propertiesWithoutLocation.isNotEmpty()) {
            for (property in propertiesWithoutLocation) {
                val location = getLatLngPropertyLocationUseCase.invoke(
                    "${property.propertyEntity.address} " +
                            "${property.propertyEntity.city} " +
                            "${property.propertyEntity.zipCode} " +
                            "${property.propertyEntity.state} " +
                            property.propertyEntity.country
                )

                property.propertyEntity.lat = location.lat
                property.propertyEntity.lng = location.lng

                updatePropertyUseCase.invoke(property.propertyEntity)
            }
        } else if (!isInternetAvailable && propertiesWithoutLocation.isNotEmpty()) {
            _viewAction.emit(ListViewAction.ShowNoInternetWarning)
        }

        val filteredList = if (searchParameters == null) {
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

        if (isTablet) {
            if (filteredList.isEmpty()) {
                if (currentPropertyId != null) {
                    currentPropertyIdRepository.setCurrentId(null)
                }
            } else if (currentPropertyId != null) {
                val currentPropertyStillVisible = filteredList.any { property ->
                    property.propertyEntity.id == currentPropertyId
                }

                if (!currentPropertyStillVisible) {
                    currentPropertyIdRepository.setCurrentId(
                        filteredList.first().propertyEntity.id
                    )
                }
            }
        }

        mapPropertiesIntoViewState(
            propertiesWithPhotosEntity = filteredList,
            isDollar = isDollar,
            currentPropertyId = currentPropertyId,
            isTablet = isTablet
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onDeleteTemporaryPhotoRepository() {
        onDeleteTemporaryPhotoUseCase.invoke()
    }

    fun onNavigateToCreateActivity() {
        _viewAction.tryEmit(ListViewAction.NavigateToCreateActvity)
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet.value = isTablet
    }

    private fun mapPropertiesIntoViewState(
        propertiesWithPhotosEntity: List<PropertyWithPhotosEntity>,
        isDollar: Boolean,
        currentPropertyId: Long?,
        isTablet: Boolean
    ): List<PropertyViewState> {
        return propertiesWithPhotosEntity.map { propertyWithPhotosEntity ->
            PropertyViewState(
                id = propertyWithPhotosEntity.propertyEntity.id,
                type = propertyWithPhotosEntity.propertyEntity.type,
                price = convertMoney(
                    price = "${propertyWithPhotosEntity.propertyEntity.price}",
                    isDollar = isDollar
                ),
                photoList = propertyWithPhotosEntity.photos.map { it },
                city = propertyWithPhotosEntity.propertyEntity.city,
                isSold = propertyWithPhotosEntity.propertyEntity.propertySold,
                onItemClicked = {
                    if (!isTablet) {
                        _viewAction.tryEmit(ListViewAction.NavigateToDetailActivity)
                    }

                    currentPropertyIdRepository.setCurrentId(
                        propertyWithPhotosEntity.propertyEntity.id
                    )
                },
                isSelected =
                    propertyWithPhotosEntity.propertyEntity.id == currentPropertyId
            )
        }
    }

    private fun convertMoney(
        price: String,
        isDollar: Boolean
    ): String {
        val decimalFormat = DecimalFormat("#,###.#")
        val formattedPrice = decimalFormat.format(price.toInt()).toString()

        return if (isDollar) {
            "$formattedPrice $"
        } else {
            "${decimalFormat.format(Utils.convertDollarToEuro(price.toInt()))} €"
        }
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

        return (searchPriceMinimum == null ||
                propertyPrice >= searchPriceMinimum) &&
                (searchPriceMaximum == null ||
                        propertyPrice <= searchPriceMaximum)
    }

    private fun compareSurface(
        searchParameters: SearchParameters,
        property: PropertyWithPhotosEntity
    ): Boolean {
        val searchSurfaceMinimum = searchParameters.surfaceMinimum
        val searchSurfaceMaximum = searchParameters.surfaceMaximum
        val propertySurface = property.propertyEntity.surface

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
                propertyCity.equals(searchCity.trim(), ignoreCase = true)
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
                property.photos.size >= searchParameters.minimumPhotos
    }
}