package com.tuto.realestatemanager.ui.detail

import androidx.lifecycle.ViewModel
import com.tuto.realestatemanager.ui.main.MainViewAction
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(

) : ViewModel() {

    val navigateSingleLiveEvent: SingleLiveEvent<DetailViewAction> = SingleLiveEvent()

    fun onNavigateToMainActivity(){
        navigateSingleLiveEvent.setValue(DetailViewAction.NavigateToMainActivity)
    }

    fun onNavigateToEditActivity(){
        navigateSingleLiveEvent.setValue(DetailViewAction.NavigateToEditActivity)
    }

}