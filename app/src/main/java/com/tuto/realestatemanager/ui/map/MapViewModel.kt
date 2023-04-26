package com.tuto.realestatemanager.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    propertyRepository: PropertyRepository
) : ViewModel() {


    val propertyList: Flow<List<PropertyWithPhotosEntity>> = propertyRepository.getAllPropertiesWithPhotosEntity()

    val mapViewStateList : LiveData<List<PropertyWithPhotosEntity>> = propertyList.filterNotNull().asLiveData(Dispatchers.IO)


}