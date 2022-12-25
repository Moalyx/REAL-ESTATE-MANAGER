package com.tuto.realestatemanager.current_property

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdIdRepositoryImpl @Inject constructor() : CurrentPropertyIdRepository {

    private val currentPropertyIdMutableStateFlow = MutableStateFlow<Long?>(null)

    @MainThread
    override fun setCurrentId(currentId: Long) {
        currentPropertyIdMutableStateFlow.value = currentId
    }

    override val currentIdFlow: Flow<Long?> = currentPropertyIdMutableStateFlow
}