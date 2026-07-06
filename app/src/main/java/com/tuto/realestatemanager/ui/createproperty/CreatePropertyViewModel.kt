package com.tuto.realestatemanager.ui.createproperty

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.domain.autocomplete.GetPredictionsUseCase
import com.tuto.realestatemanager.domain.autocomplete.model.PredictionAddressEntity
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import com.tuto.realestatemanager.domain.usecase.internetconnectivity.IsInternetAvailableUseCase
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.photo.DeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.photo.InsertPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.GetTemporaryPhotoListUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase,
    private val getPredictionsUseCase: GetPredictionsUseCase,
    private val propertyRepository: PropertyRepository,
    getTemporaryPhotoListUseCase: GetTemporaryPhotoListUseCase,
    private val coroutineDispatchersProvider: CoroutineDispatchersProvider,
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val deleteTemporaryPhotoUseCase: DeleteTemporaryPhotoUseCase,
    getUserLocationFlowUseCase: GetUserLocationFlowUseCase,
    isInternetAvailableUseCase: IsInternetAvailableUseCase
) : ViewModel() {

    private val addressSearchMutableStateFlow = MutableStateFlow<String?>(null)
    private val placeIdMutableStateFlow = MutableStateFlow<String?>(null)

    val navigateSingleLiveEvent: SingleLiveEvent<CreateViewAction> = SingleLiveEvent()

    val hasInternetLiveData: LiveData<Boolean> =
        isInternetAvailableUseCase.invoke()
            .distinctUntilChanged()
            .asLiveData()

    private val placeDetailAddress: LiveData<AddressComponentsEntity> =
        placeIdMutableStateFlow
            .filterNotNull()
            .mapLatest { placeId ->
                getPlaceAddressComponentsUseCase.invoke(placeId)
            }
            .filterNotNull()
            .asLiveData(coroutineDispatchersProvider.io)

    val placeDetailViewState: LiveData<PlaceDetailViewState> =
        placeDetailAddress.map { addressComponents ->
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

    private val predictionsFlow: Flow<Pair<String, Location?>> =
        combine(
            addressSearchMutableStateFlow
                .filterNotNull()
                .filter { address -> address.length >= 3 },
            getUserLocationFlowUseCase.invoke()
        ) { address, location ->
            address to location
        }

    private val predictionResponseLiveData: LiveData<List<PredictionAddressEntity>> =
        predictionsFlow
            .mapLatest { (address, location) ->
                val localisation = if (location != null) {
                    "${location.latitude},${location.longitude}"
                } else {
                    "40.7128,-74.0060"
                }

                getPredictionsUseCase.invoke(
                    address,
                    localisation
                )
            }
            .asLiveData(coroutineDispatchersProvider.io)

    val predictionListViewState: LiveData<List<PredictionViewState>> =
        predictionResponseLiveData.map { predictions ->
            predictions.map { prediction ->
                PredictionViewState(
                    address = prediction.prediction,
                    id = prediction.placeId
                )
            }
        }

    private val temporaryPhotoStateFlow: StateFlow<List<TemporaryPhoto>> =
        getTemporaryPhotoListUseCase.invoke()

    val temporaryPhotoLiveData: LiveData<List<TemporaryPhoto>> =
        temporaryPhotoStateFlow.asLiveData(coroutineDispatchersProvider.io)

    fun onAddressSearchChanged(address: String?) {
        addressSearchMutableStateFlow.value = address
    }

    fun onSetAutocompleteAddressId(id: String) {
        placeIdMutableStateFlow.value = id
    }

    fun deleteTemporaryPhoto(temporaryPhoto: TemporaryPhoto) {
        deleteTemporaryPhotoUseCase.invoke(temporaryPhoto)
    }

    fun createProperty(
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
        poiTrain: Boolean,
        poiAirport: Boolean,
        poiResto: Boolean,
        poiSchool: Boolean,
        poiBus: Boolean,
        poiPark: Boolean
    ) {
        val saleSince = Utils.todayDate()
        val dateOfSale = if (isSold) Utils.todayDate() else "Not yet sold"

        val property = PropertyEntity(
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
            val propertyId = propertyRepository.insertProperty(property)

            temporaryPhotoStateFlow.value.forEach { temporaryPhoto ->
                insertPhotoUseCase.invoke(
                    PhotoEntity(
                        propertyId = propertyId,
                        photoUri = temporaryPhoto.uri,
                        photoTitle = temporaryPhoto.title
                    )
                )
            }

            onDeleteTemporaryPhotoUseCase.invoke()
            navigateSingleLiveEvent.postValue(CreateViewAction.NavigateToMainActivity)
        }
    }

    fun onNavigateToMainActivity() {
        navigateSingleLiveEvent.setValue(CreateViewAction.NavigateToMainActivity)
    }

    fun clearTemporaryPhotos() {
        onDeleteTemporaryPhotoUseCase.invoke()
    }
}