package com.tuto.realestatemanager.ui.createproperty

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.repository.location.LocationRepository
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.domain.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.domain.autocomplete.GetPredictionsUseCase
import com.tuto.realestatemanager.domain.autocomplete.model.PredictionAddressEntity
import com.tuto.realestatemanager.domain.place.CoroutineDispatchersProvider
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import com.tuto.realestatemanager.domain.usecase.location.GetUserLocationFlowUseCase
import com.tuto.realestatemanager.domain.usecase.photo.InsertPhotoUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.GetTemporaryPhotoListUseCase
import com.tuto.realestatemanager.domain.usecase.temporaryphoto.OnDeleteTemporaryPhotoUseCase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import com.tuto.realestatemanager.ui.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase,
    private val getPredictionsUseCase: GetPredictionsUseCase,
    private val propertyRepository: PropertyRepository,
    getTemporaryPhotoListUseCase: GetTemporaryPhotoListUseCase,
    private val coroutineDispatchersProvider: CoroutineDispatchersProvider,
    private val onDeleteTemporaryPhotoUseCase: OnDeleteTemporaryPhotoUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    converterRepository: PriceConverterRepository,
    private val dispatcher : CoroutineContext,
    private val getUserLocationFlowUseCase: GetUserLocationFlowUseCase
) : ViewModel() {

    private val addressSearchMutableStateFlow = MutableStateFlow<String?>(null)
    private val placeIdMutableStateFlow = MutableStateFlow<String?>(null)

    fun onSetAutocompleteAddressId(id: String) {
        placeIdMutableStateFlow.value = id
    }

    private val isDollar: Boolean = converterRepository.isDollarStateFlow.value

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

    private val predictionsFlow: Flow<Pair<String, Location>> = flow {
        combine(
            addressSearchMutableStateFlow.filterNotNull(),
            getUserLocationFlowUseCase.invoke()
        ) { address, location ->

            emit(Pair(address, location))


        }.collect()
    }

    private val predictionResponseLiveData: LiveData<List<PredictionAddressEntity>> =
        predictionsFlow.mapLatest {
            getPredictionsUseCase.invoke(
                it.first,
                "${it.second.latitude},${it.second.longitude}"

            )
        }.asLiveData(coroutineDispatchersProvider.io)

    private val temporaryPhotoStateFlow: StateFlow<List<TemporaryPhoto>> =
        getTemporaryPhotoListUseCase.invoke()

    val temporaryPhotoLiveData: LiveData<List<TemporaryPhoto>> =
        temporaryPhotoStateFlow.asLiveData()

    fun onAddressSearchChanged(address: String?) {
        addressSearchMutableStateFlow.value = address
    }

    val predictionListViewState: LiveData<List<PredictionViewState>> =
        predictionResponseLiveData.map {
            it.map { predictions ->
                PredictionViewState(
                    predictions.prediction,
                    predictions.placeId
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

        val saleSince: String = Utils.todayDate()

        val dateOfSale = "Not yet sold"
        val property = PropertyEntity(
            type = type,
            price = convertMoney(price.toString(), isDollar).toInt(),
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
                insertPhotoUseCase.invoke(
                    PhotoEntity(
                        propertyId = propertyId,
                        photoUri = temporaryPhoto.uri,
                        photoTitle = temporaryPhoto.title
                    )
                )
            }
            onDeleteTemporaryPhotoUseCase.invoke()
        }
    }

    private fun convertMoney(price: String, isDollar: Boolean): String =
        if (isDollar) price else Utils.convertEuroToDollar(price.toInt()).toString()

    val navigateSingleLiveEvent: SingleLiveEvent<CreateViewAction> = SingleLiveEvent()

    fun onNavigateToMainActivity() {
        navigateSingleLiveEvent.setValue(CreateViewAction.NavigateToMainActivity)
    }

}