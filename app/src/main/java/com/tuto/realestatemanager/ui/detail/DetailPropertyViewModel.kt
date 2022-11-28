package com.tuto.realestatemanager.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.repository.PropertyRepository
import com.tuto.realestatemanager.ui.list.PropertyViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailPropertyViewModel @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val propertyRepository: PropertyRepository
): ViewModel() {

    val detailPropertyLiveData: LiveData<PropertyDetailViewState> = currentPropertyIdRepository.currentIdLiveData.switchMap { id ->
        propertyRepository.getPropertyByIdLiveData(id).map {
            PropertyDetailViewState(
                it.id,
                it.type,
                it.price,
                it.photoList,
                it.county,
                it.surface
            )
        }
    }


}