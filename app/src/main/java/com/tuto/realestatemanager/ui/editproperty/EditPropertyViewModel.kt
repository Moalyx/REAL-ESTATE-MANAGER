package com.tuto.realestatemanager.ui.editproperty

import android.widget.CheckBox
import androidx.lifecycle.*
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditPropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    temporaryPhotoRepository: TemporaryPhotoRepository

) : ViewModel() {

    //val actionSingleLiveEvent = SingleLiveEvent<EditActivityViewAction>()

    private val photoList = mutableListOf<EditPropertyPhotoViewState>()
    private var addedPhotoMutableList = mutableListOf<TemporaryPhoto>()
    private var updatedRegisteredPhotoMutableList = mutableListOf<PhotoEntity>()


    private val getRegisteredPhoto: LiveData<List<PhotoEntity>> =
        photoRepository.getAllPhoto().asLiveData(Dispatchers.IO)

    private val getAddedPhoto: LiveData<List<TemporaryPhoto>> =
        temporaryPhotoRepository.getTemporaryPhotoList().asLiveData(Dispatchers.Main)

    private val currentPropertyId: LiveData<Long> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().asLiveData(Dispatchers.IO)

    val getAllPhotoLiveData: LiveData<List<EditPropertyPhotoViewState>> = liveData {
        combine(
            photoRepository.getAllPhoto(),
            temporaryPhotoRepository.getTemporaryPhotoList()
        ) { registeredPhoto, newPhoto ->
            if (newPhoto == null) {
                emit(registeredPhoto.map { photo ->
                    EditPropertyPhotoViewState(
                        id = photo.id,
                        photoTitle = photo.photoTitle,
                        photoUri = photo.photoUri,
                    )
                }
                )
            } else {
                for (photo in registeredPhoto) {
                    photoList.add(
                        EditPropertyPhotoViewState(
                            photo.id,
                            photoTitle = photo.photoTitle,
                            photoUri = photo.photoUri,

                            )
                    )
                }

                for (photo in newPhoto) {
                    photoList.add(
                        EditPropertyPhotoViewState(
                            photo.id,
                            photoTitle = photo.title,
                            photoUri = photo.uri

                        )
                    )
                }
                emit(photoList)
            }

        }.collect()
    }

    private val getAllPhotoMediatorLiveData: MediatorLiveData<List<EditPropertyPhotoViewState>> =
        MediatorLiveData<List<EditPropertyPhotoViewState>>().apply {

            addSource(getRegisteredPhoto) { oldPhoto ->
                combine(oldPhoto, getAddedPhoto.value, currentPropertyId.value)
            }

            addSource(getAddedPhoto) { addedPhoto ->
                combine(getRegisteredPhoto.value, addedPhoto, currentPropertyId.value)
            }

            addSource(currentPropertyId) { currentPropertyId ->
                combine(getRegisteredPhoto.value, getAddedPhoto.value, currentPropertyId)
            }

        }

    private fun combine(
        registeredPhoto: List<PhotoEntity>?,
        addedPhoto: List<TemporaryPhoto>?,
        currentPropertyId: Long?
    ) {
        registeredPhoto ?: return

        //photoList.clear()

        if (addedPhoto == null) {
            getAllPhotoMediatorLiveData.value = registeredPhoto.map { photo ->
                EditPropertyPhotoViewState(
                    id = photo.id,
                    photoTitle = photo.photoTitle,
                    photoUri = photo.photoUri,
                )
            }
            updatedRegisteredPhotoMutableList = registeredPhoto.toMutableList()

        } else {

            //photoList.clear()


            for (photo in registeredPhoto) {
                if (!registeredPhoto.contains(photo))
                    photoList.add(
                        EditPropertyPhotoViewState(
                            photo.id,
                            photoTitle = photo.photoTitle,
                            photoUri = photo.photoUri,

                            )
                    )
            }

            for (photo in addedPhoto) {
                if (!addedPhoto.contains(photo))
                    photoList.add(
                        EditPropertyPhotoViewState(
                            photo.id,
                            photoTitle = photo.title,
                            photoUri = photo.uri

                        )
                    )
            }


            updatedRegisteredPhotoMutableList = registeredPhoto.toMutableList()
//
            addedPhotoMutableList = addedPhoto.toMutableList()


            getAllPhotoMediatorLiveData.value = photoList


        }
    }


    val getPhoto: LiveData<List<EditPropertyPhotoViewState>> = getAllPhotoMediatorLiveData

//    fun OnDeletePhoto(id: Long) {
//        photoRepository.deletePhotoById(id)
//       // photoList.remove()
//    }

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
//                    y.photos.map { it },
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

//    private val temporaryPhotoStateFlow: StateFlow<List<TemporaryPhoto>> =
//        temporaryPhotoRepository.getTemporaryPhotoList() //TODO A IMPLEMENTER
//
//    val temporaryPhotoLiveData: LiveData<List<TemporaryPhoto>> =
//        temporaryPhotoStateFlow.asLiveData() // TODO A IMPLEMENTER

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
        viewModelScope.launch(Dispatchers.IO) { photoRepository.deleteAllPropertyPhotos(id) }

        viewModelScope.launch(Dispatchers.IO) {
            propertyRepository.updateProperty(property)

//            photoRepository.deleteAllPropertyPhotos(id)

//            if (photoList.isNotEmpty()) {
            for (temporaryPhoto in photoList/*temporaryPhotoStateFlow.value*/) { //todo pluto utilise le mediator ici avec getphoto.value!! a verifier

                photoRepository.insertPhoto(
                    PhotoEntity(
                        propertyId = id,
                        photoUri = temporaryPhoto.photoUri,
                        photoTitle = temporaryPhoto.photoTitle
                    )
                )
            }
            photoList.clear()
//            updatedRegisteredPhotoMutableList.clear()
//            addedPhotoMutableList.clear()

//            }
        }

        //photoList.clear()
//        updatedRegisteredPhotoMutableList.clear()
//        addedPhotoMutableList.clear()

    }
}