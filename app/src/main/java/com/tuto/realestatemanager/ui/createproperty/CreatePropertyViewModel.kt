package com.tuto.realestatemanager.ui.createproperty

import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.domain.place.GetPlaceAddressComponentsUseCase
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Collections.emptyList
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val getPlaceAddressComponentsUseCase: GetPlaceAddressComponentsUseCase,
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val autocompleteRepository: AutocompleteRepository,
    temporaryPhotoRepository: TemporaryPhotoRepository

) : ViewModel() {

    private val typeMutableStateFlow = MutableStateFlow<String?>(null)
    private val priceMutableStateFlow = MutableStateFlow<Int?>(null)
    private val countyMutableStateFlow = MutableStateFlow<String?>(null)
    private val surfaceMutableStateFlow = MutableStateFlow<Int?>(null)
    private val propertyIdMutableStateFlow = MutableStateFlow<Long?>(null)
    private val addressSearchMutableStateFlow = MutableStateFlow<String?>(null)
    private val placeIdMutableStateFlow = MutableStateFlow<String?>(null)
    private val photoEntityMutableStateFlow = MutableStateFlow<List<TemporaryPhoto>>(emptyList())

//    private lateinit var propertyEntity: PropertyEntity
//    private var propertyId: Long = propertyEntity.id

    fun setPropertyId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
    }

    //private val photoMutableStateFlow = MutableStateFlow<List<String>>()
    private val photosUrlMutableStateFlow = MutableStateFlow<List<String>>(emptyList())
    private val photosTitleMutableStateFlow = MutableStateFlow<List<String>>(emptyList())
    var temporaryPhotoMutableStateFlow = MutableStateFlow<TemporaryPhoto?>(null)

    //    val photo: LiveData<CreateTempPhoto> = createTempPhotoMutableStateFlow.filterNotNull().asLiveData(Dispatchers.IO)
    val photo: LiveData<List<String>> = photosUrlMutableStateFlow.asLiveData(Dispatchers.IO)

    fun createTemporaryPhoto(photoUrl: String?/*, photoTitle : String?*/) {


        photosUrlMutableStateFlow.update {
            it + photoUrl!!
        }
//        photosTitleMutableStateFlow.update {
//            it + photoTitle!!
//        }

//        val listTempPhoto = arrayListOf<CreateTempPhoto>()
//        listTempPhoto.add( createTempPhotoMutableStateFlow.value = CreateTempPhoto(photosUrlMutableStateFlow.value, photosTitleMutableStateFlow.value))
//

    }



//    fun createTemporaryPhoto(photoUrl: String?, photoTitle : String?) {
//
//    }

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

    private val temporaryPhotoMutableStateFlow2 = temporaryPhotoRepository.getTemporaryPhotoList().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000, 0), emptyList())
    val temporaryPhoto : LiveData<List<TemporaryPhoto>> = temporaryPhotoRepository.getTemporaryPhotoList().asLiveData(Dispatchers.IO)

    fun onAddressSearchChanged(address: String?) {
        addressSearchMutableStateFlow.value = address
    }

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

    fun createPhoto(temporaryPhoto: List<TemporaryPhoto>){
        photoEntityMutableStateFlow.value = temporaryPhoto
    }

    fun createProperty(
        type: String,
        price: Int,
        address : String,
        city : String,
        state : String,
        zipcode : Int,
        country: String,
        surface: Int,
        lat : Double,
        lng : Double,
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

            for (temporaryPhoto in temporaryPhotoMutableStateFlow2.value/*photosUrlMutableStateFlow.value*/) {
                photoRepository.insertPhoto(
                    PhotoEntity(
                        propertyId = propertyId,
                        photoUri = temporaryPhoto.uri!!,
                        photoTitle = temporaryPhoto.title!!
                    )
                )
            }
        }
    }
}