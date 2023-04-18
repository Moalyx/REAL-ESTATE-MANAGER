package com.tuto.realestatemanager.data.repository.property

import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {

    suspend fun insertProperty(propertyEntity: PropertyEntity): Long

    suspend fun updateProperty(propertyEntity: PropertyEntity)

    fun getAllPropertiesWithPhotosEntity(): Flow<List<PropertyWithPhotosEntity>>

    fun getPropertyById(id: Long): Flow<PropertyWithPhotosEntity>

    suspend fun deletePropertyById(id: Long)


}