package com.tuto.realestatemanager.ui.createproperty

import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhotoEntity
import com.tuto.realestatemanager.repository.photo.PhotoRepository
import com.tuto.realestatemanager.repository.property.PropertyRepository
import com.tuto.realestatemanager.ui.editproperty.UpdatePropertyViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository

) : ViewModel() {

    private val typeMutableStateFlow = MutableStateFlow<String?>(null)
    private val priceMutableStateFlow = MutableStateFlow<Int?>(null)
    private val countyMutableStateFlow = MutableStateFlow<String?>(null)
    private val surfaceMutableStateFlow = MutableStateFlow<Int?>(null)
    private val propertyIdMutableStateFlow = MutableStateFlow<Long?>(null)
//    private lateinit var propertyEntity: PropertyEntity
//    private var propertyId: Long = propertyEntity.id

    fun setPropertyId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
    }

    val photo: LiveData<List<TemporaryPhotoEntity>> = photoRepository.getAllTemporaryPhoto().asLiveData(Dispatchers.IO)

    val detailPropertyLiveData: LiveData<UpdatePropertyViewState> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().flatMapLatest { id ->
            propertyRepository.getPropertyById(id).map { propertyWithPhotosEntity ->
                UpdatePropertyViewState(
                    propertyWithPhotosEntity.propertyEntity.id,
                    propertyWithPhotosEntity.propertyEntity.type,
                    propertyWithPhotosEntity.propertyEntity.price,
                    propertyWithPhotosEntity.photos.map { it.photoUri },
                    propertyWithPhotosEntity.propertyEntity.country,
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

    fun getPhoto() {

    }


    fun onTypeSelected(type: String) {
        typeMutableStateFlow.value = type
    }

//    fun onPriceChanged(price: Int){
//        priceMutableStateFlow.value = price
//    }

    fun onCountyChanged(county: String) {
        countyMutableStateFlow.value = county
    }

//    fun onSurfaceChanged(surface: Int){
//        surfaceMutableStateFlow.value = surface
//    }


//    val poiAirport = false
//    val poiTrain = false
//    val poiPark = false
//    val poiBus = false
//    val poiResto = false
//
//    fun poiAirportStatus(): Boolean{
//        if (poiAirport == true)
//            return true
//        return true
//    }

    fun createProperty(
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
        poiPark: Boolean
    ) {
        val saleSince = LocalDate.now().toString()
        val property = PropertyEntity(
            id = 0,
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
        viewModelScope.launch(Dispatchers.IO) {
            val propertyId = propertyRepository.insertProperty(property)

            val temporaryPhotos = photoRepository.getAllTemporaryPhoto().first()

            for (temporaryPhoto in temporaryPhotos) {
                photoRepository.insertPhoto(
                    PhotoEntity(
                        propertyId = propertyId,
                        photoUri = temporaryPhoto.photoUri,
                    )
                )
            }

            photoRepository.flushTemporaryPhotos()
        }


//        val photo = PhotoEntity(id = 0,
//            property.id,
//            photoUrl
//        )
//        viewModelScope.launch(Dispatchers.Main) { photoRepository.insertPhoto(photo) }
    }


    fun createTemporaryPhoto(photoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            photoRepository.insertTemporaryPhoto(TemporaryPhotoEntity(photoUri = photoUrl))
        }
    }

}