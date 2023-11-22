package com.tuto.realestatemanager.ui.main

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.repository.location.LocationRepository
import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepository
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val priceConverterRepository: PriceConverterRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var isTablet: Boolean = false

    val navigateSingleLiveEvent: SingleLiveEvent<MainViewAction> = SingleLiveEvent()

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }

    fun converterPrice(){
        priceConverterRepository.convertPrice()
    }
    val iconStatus: LiveData<Boolean> = priceConverterRepository.isDollarStateFlow.asLiveData(
        Dispatchers.IO)

    fun navigateToSearch(){
        navigateSingleLiveEvent.setValue(MainViewAction.NavigateToSearch)
    }

    fun onResume() {
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){

        }
    }

}