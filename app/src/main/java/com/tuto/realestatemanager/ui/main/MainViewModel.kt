package com.tuto.realestatemanager.ui.main

import androidx.lifecycle.ViewModel
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

    private var isTablet: Boolean = false

    val navigateSingleLiveEvent = SingleLiveEvent<MainViewAction>()

    init {
        navigateSingleLiveEvent.addSource(currentPropertyIdRepository.currentIdLiveData) {
            if (!isTablet) {
                navigateSingleLiveEvent.setValue(MainViewAction.NavigateToDetailActivity)
            }
        }
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }


}