package com.tuto.realestatemanager.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdIdRepositoryImpl
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.location.LocationRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import com.tuto.realestatemanager.ui.main.MainViewAction
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    currentPropertyIdRepositoryImpl: CurrentPropertyIdIdRepositoryImpl,
    propertyRepository: PropertyRepository
) : ViewModel() {


    private val propertyList: Flow<List<PropertyWithPhotosEntity>> = propertyRepository.getAllPropertiesWithPhotosEntity()

    val mapViewStateList : LiveData<List<PropertyWithPhotosEntity>> = propertyList.filterNotNull().asLiveData(Dispatchers.IO)

    val userLocation : LiveData<Location> = locationRepository.getUserLocation().asLiveData(Dispatchers.IO)

//    private var isTablet: Boolean = false //todo verifier pourquoi ici cela ne marche pas
//
//    val navigateSingleLiveEvent: SingleLiveEvent<MapViewAction> = SingleLiveEvent()
//
//    init {
//        navigateSingleLiveEvent.addSource(currentPropertyIdRepositoryImpl.currentIdFlow.filterNotNull().asLiveData()) {
//            if (!isTablet) {
//                navigateSingleLiveEvent.setValue(MapViewAction.NavigateToDetailActivity)
//            }
//        }
//    }
//
//    fun onConfigurationChanged(isTablet: Boolean) {
//        this.isTablet = isTablet
//    }


}