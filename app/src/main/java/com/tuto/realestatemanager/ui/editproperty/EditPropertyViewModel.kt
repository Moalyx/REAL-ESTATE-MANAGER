package com.tuto.realestatemanager.ui.editproperty

import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditPropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val temporaryPhotoRepository: TemporaryPhotoRepository

) : ViewModel() {

    fun setPropertyId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
    }

    val detailPropertyLiveData: LiveData<UpdatePropertyViewState> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().flatMapLatest { id ->
            propertyRepository.getPropertyById(id).map { propertyWithPhotosEntity ->
                UpdatePropertyViewState(
                    propertyWithPhotosEntity.propertyEntity.id,
                    propertyWithPhotosEntity.propertyEntity.type,
                    propertyWithPhotosEntity.propertyEntity.price,
                    propertyWithPhotosEntity.photos.map { it },
                    propertyWithPhotosEntity.propertyEntity.address,
                    propertyWithPhotosEntity.propertyEntity.city,
                    propertyWithPhotosEntity.propertyEntity.zipCode,
                    propertyWithPhotosEntity.propertyEntity.state,
                    propertyWithPhotosEntity.propertyEntity.country,
                    propertyWithPhotosEntity.propertyEntity.lat,
                    propertyWithPhotosEntity.propertyEntity.lng,
                    propertyWithPhotosEntity.propertyEntity.surface,
                    propertyWithPhotosEntity.propertyEntity.description,
                    propertyWithPhotosEntity.propertyEntity.room,
                    propertyWithPhotosEntity.propertyEntity.bedroom,
                    propertyWithPhotosEntity.propertyEntity.bathroom,
                    propertyWithPhotosEntity.propertyEntity.propertyOnSaleSince,
                    propertyWithPhotosEntity.propertyEntity.poiTrain,
                    propertyWithPhotosEntity.propertyEntity.poiAirport,
                    propertyWithPhotosEntity.propertyEntity.poiResto,
                    propertyWithPhotosEntity.propertyEntity.poiSchool,
                    propertyWithPhotosEntity.propertyEntity.poiBus,
                    propertyWithPhotosEntity.propertyEntity.poiPark
                )
            }
        }.asLiveData(Dispatchers.IO)

    fun isChecked(view: CheckBox, boolean: Boolean): Boolean {
        view.isChecked = false
        if (boolean) view.isChecked = true
        return view.isChecked
    }

    private val temporaryPhotoStateFlow: StateFlow<List<TemporaryPhoto>> =
        temporaryPhotoRepository.getTemporaryPhotoList() //TODO A IMPLEMENTER

    val temporaryPhotoLiveData: LiveData<List<TemporaryPhoto>> =
        temporaryPhotoStateFlow.asLiveData() // TODO A IMPLEMENTER

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
        val dateOfSale = LocalDate.now().toString()
        val property = PropertyEntity(
            id,
            type,
            price,
            address,
            city,
            state,
            zipcode,
            country,
            surface,
            lat,
            lng,
            description,
            room,
            bedroom,
            bathroom,
            agent,
            isSold,
            dateOfSale,
            saleSince,
            poiTrain,
            poiAirport,
            poiResto,
            poiSchool,
            poiBus,
            poiPark
        )
        viewModelScope.launch(Dispatchers.IO) {
            propertyRepository.updateProperty(property)


            for (temporaryPhoto in temporaryPhotoStateFlow.value) {
                photoRepository.insertPhoto(
                    PhotoEntity(
                        propertyId = id,
                        photoUri = temporaryPhoto.uri,
                        photoTitle = temporaryPhoto.title
                    )
                )
            }
        }

//        val photo = PhotoEntity(id = 0,
//            property.id,
//            photoUri
//        )
//        viewModelScope.launch(Dispatchers.Main) { photoRepository.upDatePhoto(photo) }
    }

}