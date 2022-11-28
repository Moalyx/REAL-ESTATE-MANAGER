package com.tuto.realestatemanager.ui.list

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.repository.PropertyRepository
import com.tuto.realestatemanager.ui.detail.DetailActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor(
    propertyRepository: PropertyRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

    val propertyListLiveData: LiveData<List<PropertyViewState>> = propertyRepository.propertiesListLiveData.map { propertyEntities ->
            propertyEntities.map {
                PropertyViewState(
                    id = it.id,
                    type = it.type,
                    price = it.price,
                    photoList = it.photoList,
                    county = it.county,
                    onItemClicked = {
                        currentPropertyIdRepository.setCurrentId(it.id)
                    }
                )
            }
        }
}