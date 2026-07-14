package com.tuto.realestatemanager.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.domain.usecase.currentproperty.CurrentIdFlowUseCase
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetPropertyWithPhotosByIdUseCase
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class DetailPropertyViewModel @Inject constructor(
    currentIdFlowUseCase: CurrentIdFlowUseCase,
    isDollarFlowUseCase: IsDollarFlowUseCase,
    private val getPropertyWithPhotosByIdUseCase: GetPropertyWithPhotosByIdUseCase,
    isInternetAvailableUseCase: IsInternetAvailableUseCase
) : ViewModel() {

    private companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }

    private val _selectedPhotoUri = MutableStateFlow<String?>(null)
    val selectedPhotoUri: StateFlow<String?> = _selectedPhotoUri.asStateFlow()

    private val currentPropertyFlow = currentIdFlowUseCase.invoke()
        .onEach {
            _selectedPhotoUri.value = null
        }
        .flatMapLatest { currentId ->
            if (currentId == null) {
                flowOf(null)
            } else {
                getPropertyWithPhotosByIdUseCase.invoke(currentId)
            }
        }

    private val _viewAction = MutableSharedFlow<DetailViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

    val detailPropertyStateFlow: StateFlow<PropertyDetailViewState?> = combine(
        _selectedPhotoUri,
        currentPropertyFlow,
        isDollarFlowUseCase.invoke(),
        isInternetAvailableUseCase.invoke()
    ) { selectedPhotoUri, propertyWithPhotosEntity, isDollar, hasInternet ->

        if (propertyWithPhotosEntity == null) {
            null
        } else {
            PropertyDetailViewState(
                id = propertyWithPhotosEntity.propertyEntity.id,
                type = propertyWithPhotosEntity.propertyEntity.type,
                price = convertMoney(
                    price = propertyWithPhotosEntity.propertyEntity.price.toString(),
                    isDollar = isDollar
                ),
                photoList = propertyWithPhotosEntity.photos,
                address = propertyWithPhotosEntity.propertyEntity.address,
                city = propertyWithPhotosEntity.propertyEntity.city,
                zipcode = propertyWithPhotosEntity.propertyEntity.zipCode,
                state = propertyWithPhotosEntity.propertyEntity.state,
                country = propertyWithPhotosEntity.propertyEntity.country,
                surface = propertyWithPhotosEntity.propertyEntity.surface,
                description = propertyWithPhotosEntity.propertyEntity.description,
                room = propertyWithPhotosEntity.propertyEntity.room,
                bedroom = propertyWithPhotosEntity.propertyEntity.bedroom,
                bathroom = propertyWithPhotosEntity.propertyEntity.bathroom,
                agent = propertyWithPhotosEntity.propertyEntity.agent,
                isSold = propertyWithPhotosEntity.propertyEntity.propertySold,
                saleSince = convertDate(
                    date = propertyWithPhotosEntity.propertyEntity.propertyOnSaleSince,
                    isDollar = isDollar
                ),
                saleDate = if (
                    propertyWithPhotosEntity.propertyEntity.propertySold &&
                    propertyWithPhotosEntity.propertyEntity.propertyDateOfSale != "Not yet sold"
                ) {
                    convertDate(
                        date = propertyWithPhotosEntity.propertyEntity.propertyDateOfSale,
                        isDollar = isDollar
                    )
                } else {
                    propertyWithPhotosEntity.propertyEntity.propertyDateOfSale
                },
                poiTrain = propertyWithPhotosEntity.propertyEntity.poiTrain,
                poiAirport = propertyWithPhotosEntity.propertyEntity.poiAirport,
                poiResto = propertyWithPhotosEntity.propertyEntity.poiResto,
                poiSchool = propertyWithPhotosEntity.propertyEntity.poiSchool,
                poiBus = propertyWithPhotosEntity.propertyEntity.poiBus,
                poiPark = propertyWithPhotosEntity.propertyEntity.poiPark,
                photoUri = selectedPhotoUri
                    ?: propertyWithPhotosEntity.photos.firstOrNull()?.photoUri
                    ?: "",
                hasInternet = hasInternet
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = null
    )

    fun setUri(uri: String) {
        _selectedPhotoUri.value = uri
    }

    fun onNavigateToEditActivity() {
        _viewAction.tryEmit(DetailViewAction.NavigateToEditActivity)
    }

    private fun convertMoney(
        price: String,
        isDollar: Boolean
    ): String {
        val decimalFormat = DecimalFormat("#,###.#")
        val formattedPrice = decimalFormat.format(price.toInt()).trim()

        return if (isDollar) {
            "$formattedPrice $"
        } else {
            "${decimalFormat.format(Utils.convertDollarToEuro(price.toInt()))} €"
        }
    }

    private fun convertDate(
        date: String,
        isDollar: Boolean
    ): String {
        return if (isDollar) {
            Utils.formatToUS(date)
        } else {
            date
        }
    }
}