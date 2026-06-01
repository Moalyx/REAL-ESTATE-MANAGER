package com.tuto.realestatemanager.ui.detail

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.tuto.realestatemanager.domain.usecase.currentproperty.CurrentIdFlowUseCase
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetPropertyWithPhotosByIdUseCase
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class DetailPropertyViewModel @Inject constructor(
    currentIdFlowUseCase: CurrentIdFlowUseCase,
    isDollarFlowUseCase: IsDollarFlowUseCase,
    private val getPropertyWithPhotosByIdUseCase: GetPropertyWithPhotosByIdUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase
) : ViewModel() {

    private val getCurrentPropertyFlow = currentIdFlowUseCase.invoke()
        .onEach { photoUriMutableStateFlow.value = null }
        .flatMapLatest { currentId ->
            if (currentId == null) {
                flowOf(null)
            } else {
                getPropertyWithPhotosByIdUseCase.invoke(currentId)
            }
        }

    private val photoUriMutableStateFlow = MutableStateFlow<String?>(null)

    fun setUri(uri: String) {
        photoUriMutableStateFlow.tryEmit(uri)
    }

    fun getUri(): LiveData<String> =
        photoUriMutableStateFlow.filterNotNull().asLiveData(Dispatchers.IO)

    val detailPropertyLiveData: LiveData<PropertyDetailViewState?> = liveData {
        combine(
            photoUriMutableStateFlow,
            getCurrentPropertyFlow,
            isDollarFlowUseCase.invoke(),
            isInternetAvailableUseCase.invoke()
        ) { photoUri, propertyWithPhotosEntity, isDollar, hasInternet ->

            if (propertyWithPhotosEntity == null) {
                null
            } else {
                PropertyDetailViewState(
                    id = propertyWithPhotosEntity.propertyEntity.id,
                    type = propertyWithPhotosEntity.propertyEntity.type,
                    price = convertMoney(
                        propertyWithPhotosEntity.propertyEntity.price.toString(),
                        isDollar
                    ),
                    photoList = propertyWithPhotosEntity.photos.map { it },
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
                        propertyWithPhotosEntity.propertyEntity.propertyOnSaleSince,
                        isDollar
                    ),
                    saleDate = if (
                        propertyWithPhotosEntity.propertyEntity.propertySold &&
                        propertyWithPhotosEntity.propertyEntity.propertyDateOfSale != "Not yet sold"
                    ) {
                        convertDate(propertyWithPhotosEntity.propertyEntity.propertyDateOfSale, isDollar)
                    } else {
                        propertyWithPhotosEntity.propertyEntity.propertyDateOfSale
                    },
                    poiTrain = propertyWithPhotosEntity.propertyEntity.poiTrain,
                    poiAirport = propertyWithPhotosEntity.propertyEntity.poiAirport,
                    poiResto = propertyWithPhotosEntity.propertyEntity.poiResto,
                    poiSchool = propertyWithPhotosEntity.propertyEntity.poiSchool,
                    poiBus = propertyWithPhotosEntity.propertyEntity.poiBus,
                    poiPark = propertyWithPhotosEntity.propertyEntity.poiPark,
                    photoUri = photoUri
                        ?: propertyWithPhotosEntity.photos.firstOrNull()?.photoUri
                        ?: "",
                    hasInternet = hasInternet
                )
            }
        }.collect {
            emit(it)
        }
    }

    private fun convertMoney(price: String, isDollar: Boolean): String {
        val decimalFormat = DecimalFormat("#,###.#")
        val formatPrice: String = decimalFormat.format(price.toInt()).toString().trim()
        val convertPrice: String = if (isDollar) {
            "$formatPrice $"
        } else {
            decimalFormat.format(Utils.convertDollarToEuro(price.toInt())) + " €"
        }
        return convertPrice
    }

    private fun convertDate(date: String, isDollar: Boolean): String =
        if (isDollar) Utils.formatToUS(date) else date


    fun isVisible(view: ImageView, isVisible: Boolean): Boolean {
        view.isVisible = false
        if (isVisible) view.isVisible = true
        return view.isVisible
    }

    val navigateSingleLiveEvent: SingleLiveEvent<DetailViewAction> = SingleLiveEvent()

    fun onNavigateToEditActivity() {
        navigateSingleLiveEvent.setValue(DetailViewAction.NavigateToEditActivity)
    }

//    fun onNavigateToMainActivity(){
//        navigateSingleLiveEvent.setValue(DetailViewAction.NavigateToMainActivity)
//    }

}