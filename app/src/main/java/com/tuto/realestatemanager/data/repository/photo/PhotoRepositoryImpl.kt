package com.tuto.realestatemanager.data.repository.photo

import com.tuto.realestatemanager.data.database.PropertyDao
import com.tuto.realestatemanager.model.PhotoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao
): PhotoRepository {

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

    override suspend fun deletePhotoById(id: Long) {
        propertyDao.deletePhotoById(id)
    }

    override suspend fun deleteAllPropertyPhotos(propertyId: Long) {
        propertyDao.deleteAllPropertyPhotos(propertyId)
    }

}