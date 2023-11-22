package com.tuto.realestatemanager.data.current_property

import android.util.Log
import androidx.annotation.MainThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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

    override val currentIdFlow: Flow<Long?> = currentPropertyIdMutableStateFlow
}