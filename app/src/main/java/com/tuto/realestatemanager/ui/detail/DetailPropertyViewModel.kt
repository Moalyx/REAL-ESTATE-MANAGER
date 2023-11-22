package com.tuto.realestatemanager.ui.detail

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.domain.usecase.currentproperty.CurrentIdFlowUseCase
import com.tuto.realestatemanager.domain.usecase.priceconverter.IsDollarFlowUseCase
import com.tuto.realestatemanager.domain.usecase.property.GetPropertyWithPhotosByIdUseCase
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class DetailPropertyViewModel @Inject constructor(
    currentIdFlowUseCase: CurrentIdFlowUseCase,
    isDollarFlowUseCase: IsDollarFlowUseCase,
    private val getPropertyWithPhotosByIdUseCase: GetPropertyWithPhotosByIdUseCase,
    coroutineDispatchersProvider : CoroutineDispatchersProvider
) : ViewModel() {

    private val isDollar = isDollarFlowUseCase.invoke().value

    val detailPropertyLiveData: LiveData<PropertyDetailViewState> =
        currentIdFlowUseCase.invoke().filterNotNull().flatMapLatest { id ->
            getPropertyWithPhotosByIdUseCase.invoke(id).map { propertyWithPhotosEntity ->
                PropertyDetailViewState(
                    id = propertyWithPhotosEntity.propertyEntity.id,
                    type = propertyWithPhotosEntity.propertyEntity.type,
                    convertMoney(propertyWithPhotosEntity.propertyEntity.price.toString(), isDollar),
                    photoList = propertyWithPhotosEntity.photos.map { it },
                    address = propertyWithPhotosEntity.propertyEntity.address,
                    city = propertyWithPhotosEntity.propertyEntity.city,
                    zipcode = propertyWithPhotosEntity.propertyEntity.zipCode,
                    state = propertyWithPhotosEntity.propertyEntity.state,
                    country = propertyWithPhotosEntity.propertyEntity.country,
                    surface = propertyWithPhotosEntity.propertyEntity.surface,
                    description = propertyWithPhotosEntity.propertyEntity.description,
                    room = propertyWithPhotosEntity.propertyEntity.room,
                    bathroom = propertyWithPhotosEntity.propertyEntity.bedroom,
                    bedroom = propertyWithPhotosEntity.propertyEntity.bathroom,
                    agent = propertyWithPhotosEntity.propertyEntity.agent,
                    isSold = propertyWithPhotosEntity.propertyEntity.propertySold,
                    saleSince = convertDate(propertyWithPhotosEntity.propertyEntity.propertyOnSaleSince, isDollar),
                    saleDate = propertyWithPhotosEntity.propertyEntity.propertyDateOfSale,
                    poiTrain = propertyWithPhotosEntity.propertyEntity.poiTrain,
                    poiAirport = propertyWithPhotosEntity.propertyEntity.poiAirport,
                    poiResto = propertyWithPhotosEntity.propertyEntity.poiResto,
                    poiSchool = propertyWithPhotosEntity.propertyEntity.poiSchool,
                    poiBus = propertyWithPhotosEntity.propertyEntity.poiBus,
                    poiPark = propertyWithPhotosEntity.propertyEntity.poiPark
                )
            }
        }.asLiveData(coroutineDispatchersProvider.io)

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

    private fun convertDate(date: String, isDollar: Boolean): String{
        val convertDate = if (isDollar){
            Utils.formatToUS(date)
        }else{
            date
        }
        return convertDate
    }

    fun isVisible(view: ImageView, isVisible: Boolean): Boolean{
        view.isVisible = false
        if(isVisible) view.isVisible = true
        return view.isVisible
    }

    val navigateSingleLiveEvent: SingleLiveEvent<DetailViewAction> = SingleLiveEvent()

    fun onNavigateToEditActivity(){
        navigateSingleLiveEvent.setValue(DetailViewAction.NavigateToEditActivity)
    }

    fun onNavigatetoMainActivity(){
        navigateSingleLiveEvent.setValue(DetailViewAction.NavigateToMainActivity)
    }

}