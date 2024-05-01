package com.tuto.realestatemanager.ui.editproperty

import android.widget.CheckBox
import androidx.lifecycle.*
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.geocoding.model.Location
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.domain.usecase.geocode.GetLatLngPropertyLocationUseCase
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.ui.createproperty.CreateViewAction
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditPropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val getLatLngPropertyLocationUseCase: GetLatLngPropertyLocationUseCase


    ) : ViewModel() {

    val getAllPhotoLiveData: LiveData<List<EditPropertyPhotoViewState>> = liveData {
        combine(
            photoRepository.getAllPhoto(),
            currentPropertyIdRepository.currentIdFlow
        ) { registeredPhotos, id ->

            val filteredPhoto = registeredPhotos.filter { photo -> photo.propertyId == id }
            val mappedPhoto = filteredPhoto.map { photo ->
                EditPropertyPhotoViewState(
                    photo.id,
                    photo.photoTitle,
                    photo.photoUri
                )
            }
            emit(mappedPhoto)
        }.collect()
    }

    fun setPropertyId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
    }

    val detailPropertyLiveData: LiveData<UpdatePropertyViewState> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().flatMapLatest { id ->
            propertyRepository.getPropertyById(id).map { propertyEntity ->

                UpdatePropertyViewState(
                    propertyEntity.id,
                    propertyEntity.type,
                    propertyEntity.price,
                    propertyEntity.address,
                    propertyEntity.city,
                    propertyEntity.zipCode,
                    propertyEntity.state,
                    propertyEntity.country,
                    propertyEntity.lat,
                    propertyEntity.lng,
                    propertyEntity.surface,
                    propertyEntity.description,
                    propertyEntity.agent,
                    propertyEntity.room,
                    propertyEntity.bedroom,
                    propertyEntity.bathroom,
                    propertyEntity.propertyOnSaleSince,
                    propertyEntity.poiTrain,
                    propertyEntity.poiAirport,
                    propertyEntity.poiResto,
                    propertyEntity.poiSchool,
                    propertyEntity.poiBus,
                    propertyEntity.poiPark,
                    propertyEntity.propertySold
                )
            }
        }.asLiveData(Dispatchers.IO)

    fun isChecked(view: CheckBox, boolean: Boolean): Boolean {
        view.isChecked = false
        if (boolean) view.isChecked = true
        return view.isChecked
    }

    fun updateProperty(
        id: Long,
        type: String,
        price: Int,
        address: String,
        city: String,
        state: String,
        zipcode: Int,
        country: String,
        surface: Int,
        lat: Double,
        lng: Double,
        description: String,
        room: Int,
        bedroom: Int,
        bathroom: Int,
        agent: String,
        isSold: Boolean,
        poiTrain: Boolean,
        saleSince: String,
        poiAirport: Boolean,
        poiResto: Boolean,
        poiSchool: Boolean,
        poiBus: Boolean,
        poiPark: Boolean
    ) {
        val dateOfSale = "Not yet sold"
        val property = PropertyEntity(
            id = id,
            type = type,
            price = price,
            address = address,
            city = city,
            state = state,
            zipCode = zipcode,
            country = country,
            surface = surface,
            lat = lat,
            lng = lng,
            description = description,
            room = room,
            bedroom = bedroom,
            bathroom = bathroom,
            agent = agent,
            propertySold = isSold,
            propertyOnSaleSince = saleSince,
            propertyDateOfSale = dateOfSale,
            poiTrain = poiTrain,
            poiAirport = poiAirport,
            poiResto = poiResto,
            poiSchool = poiSchool,
            poiBus = poiBus,
            poiPark = poiPark
        )
        viewModelScope.launch(Dispatchers.IO) {
            propertyRepository.updateProperty(property)
        }

    }

    val navigateSingleLiveEvent: SingleLiveEvent<EditViewAction> = SingleLiveEvent()

    fun onNavigateToDetailActivity(){
        navigateSingleLiveEvent.setValue(EditViewAction.NavigateFromEditToDetailActivity)
    }


}