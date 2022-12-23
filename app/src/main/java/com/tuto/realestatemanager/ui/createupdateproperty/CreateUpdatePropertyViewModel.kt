package com.tuto.realestatemanager.ui.createupdateproperty

import android.os.Build
import android.view.View
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.repository.photo.PhotoRepository
import com.tuto.realestatemanager.repository.property.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateUpdatePropertyViewModel @Inject constructor(
    val propertyRepository: PropertyRepository,
    val photoRepository: PhotoRepository

): ViewModel(){

    private val typeMutableStateFlow= MutableStateFlow<String?>(null)
    private val priceMutableStateFlow = MutableStateFlow<Int?>(null)
    private val countyMutableStateFlow = MutableStateFlow<String?>(null)
    private val surfaceMutableStateFlow = MutableStateFlow<Int?>(null)

    fun onTypeSelected(type: String){
        typeMutableStateFlow.value = type
    }

//    fun onPriceChanged(price: Int){
//        priceMutableStateFlow.value = price
//    }

    fun onCountyChanged(county: String){
        countyMutableStateFlow.value = county
    }

//    fun onSurfaceChanged(surface: Int){
//        surfaceMutableStateFlow.value = surface
//    }


    val poiAirport = false
    val poiTrain = false
    val poiPark = false
    val poiBus = false
    val poiResto = false

    fun poiAirportStatus(): Boolean{
        if (poiAirport == true)
            return true
        return true
    }

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
        poiPark: Boolean,
        photoUrl: String
    ){
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
        viewModelScope.launch(Dispatchers.IO) { propertyRepository.insertProperty(property) }

        val photo = PhotoEntity(id = 0,
            1,
            photoUrl
        )
        viewModelScope.launch(Dispatchers.Main) { photoRepository.insertPhoto(photo) }
    }

//    fun createPhoto(url: String){
//        val photo = PhotoEntity(
//            id = 0,
//            0,
//            url
//        )
//
//    }



//    fun onCreatePropertyOnDatabase(/*type: String, county: String*/){
//       val property = PropertyEntity(
//           0,
//           typeMutableStateFlow.value!!,
//           10,
//           priceMutableStateFlow.value!!,
//           countyMutableStateFlow.value!!,
//           10
//       )
//       viewModelScope.launch { propertyRepository.insertProperty(property) }
//    }


}