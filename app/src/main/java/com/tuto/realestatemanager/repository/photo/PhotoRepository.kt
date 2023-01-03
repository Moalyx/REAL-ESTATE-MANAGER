package com.tuto.realestatemanager.repository.photo

import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.TemporaryPhotoEntity
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    suspend fun insertTemporaryPhoto(temporaryPhotoEntity: TemporaryPhotoEntity)

    suspend fun flushTemporaryPhotos()

    suspend fun insertPhoto(photoEntity: PhotoEntity)

    suspend fun upDatePhoto(photoEntity: PhotoEntity)

    fun getAllTemporaryPhoto(): Flow<List<TemporaryPhotoEntity>>

    fun getAllPhoto(): Flow<List<PhotoEntity>>

    fun getPhotoById(id: Long): Flow<PhotoEntity>

}