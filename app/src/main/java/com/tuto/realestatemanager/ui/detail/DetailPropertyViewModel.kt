package com.tuto.realestatemanager.ui.detail

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class DetailPropertyViewModel @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    val priceConverterRepository: PriceConverterRepository,
    private val propertyRepository: PropertyRepository,
    coroutineDispatchersProvider : CoroutineDispatchersProvider
) : ViewModel() {

    private val isDollar = priceConverterRepository.isDollarStateFlow.value

    val detailPropertyLiveData: LiveData<PropertyDetailViewState> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().flatMapLatest { id ->
            propertyRepository.getPropertyWithPhotoById(id).map { propertyWithPhotosEntity ->
                PropertyDetailViewState(
                    id = propertyWithPhotosEntity.propertyEntity.id,
                    type = propertyWithPhotosEntity.propertyEntity.type,
                    convertMoney(propertyWithPhotosEntity.propertyEntity.price.toString(), isDollar),
//                    price = propertyWithPhotosEntity.propertyEntity.price,
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
            date
        }else{
            Utils.formatToUS(date)
        }
        return convertDate
    }

    fun isVisible(view: ImageView, isVisible: Boolean): Boolean{
        view.isVisible = false
        if(isVisible) view.isVisible = true
        return view.isVisible
    }




}