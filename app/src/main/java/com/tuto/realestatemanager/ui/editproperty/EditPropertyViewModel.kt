package com.tuto.realestatemanager.ui.editproperty

import android.widget.CheckBox
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.domain.autocomplete.GetPredictionsUseCase
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeletePhotoByIdUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.photo.InsertPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.GetTemporaryPhotoListUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.createproperty.PlaceDetailViewState
import com.tuto.realestatemanager.ui.createproperty.PredictionViewState
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val deletePhotoByIdUseCase: DeletePhotoByIdUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase,
    getTemporaryPhotoListUseCase: GetTemporaryPhotoListUseCase,
    private val deleteTemporaryPhotoUseCase: DeleteTemporaryPhotoUseCase,
    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase,
    private val getPredictionsUseCase: GetPredictionsUseCase,
    private val getUserLocationFlowUseCase: GetUserLocationFlowUseCase,
    private val coroutineDispatchersProvider: CoroutineDispatchersProvider
) : ViewModel() {

    private companion object {
        private const val MINIMUM_ADDRESS_LENGTH = 3
        private const val AUTOCOMPLETE_DELAY_MILLIS = 400L
        private const val DEFAULT_LOCATION = "40.7128,-74.0060"
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }

    private val deletedPhotoIdsMutableStateFlow =
        MutableStateFlow<List<Long>>(emptyList())

    private val addressSearchMutableStateFlow =
        MutableStateFlow<String?>(null)

    private val placeIdMutableStateFlow =
        MutableStateFlow<String?>(null)

    private val temporaryPhotoStateFlow: StateFlow<List<TemporaryPhoto>> =
        getTemporaryPhotoListUseCase.invoke()

    private val _viewAction = MutableSharedFlow<EditViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

    val placeDetailViewState: StateFlow<PlaceDetailViewState?> =
        placeIdMutableStateFlow
            .filterNotNull()
            .mapLatest { placeId ->
                getPlaceAddressComponentsUseCase.invoke(placeId)
            }
            .filterNotNull()
            .map { addressComponents ->
                PlaceDetailViewState(
                    number = addressComponents.streetNumber,
                    address = addressComponents.fullAddress,
                    city = addressComponents.city,
                    zipCode = addressComponents.zipCode,
                    state = addressComponents.state,
                    country = addressComponents.country,
                    lat = addressComponents.lat,
                    lng = addressComponents.lng
                )
            }
            .flowOn(coroutineDispatchersProvider.io)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STOP_TIMEOUT_MILLIS
                ),
                initialValue = null
            )

    val predictionListViewState: StateFlow<List<PredictionViewState>> =
        addressSearchMutableStateFlow
            .map { address ->
                address.orEmpty().trim()
            }
            .distinctUntilChanged()
            .mapLatest { address ->
                if (address.length < MINIMUM_ADDRESS_LENGTH) {
                    emptyList()
                } else {
                    delay(AUTOCOMPLETE_DELAY_MILLIS)

                    val location =
                        getUserLocationFlowUseCase.invoke().first()

                    val localisation = if (location != null) {
                        "${location.latitude},${location.longitude}"
                    } else {
                        DEFAULT_LOCATION
                    }

                    getPredictionsUseCase.invoke(
                        address,
                        localisation
                    ).map { prediction ->
                        PredictionViewState(
                            address = prediction.prediction,
                            id = prediction.placeId
                        )
                    }
                }
            }
            .flowOn(coroutineDispatchersProvider.io)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STOP_TIMEOUT_MILLIS
                ),
                initialValue = emptyList()
            )

    val allPhotoStateFlow: StateFlow<List<EditPropertyPhotoViewState>> =
        combine(
            photoRepository.getAllPhoto(),
            currentPropertyIdRepository.currentIdFlow,
            deletedPhotoIdsMutableStateFlow,
            temporaryPhotoStateFlow
        ) { registeredPhotos, id, deletedPhotoIds, temporaryPhotos ->

            val savedPhotos = registeredPhotos
                .filter { photo ->
                    photo.propertyId == id
                }
                .filter { photo ->
                    photo.id !in deletedPhotoIds
                }
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
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                STOP_TIMEOUT_MILLIS
            ),
            initialValue = emptyList()
        )

    val detailPropertyStateFlow: StateFlow<UpdatePropertyViewState?> =
        currentPropertyIdRepository.currentIdFlow
            .filterNotNull()
            .flatMapLatest { id ->
                propertyRepository.getPropertyById(id)
            }
            .map { propertyEntity ->
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
                    propertyEntity.propertyDateOfSale,
                    propertyEntity.poiTrain,
                    propertyEntity.poiAirport,
                    propertyEntity.poiResto,
                    propertyEntity.poiSchool,
                    propertyEntity.poiBus,
                    propertyEntity.poiPark,
                    propertyEntity.propertySold
                )
            }
            .flowOn(coroutineDispatchersProvider.io)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STOP_TIMEOUT_MILLIS
                ),
                initialValue = null
            )

    fun onAddressSearchChanged(address: String?) {
        addressSearchMutableStateFlow.value = address
    }

    fun onSetAutocompleteAddressId(id: String) {
        placeIdMutableStateFlow.value = id
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
        deletedPhotoIdsMutableStateFlow.value += photoId
    }

    fun setPropertyId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
    }

    fun isChecked(view: CheckBox, boolean: Boolean): Boolean {
        view.isChecked = boolean
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
        lat: Double?,
        lng: Double?,
        description: String,
        room: Int,
        bedroom: Int,
        bathroom: Int,
        agent: String,
        isSold: Boolean,
        wasSold: Boolean,
        previousDateOfSale: String,
        poiTrain: Boolean,
        saleSince: String,
        poiAirport: Boolean,
        poiResto: Boolean,
        poiSchool: Boolean,
        poiBus: Boolean,
        poiPark: Boolean
    ) {
        val dateOfSale = when {
            !isSold -> "Not yet sold"
            wasSold -> previousDateOfSale
            else -> Utils.todayDate()
        }

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

        viewModelScope.launch(coroutineDispatchersProvider.io) {
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

            _viewAction.emit(
                EditViewAction.NavigateFromEditToDetailActivity
            )
        }
    }

    fun clearTemporaryPhotos() {
        onDeleteTemporaryPhotoUseCase.invoke()
    }
}