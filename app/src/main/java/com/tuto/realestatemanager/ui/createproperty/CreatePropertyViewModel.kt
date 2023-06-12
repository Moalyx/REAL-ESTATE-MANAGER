package com.tuto.realestatemanager.ui.createproperty

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.tuto.realestatemanager.MainApplication
import com.tuto.realestatemanager.data.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase,
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val autocompleteRepository: AutocompleteRepository,
    private val clock: Clock,
    private val coroutineDispatchersProvider: CoroutineDispatchersProvider,
    private val temporaryPhotoRepository: TemporaryPhotoRepository,
) : ViewModel() {

    private val addressSearchMutableStateFlow = MutableStateFlow<String?>(null)
    private val placeIdMutableStateFlow = MutableStateFlow<String?>(null)

    fun onGetAutocompleteAddressId(id: String) {
        placeIdMutableStateFlow.value = id
    }

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
    private val predictionResponseLiveData: LiveData<PredictionResponse> =
        addressSearchMutableStateFlow.filterNotNull().mapLatest {
            autocompleteRepository.getAutocompleteResult(it)
        }.asLiveData(Dispatchers.IO)

    val predictionListViewState: LiveData<List<PredictionViewState>> =
        predictionResponseLiveData.map {
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
        agent: String,
        isSold: Boolean,
        poiTrain: Boolean,
        poiAirport: Boolean,
        poiResto: Boolean,
        poiSchool: Boolean,
        poiBus: Boolean,
        poiPark: Boolean,
    ) {

//        if (type.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty()){
////            || zipcode.toString().isEmpty() || country.isEmpty() || surface.toString() == "" || description.isEmpty()
////            || room.toString() == "" || bedroom.toString() == "" || bathroom.toString() == "" || agent.isEmpty()){
//
//
//
//
//        }else{

//        val saleSince = LocalDate.now(clock).toString()
        val saleSince = Utils.todayDate()
        val dateOfSale = "estate available for sale"
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

            for (temporaryPhoto in temporaryPhotoStateFlow.value) {
                photoRepository.insertPhoto(
                    PhotoEntity(
                        propertyId = propertyId,
                        photoUri = temporaryPhoto.uri,
                        photoTitle = temporaryPhoto.title
                    )
                )
            }
            temporaryPhotoRepository.onDeleteTemporaryPhotoRepo()
        }


    }
}