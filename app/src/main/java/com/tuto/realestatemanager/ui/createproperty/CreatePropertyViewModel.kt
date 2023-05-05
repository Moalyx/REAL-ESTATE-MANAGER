package com.tuto.realestatemanager.ui.createproperty

import androidx.lifecycle.*
import com.tuto.realestatemanager.data.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Collections.emptyList
import javax.inject.Inject

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase,
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val autocompleteRepository: AutocompleteRepository,
    temporaryPhotoRepository: TemporaryPhotoRepository

) : ViewModel() {

    private val addressSearchMutableStateFlow = MutableStateFlow<String?>(null)
    private val placeIdMutableStateFlow = MutableStateFlow<String?>(null)
    private val photosUrlMutableStateFlow = MutableStateFlow<List<String>>(emptyList())


    val photo: LiveData<List<String>> = photosUrlMutableStateFlow.asLiveData(Dispatchers.IO)


    fun onGetAutocompleteAddressId(id: String) {
        placeIdMutableStateFlow.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val placeDetailAddress: LiveData<AddressComponentsEntity> =
        placeIdMutableStateFlow.filterNotNull().mapLatest {
            getPlaceAddressComponentsUseCase.invoke(it)
        }.filterNotNull()
            .asLiveData(Dispatchers.IO)

    val placeDetailViewState: LiveData<PlaceDetailViewState> = placeDetailAddress.map {
        PlaceDetailViewState(
            number = it.streetNumber,
            address = it.fullAddress,
            city = it.city,
            zipCode = it.zipCode,
            state = it.state,
            country = it.country,
            lat = it.lat,
            lng = it.lng
        )
    }

    private val temporaryPhotoStateFlow: StateFlow<List<TemporaryPhoto>> =
        temporaryPhotoRepository.getTemporaryPhotoList()

    val temporaryPhotoLiveData: LiveData<List<TemporaryPhoto>> =
        temporaryPhotoStateFlow.asLiveData()

    fun onAddressSearchChanged(address: String?) {
        addressSearchMutableStateFlow.value = address
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val predictions: LiveData<PredictionResponse> =
        addressSearchMutableStateFlow.filterNotNull().mapLatest {
            autocompleteRepository.getAutocompleteResult(it)
        }.asLiveData(Dispatchers.IO)

    val predictionListViewState: LiveData<List<PredictionViewState>> = predictions.map {
        it.predictions.map { predictions ->
            PredictionViewState(
                predictions.structuredFormatting?.mainText.toString(),
                predictions.structuredFormatting?.secondaryText.toString(),
                predictions.structuredFormatting?.mainText.toString(),
                predictions.structuredFormatting?.mainText.toString(),
                predictions.structuredFormatting?.mainText.toString(),
                predictions.structuredFormatting?.mainText.toString(),
                predictions.placeId!!
            )
        }
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
        lat: Double,
        lng: Double,
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

            for (temporaryPhoto in temporaryPhotoStateFlow.value) {
                photoRepository.insertPhoto(
                    PhotoEntity(
                        propertyId = propertyId,
                        photoUri = temporaryPhoto.uri,
                        photoTitle = temporaryPhoto.title
                    )
                )
            }
        }
    }
}