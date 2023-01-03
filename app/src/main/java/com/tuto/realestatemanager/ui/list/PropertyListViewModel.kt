package com.tuto.realestatemanager.ui.list

import androidx.lifecycle.*
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.repository.property.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor(
    propertyRepository: PropertyRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

//    val propertyListLiveData: LiveData<List<PropertyViewState>> = propertyRepository.getAllProperties().map { propertyEntities ->
//            propertyEntities.map { propertyWithPhotosEntity->
//                PropertyViewState(
//                    id = propertyWithPhotosEntity.propertyEntity.id,
//                    type = propertyWithPhotosEntity.propertyEntity.type,
//                    price = propertyWithPhotosEntity.propertyEntity.price,
//                    photoList = propertyWithPhotosEntity.photos.map { it.url },
//                    county = propertyWithPhotosEntity.propertyEntity.county,
//                    onItemClicked = {
//                        currentPropertyIdRepository.setCurrentId(propertyWithPhotosEntity.propertyEntity.id)
//                    }
//                )
//            }
//        }.asLiveData(Dispatchers.IO)

    val propertyListLiveData: LiveData<List<PropertyViewState>> =
        propertyRepository.getAllPropertiesWithPhotosEntity().map { propertyEntities ->
            propertyEntities.map { propertyWithPhotosEntity ->
                PropertyViewState(
                    id = propertyWithPhotosEntity.propertyEntity.id,
                    type = propertyWithPhotosEntity.propertyEntity.type,
                    price = propertyWithPhotosEntity.propertyEntity.price,
                    photoList = propertyWithPhotosEntity.photos.map { it.photoUri },
                    county = propertyWithPhotosEntity.propertyEntity.country,
                    onItemClicked = {
                        currentPropertyIdRepository.setCurrentId(propertyWithPhotosEntity.propertyEntity.id)
                    }
                )
            }
        }.asLiveData(Dispatchers.IO)

//    val propertyListLiveData2: LiveData<List<PropertyViewState>> = liveData(Dispatchers.IO) {
//        propertyRepository.getAllProperties().map { propertyEntities ->
//            propertyEntities.map {
//                PropertyViewState(
//                    id = it.id,
//                    type = it.type,
//                    price = it.price,
//                    photoList = it.photoList,
//                    county = it.county,
//                    onItemClicked = {
//                        currentPropertyIdRepository.setCurrentId(it.id)
//                    }
//                )
//            }
//        }.collect {
//            emit(it)
//        }
//    }
}