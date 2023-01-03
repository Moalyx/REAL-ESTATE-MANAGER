package com.tuto.realestatemanager.current_property

import android.util.Log
import androidx.annotation.MainThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdIdRepositoryImpl @Inject constructor() : CurrentPropertyIdRepository {

    private val currentPropertyIdMutableStateFlow = MutableSharedFlow<Long>(1)

    @MainThread
    override fun setCurrentId(currentId: Long) {
        currentPropertyIdMutableStateFlow.tryEmit(currentId)
    }

    override val currentIdFlow: Flow<Long?> = currentPropertyIdMutableStateFlow.onEach {
        Log.d("MOP", "currentPropertyIdMutableStateFlow to $it" )
    }
}