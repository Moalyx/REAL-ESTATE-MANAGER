package com.tuto.realestatemanager.repository.photo

import com.tuto.realestatemanager.database.PropertyDao
import com.tuto.realestatemanager.model.PhotoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao
): PhotoRepository {
    override suspend fun insertTemporaryPhoto(temporaryPhotoEntity: TemporaryPhotoEntity) {
        propertyDao.insertTemporaryPhoto(temporaryPhotoEntity)
    }

    override suspend fun flushTemporaryPhotos() {
        propertyDao.flushTemporaryPhotos()
    }

    override suspend fun insertPhoto(photoEntity: PhotoEntity) {
        propertyDao.insertPhoto(photoEntity)
    }

    override suspend fun upDatePhoto(photoEntity: PhotoEntity) {
        propertyDao.updatePhoto(photoEntity)
    }

    override fun getAllPhoto(): Flow<List<PhotoEntity>> {
        return propertyDao.getAllPhotos()
    }

    override fun getPhotoById(id: Long): Flow<PhotoEntity> {
        return propertyDao.getPhotoByID(id)
    }


}