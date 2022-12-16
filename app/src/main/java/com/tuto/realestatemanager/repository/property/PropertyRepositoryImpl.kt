package com.tuto.realestatemanager.repository.property

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tuto.realestatemanager.database.PropertyDao
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject


class PropertyRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao
) : PropertyRepository {

    private val propertiesWithPhotos = listOf(
        PropertyWithPhotosEntity(
            PropertyEntity(
                1,
                "LOFT",
                7_500_000,
                "New-York",
                2_500
            ),
            List(10) {
                PhotoEntity(
                    it.toLong(),
                    1,
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                )
            }
        )
    )
//
//
//            PropertyEntity(
//                2,
//                "PENTHOUSE",
//                5_800_000,
//                listOf(
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                ),
//                "Los Angeles",
//                2_000
//            ),
//
//            PropertyEntity(
//                1,
//                "LOFT",
//                7_500_000,
//                listOf(
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg"
//                ),
//                "New-York",
//                2_500
//            ),
//
//            PropertyEntity(
//                2,
//                "PENTHOUSE",
//                5_800_000,
//                listOf(
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                ),
//                "Los Angeles",
//                2_000
//            ),
//
//            PropertyEntity(
//                1,
//                "LOFT",
//                7_500_000,
//                listOf(
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg"
//                ),
//                "New-York",
//                2_500
//            ),
//
//            PropertyEntity(
//                2,
//                "PENTHOUSE",
//                5_800_000,
//                listOf(
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
//                ),
//                "Los Angeles",
//                2_000
//            )
//        )

    override suspend fun insertProperty(propertyEntity: PropertyEntity) {
        propertyDao.insertProperty(propertyEntity)
    }

    override suspend fun updateProperty(propertyEntity: PropertyEntity) {
        propertyDao.updateProperty(propertyEntity)
    }

    override fun getAllProperties(): Flow<List<PropertyWithPhotosEntity>> = flowOf(propertiesWithPhotos)

    override fun getPropertyById(id: Long): Flow<PropertyWithPhotosEntity> = flowOf(
        propertiesWithPhotos.find { it.propertyEntity.id == id }
    ).filterNotNull()

    override suspend fun deletePropertyById(id: Long) {
        propertyDao.deletePropertyById(id)
    }
}