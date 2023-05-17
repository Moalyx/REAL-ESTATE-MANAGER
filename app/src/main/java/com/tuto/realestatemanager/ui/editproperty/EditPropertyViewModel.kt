package com.tuto.realestatemanager.ui.editproperty

import android.widget.CheckBox
import androidx.lifecycle.*
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class EditPropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    temporaryPhotoRepository: TemporaryPhotoRepository

) : ViewModel() {

    //val actionSingleLiveEvent = SingleLiveEvent<EditActivityViewAction>()

    private var addedPhotoMutableList = mutableListOf<TemporaryPhoto>()
    private var updatedRegisteredPhotoMutableList = mutableListOf<PhotoEntity>()


    private val getRegisteredPhoto: LiveData<List<PhotoEntity>> =
        photoRepository.getAllPhoto().asLiveData(Dispatchers.IO)

    private val getAddedPhoto: LiveData<List<TemporaryPhoto>> =
        temporaryPhotoRepository.getTemporaryPhotoList().asLiveData(Dispatchers.Main)

    private val currentPropertyId: LiveData<Long> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().asLiveData(Dispatchers.IO)

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



        if (addedPhoto == null) {
            getAllPhotoMediatorLiveData.value = registeredPhoto.map { photo ->
                EditPropertyPhotoViewState(
                    photoTitle = photo.photoTitle,
                    photoUri = photo.photoUri,
                )
            }
            updatedRegisteredPhotoMutableList = registeredPhoto as MutableList<PhotoEntity>

        }else{

            val photoList = mutableListOf<EditPropertyPhotoViewState>()
            //photoList.clear()

//            for (photo in registeredPhoto) {
//                photoList.add(
//                    EditPropertyPhotoViewState(
//                        photoTitle = photo.photoTitle,
//                        photoUri = photo.photoUri,
//
//                        )
//                )
//            }
            for (photo in addedPhoto) {
                photoList.add(
                    EditPropertyPhotoViewState(
                        photoTitle = photo.title,
                        photoUri = photo.uri

                    )
                )
            }

            updatedRegisteredPhotoMutableList = registeredPhoto as MutableList<PhotoEntity>

            addedPhotoMutableList = addedPhoto as MutableList<TemporaryPhoto>


            getAllPhotoMediatorLiveData.value = photoList
        }

//            for (temporaryPhoto in addedPhoto!!/*temporaryPhotoStateFlow.value*/) { //todo pluto utilise le mediator ici avec getphoto.value!! a verifier
//                viewModelScope.launch {
//                    photoRepository.insertPhoto(
//                        PhotoEntity(
//                            propertyId = currentPropertyId!!,
//                            photoUri = temporaryPhoto.uri,
//                            photoTitle = temporaryPhoto.title
//                        )
//                    )
//                }
//            }
//            getAllPhotoMediatorLiveData.value = toViewState(registeredPhoto, addedPhoto)

    }

    private fun toViewState(
        registeredPhoto: List<PhotoEntity>,
        addedPhoto: List<TemporaryPhoto>
    ): List<EditPropertyPhotoViewState> {

        val photoList = mutableListOf<EditPropertyPhotoViewState>()

        for (photo in registeredPhoto) {
            photoList.add(
                EditPropertyPhotoViewState(
                    photoTitle = photo.photoTitle,
                    photoUri = photo.photoUri,

                    )
            )
        }
        for (photo in addedPhoto) {
            photoList.add(
                EditPropertyPhotoViewState(
                    photoTitle = photo.title,
                    photoUri = photo.uri

                )
            )
        }

        updatedRegisteredPhotoMutableList = registeredPhoto as MutableList<PhotoEntity>

        addedPhotoMutableList = addedPhoto as MutableList<TemporaryPhoto>


        return photoList
    }

    val getPhoto: LiveData<List<EditPropertyPhotoViewState>> = getAllPhotoMediatorLiveData


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
                    //propertyWithPhotosEntity.photos.map { it },
                    propertyWithPhotosEntity.propertyEntity.address,
                    propertyWithPhotosEntity.propertyEntity.city,
                    propertyWithPhotosEntity.propertyEntity.zipCode,
                    propertyWithPhotosEntity.propertyEntity.state,
                    propertyWithPhotosEntity.propertyEntity.country,
                    propertyWithPhotosEntity.propertyEntity.lat,
                    propertyWithPhotosEntity.propertyEntity.lng,
                    propertyWithPhotosEntity.propertyEntity.surface,
                    propertyWithPhotosEntity.propertyEntity.description,
                    propertyWithPhotosEntity.propertyEntity.agent,
                    propertyWithPhotosEntity.propertyEntity.room,
                    propertyWithPhotosEntity.propertyEntity.bedroom,
                    propertyWithPhotosEntity.propertyEntity.bathroom,
                    propertyWithPhotosEntity.propertyEntity.propertyOnSaleSince,
                    propertyWithPhotosEntity.propertyEntity.poiTrain,
                    propertyWithPhotosEntity.propertyEntity.poiAirport,
                    propertyWithPhotosEntity.propertyEntity.poiResto,
                    propertyWithPhotosEntity.propertyEntity.poiSchool,
                    propertyWithPhotosEntity.propertyEntity.poiBus,
                    propertyWithPhotosEntity.propertyEntity.poiPark,
                    propertyWithPhotosEntity.propertyEntity.propertySold
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
        viewModelScope.launch(Dispatchers.IO) {
            propertyRepository.updateProperty(property)


            for (temporaryPhoto in addedPhotoMutableList/*temporaryPhotoStateFlow.value*/) { //todo pluto utilise le mediator ici avec getphoto.value!! a verifier
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