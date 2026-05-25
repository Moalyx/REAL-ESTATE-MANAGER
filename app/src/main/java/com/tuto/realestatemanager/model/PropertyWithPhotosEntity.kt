package com.tuto.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation

data class PropertyWithPhotosEntity(
    @Embedded
    val propertyEntity: PropertyEntity,
    @Relation(
        entity = PhotoEntity::class,
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val photos: List<PhotoEntity>
)
