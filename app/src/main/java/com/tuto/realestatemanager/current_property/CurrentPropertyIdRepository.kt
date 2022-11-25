package com.tuto.realestatemanager.current_property

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdRepository @Inject constructor() {

    private val currentPropertyIdMutableLveData = MutableLiveData<Int>()

    @MainThread
    fun setCurrentId (currentId : Int){
        currentPropertyIdMutableLveData.value = currentId
    }

    val currentIdLiveData : LiveData<Int> = currentPropertyIdMutableLveData



}