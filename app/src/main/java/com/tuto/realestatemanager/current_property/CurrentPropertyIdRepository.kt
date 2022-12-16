package com.tuto.realestatemanager.current_property

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdRepository @Inject constructor() {

    private val currentPropertyIdMutableStateFlow = MutableStateFlow<Long?>(null)

    @MainThread
    fun setCurrentId(currentId: Long) {
        currentPropertyIdMutableStateFlow.value = currentId
    }

    val currentIdFlow: Flow<Long?> = currentPropertyIdMutableStateFlow
}