package com.tuto.realestatemanager.ui.main

import androidx.lifecycle.ViewModel
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

    private var isTablet: Boolean = false

    val navigateSingleLiveEvent = SingleLiveEvent<MainViewAction>()

    init {

            if (!isTablet){
                navigateSingleLiveEvent.setValue(MainViewAction.NavigateToDetailActivity)
            }

    }

    fun onConfigurationChanged(isTablet : Boolean){
        this.isTablet = isTablet
    }






}