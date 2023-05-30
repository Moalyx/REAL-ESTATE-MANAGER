package com.tuto.realestatemanager.data.repository.property

import com.tuto.realestatemanager.data.database.PropertyDao
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PropertyRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao
) : PropertyRepository {


    //////////////////////////PROPERTY

    override suspend fun insertProperty(propertyEntity: PropertyEntity): Long {
        return propertyDao.insertProperty(propertyEntity)
    }

    override suspend fun updateProperty(propertyEntity: PropertyEntity) {
        propertyDao.updateProperty(propertyEntity)
    }


    //override fun getAllProperties(): Flow<List<PropertyWithPhotosEntity>> = flowOf(propertiesWithPhotos)

    override fun getAllPropertiesWithPhotosEntity(): Flow<List<PropertyWithPhotosEntity>> {
        return propertyDao.getAllPropertyWithPhotos()
    }

    override fun getPropertyWithPhotoById(id: Long): Flow<PropertyWithPhotosEntity> {
        return propertyDao.getPropertyWithPhotosById(id)
    }

    override fun getPropertyById(id: Long): Flow<PropertyEntity> {
        return propertyDao.getPropertyById(id)
    }


//    override fun getPropertyById(id: Long): Flow<PropertyWithPhotosEntity> = flowOf(
//        propertiesWithPhotos.find { it.propertyEntity.id == id }
//    ).filterNotNull()

    override suspend fun deletePropertyById(id: Long) {
        propertyDao.deletePropertyById(id)
    }
}