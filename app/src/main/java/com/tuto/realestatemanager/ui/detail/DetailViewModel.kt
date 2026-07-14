package com.tuto.realestatemanager.ui.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {

    private val _viewAction = MutableSharedFlow<DetailViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

    fun onNavigateToMainActivity() {
        _viewAction.tryEmit(DetailViewAction.NavigateToMainActivity)
    }
}