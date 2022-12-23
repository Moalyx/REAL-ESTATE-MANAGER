package com.tuto.realestatemanager.ui.detail

import androidx.lifecycle.*
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.repository.property.PropertyRepository
import com.tuto.realestatemanager.repository.property.PropertyRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DetailPropertyViewModel @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    val detailPropertyLiveData: LiveData<PropertyDetailViewState> =
        currentPropertyIdRepository.currentIdFlow.filterNotNull().flatMapLatest { id ->
            propertyRepository.getPropertyById(id).map { propertyWithPhotosEntity ->
                PropertyDetailViewState(
                    propertyWithPhotosEntity.propertyEntity.id,
                    propertyWithPhotosEntity.propertyEntity.type,
                    propertyWithPhotosEntity.propertyEntity.price,
                    propertyWithPhotosEntity.photos.map { it.url },
                    propertyWithPhotosEntity.propertyEntity.county,
                    propertyWithPhotosEntity.propertyEntity.surface,
                    propertyWithPhotosEntity.propertyEntity.description,
                    propertyWithPhotosEntity.propertyEntity.room,
                    propertyWithPhotosEntity.propertyEntity.bedroom,
                    propertyWithPhotosEntity.propertyEntity.bathroom,
                    propertyWithPhotosEntity.propertyEntity.propertyOnSaleSince,
                    propertyWithPhotosEntity.propertyEntity.poiTrain,
                    propertyWithPhotosEntity.propertyEntity.poiAirport,
                    propertyWithPhotosEntity.propertyEntity.poiResto,
                    propertyWithPhotosEntity.propertyEntity.poiSchool,
                    propertyWithPhotosEntity.propertyEntity.poiBus,
                    propertyWithPhotosEntity.propertyEntity.poiPark
                )
            }
        }.asLiveData(Dispatchers.IO)


}