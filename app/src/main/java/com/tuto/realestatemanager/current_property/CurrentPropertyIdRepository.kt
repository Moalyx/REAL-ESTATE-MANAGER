package com.tuto.realestatemanager.current_property

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdRepository @Inject constructor() {

    private val currentPropertyIdMutableLiveData = MutableLiveData<Long>()

    @MainThread
    fun setCurrentId (currentId : Long){
        currentPropertyIdMutableLiveData.value = currentId
    }

    val currentIdLiveData : LiveData<Long> = currentPropertyIdMutableLiveData



}