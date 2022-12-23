package com.tuto.realestatemanager.repository.property

import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {

    suspend fun insertProperty(propertyEntity: PropertyEntity)

    suspend fun updateProperty(propertyEntity: PropertyEntity)

    fun getAllPropertiesWithPhotosEntity(): Flow<List<PropertyWithPhotosEntity>>

    fun getPropertyById(id: Long): Flow<PropertyWithPhotosEntity>

    suspend fun deletePropertyById(id: Long)


}