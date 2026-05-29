package com.tuto.realestatemanager.ui.editproperty

import android.widget.CheckBox
import androidx.lifecycle.*
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.domain.usecase.geocode.GetLatLngPropertyLocationUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeletePhotoByIdUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.photo.InsertPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.GetTemporaryPhotoListUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val getLatLngPropertyLocationUseCase: GetLatLngPropertyLocationUseCase,
    private val deletePhotoByIdUseCase: DeletePhotoByIdUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase,
    private val getTemporaryPhotoListUseCase: GetTemporaryPhotoListUseCase,
    private val deleteTemporaryPhotoUseCase: DeleteTemporaryPhotoUseCase


    ) : ViewModel() {

    private val deletedPhotoIdsMutableStateFlow = MutableStateFlow<List<Long>>(emptyList())
    private val addedPhotosMutableStateFlow = MutableStateFlow<List<TemporaryPhoto>>(emptyList())

    fun addTemporaryPhotoInEdit(temporaryPhoto: TemporaryPhoto) {
        addedPhotosMutableStateFlow.value += temporaryPhoto
    }

    val getAllPhotoLiveData: LiveData<List<EditPropertyPhotoViewState>> = liveData {
        combine(
            photoRepository.getAllPhoto(),
            currentPropertyIdRepository.currentIdFlow,
            deletedPhotoIdsMutableStateFlow,
            temporaryPhotoStateFlow
        ) { registeredPhotos, id, deletedPhotoIds, temporaryPhotos ->

            val savedPhotos = registeredPhotos
                .filter { photo -> photo.propertyId == id }
                .filter { photo -> photo.id !in deletedPhotoIds }
                .map { photo ->
                    EditPropertyPhotoViewState(
                        id = photo.id,
                        photoTitle = photo.photoTitle,
                        photoUri = photo.photoUri,
                        isTemporary = false

                    )
                }

            val addedTemporaryPhotos = temporaryPhotos.map { temporaryPhoto ->
                EditPropertyPhotoViewState(
                    id = temporaryPhoto.id,
                    photoTitle = temporaryPhoto.title,
                    photoUri = temporaryPhoto.uri,
                    isTemporary = true
                )
            }

            savedPhotos + addedTemporaryPhotos

        }.collect { photos ->
            emit(photos)
        }
    }

    fun onDeleteEditPhoto(photo: EditPropertyPhotoViewState) {
        if (photo.isTemporary) {
            deleteTemporaryPhotoUseCase.invoke(
                TemporaryPhoto(
                    id = photo.id,
                    title = photo.photoTitle,
                    uri = photo.photoUri
                )
            )
        } else {
            markPhotoAsDeleted(photo.id)
        }
    }

    fun markPhotoAsDeleted(photoId: Long) {
        deletedPhotoIdsMutableStateFlow.value =
            deletedPhotoIdsMutableStateFlow.value + photoId
    }

    fun setPropertyId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
    }

    private val temporaryPhotoStateFlow: StateFlow<List<TemporaryPhoto>> =
        getTemporaryPhotoListUseCase.invoke()


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
                    propertyEntity.bathroom,
                    propertyEntity.bedroom,
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

            deletedPhotoIdsMutableStateFlow.value.forEach { photoId ->
                deletePhotoByIdUseCase.invoke(photoId)
            }

            temporaryPhotoStateFlow.value.forEach { temporaryPhoto ->
                insertPhotoUseCase.invoke(
                    PhotoEntity(
                        propertyId = id,
                        photoUri = temporaryPhoto.uri,
                        photoTitle = temporaryPhoto.title
                    )
                )
            }

            onDeleteTemporaryPhotoUseCase.invoke()
            deletedPhotoIdsMutableStateFlow.value = emptyList()
        }

    }

    fun deletePhoto(photoId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deletePhotoByIdUseCase.invoke(photoId)
        }
    }

    fun clearTemporaryPhotos() {
        onDeleteTemporaryPhotoUseCase.invoke()
    }

    val navigateSingleLiveEvent: SingleLiveEvent<EditViewAction> = SingleLiveEvent()

    fun onNavigateToDetailActivity(){
        navigateSingleLiveEvent.setValue(EditViewAction.NavigateFromEditToDetailActivity)
    }


}