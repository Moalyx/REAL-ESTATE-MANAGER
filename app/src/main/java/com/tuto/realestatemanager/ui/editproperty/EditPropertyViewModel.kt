package com.tuto.realestatemanager.ui.editproperty

import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.repository.photo.PhotoRepository
import com.tuto.realestatemanager.repository.property.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val currentPropertyIdRepository: CurrentPropertyIdRepository

): ViewModel() {

    fun setPropertyId(id: Long){
        currentPropertyIdRepository.setCurrentId(id)
    }

    val detailPropertyLiveData: LiveData<UpdatePropertyViewState> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().flatMapLatest { id ->
            propertyRepository.getPropertyById(id).map { propertyWithPhotosEntity ->
                UpdatePropertyViewState(
                    propertyWithPhotosEntity.propertyEntity.id,
                    propertyWithPhotosEntity.propertyEntity.type,
                    propertyWithPhotosEntity.propertyEntity.price,
                    propertyWithPhotosEntity.photos.map { it.url },
                    propertyWithPhotosEntity.propertyEntity.county,
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

    fun isChecked(view: CheckBox, boolean: Boolean): Boolean{
        view.isChecked = false
        if(boolean) view.isChecked = true
        return view.isChecked
    }

    fun updateProperty(
        id: Long,
        type: String,
        price: Int,
        county: String,
        surface: Int,
        description: String,
        room: Int,
        bedroom: Int,
        bathroom: Int,
        poiTrain: Boolean,
        poiAirport: Boolean,
        poiResto: Boolean,
        poiSchool: Boolean,
        poiBus: Boolean,
        poiPark: Boolean,
        photoUrl: String
    ){
        val saleSince = LocalDate.now().toString()
        val property = PropertyEntity(
            id,
            type,
            price,
            county,
            surface,
            description,
            room,
            bedroom,
            bathroom,
            saleSince,
            poiTrain,
            poiAirport,
            poiResto,
            poiSchool,
            poiBus,
            poiPark
        )
        viewModelScope.launch(Dispatchers.IO) { propertyRepository.updateProperty(property) }

        val photo = PhotoEntity(id = 0,
            1,
            photoUrl
        )
        viewModelScope.launch(Dispatchers.Main) { photoRepository.upDatePhoto(photo) }
    }

}