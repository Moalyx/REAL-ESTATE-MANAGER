package com.tuto.realestatemanager.current_property

import kotlinx.coroutines.flow.Flow

interface CurrentPropertyIdRepository {

    val currentIdFlow: Flow<Long?>

    fun setCurrentId(currentId: Long)

}