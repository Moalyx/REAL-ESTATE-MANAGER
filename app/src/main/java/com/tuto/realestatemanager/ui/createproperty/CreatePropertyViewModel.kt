package com.tuto.realestatemanager.ui.createproperty

import android.widget.CheckBox
import androidx.lifecycle.*
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.model.CreateTempPhoto
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.repository.autocomplete.model.Predictions
import com.tuto.realestatemanager.repository.photo.PhotoRepository
import com.tuto.realestatemanager.repository.placedetail.PlaceDetailRepository
import com.tuto.realestatemanager.repository.property.PropertyRepository
import com.tuto.realestemanager.repository.placedetail.model.PlaceDetailResponse
import com.tuto.realestemanager.repository.placedetail.model.PlaceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Collections.emptyList
import java.util.Collections.indexOfSubList
import javax.inject.Inject

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val autocompleteRepository: AutocompleteRepository,
    private val placeDetailRepository: PlaceDetailRepository

) : ViewModel() {

    private val typeMutableStateFlow = MutableStateFlow<String?>(null)
    private val priceMutableStateFlow = MutableStateFlow<Int?>(null)
    private val countyMutableStateFlow = MutableStateFlow<String?>(null)
    private val surfaceMutableStateFlow = MutableStateFlow<Int?>(null)
    private val propertyIdMutableStateFlow = MutableStateFlow<Long?>(null)
    private val addressSearchMutableStateFlow = MutableStateFlow<String?>(null)
    private val placeIdMutableStateFlow = MutableStateFlow<String?>(null)

//    private lateinit var propertyEntity: PropertyEntity
//    private var propertyId: Long = propertyEntity.id

    fun setPropertyId(id: Long) {
        currentPropertyIdRepository.setCurrentId(id)
    }

    //private val photoMutableStateFlow = MutableStateFlow<List<String>>()
    private val photosUrlMutableStateFlow = MutableStateFlow<List<String>>(emptyList())
    private val photosTitleMutableStateFlow = MutableStateFlow<List<String>>(emptyList())
    var createTempPhotoMutableStateFlow = MutableStateFlow<CreateTempPhoto?>(null)

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

    private val placeDetailAddress: LiveData<PlaceDetailResponse> =
        placeIdMutableStateFlow.filterNotNull().mapLatest {
            placeDetailRepository.getAdressById(it)
        }.asLiveData(Dispatchers.IO)

    val placeDetailViewState: LiveData<PlaceDetailViewState> = placeDetailAddress.map {
        PlaceDetailViewState(
            getStreetNumber(it.placeResult!!),
            getStreetAdress(it.placeResult!!),
            getCity(it.placeResult!!),
            getZipcode(it.placeResult!!),
            getState(it.placeResult!!),
            getCountry(it.placeResult!!)
        )
    }

    fun getStreetNumber(placeResult: PlaceResult): String {
        var streetNumber = ""
        for (result in placeResult.addressComponents) {
            if (result.types.get(0).equals("street_number")) {
                streetNumber = result.longName.toString()
            }
        }
        return streetNumber
    }

    private fun getStreetAdress(placeResult: PlaceResult): String {
        var streetAddress = ""
        for (result in placeResult.addressComponents) {
            if (result.types.get(0).equals("route")) {
                streetAddress = result.longName.toString()
            }
        }
        return streetAddress
    }

    private fun getCity(placeResult: PlaceResult): String {
        var city = ""
        for (result in placeResult.addressComponents) {
            if (result.types.get(0).equals("locality")) {
                city = result.longName.toString()
            }
        }
        return city
    }

    private fun getState(placeResult: PlaceResult): String {
        var state = ""
        for (result in placeResult.addressComponents) {
            if (result.types.get(0).equals("administrative_area_level_2")) {
                state = result.longName.toString()
            }
        }
        return state
    }

    private fun getCountry(placeResult: PlaceResult): String {
        var country = ""
        for (result in placeResult.addressComponents) {
            if (result.types.get(0).equals("country")) {
                country = result.longName.toString()
            }
        }
        return country
    }

    private fun getZipcode(placeResult: PlaceResult): String {
        var zipcode = ""
        for (result in placeResult.addressComponents) {
            if (result.types.get(0).equals("postal_code")) {
                zipcode = result.longName.toString()
            }
        }
        return zipcode
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

    fun onGetAutocompleteAddressId(id: String) {
        placeIdMutableStateFlow.value = id
    }


    fun onAddressSearchChanged(address: String?) {
        addressSearchMutableStateFlow.value = address
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

            for (temporaryPhotoUrl in photosUrlMutableStateFlow.value) {
                photoRepository.insertPhoto(
                    PhotoEntity(
                        propertyId = propertyId,
                        photoUri = temporaryPhotoUrl
                    )
                )
            }
        }
    }
}