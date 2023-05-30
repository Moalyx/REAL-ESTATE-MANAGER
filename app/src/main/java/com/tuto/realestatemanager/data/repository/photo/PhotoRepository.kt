package com.tuto.realestatemanager.data.repository.photo

import com.tuto.realestatemanager.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    suspend fun insertPhoto(photoEntity: PhotoEntity)

    suspend fun upDatePhoto(photoEntity: PhotoEntity)

    fun getAllPhoto(): Flow<List<PhotoEntity>>

    fun getPhotoById(id: Long): Flow<PhotoEntity>

    suspend fun deletePhotoById(id: Long)

    suspend fun deleteAllPropertyPhotos(propertyId: Long)

}